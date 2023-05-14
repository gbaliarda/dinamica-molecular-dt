import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class App {
    public static void main( String[] args ) throws IOException {
        Path filePath = Paths.get(Config.getOutputFile());
        // Create any non-existent directories in the output path
        Files.createDirectories(filePath.getParent());
        // Delete old output file
        Files.deleteIfExists(filePath);

        File file = new File(Config.getOutputFile());
        FileWriter outputWriter = new FileWriter(file, true);

        // Time step for the simulations
        double dt = Config.getDtSimulation();

        switch (Config.getExercise()) {
            case 1:
                exercise1(outputWriter, dt); // Damped Oscillator equation
                break;
            case 2:
                exercise2(outputWriter, dt);
                break;
            default:
                System.out.println("Invalid exercise number");
        }

        outputWriter.close();
    }

    private static void exercise1(FileWriter outputWriter, double dt) throws IOException {
        // Constants
        double m = 70; // kg
        double k = Math.pow(10, 4); // N/m
        double gamma = 100; // kg/s
        double A = 1; // m

        // Initial Conditions
        double x = 1;
        double v = -A*gamma / (2*m);

//        DampedOscillator.VerletEvolution(outputWriter, x, v, k, gamma, dt, m, A);
         DampedOscillator.BeemanEvolution(outputWriter, x, v, k, gamma, dt, m, A);
//         DampedOscillator.GearPredictorCorrectorEvolution(outputWriter, x, v, k, gamma, dt, m, A);
    }

    private static void exercise2(FileWriter outputWriter, double dt) throws IOException {
        List<Particle> particles = parseParticles();

        CollisionSystem collisionSystem = new CollisionSystem(particles, Config.getDtSimulation());

        double tf = 50; // s
        int timeSteps = 0;

        // Run the simulation
        while (collisionSystem.hasNextEvent() && collisionSystem.getTime() < tf) {
            collisionSystem.nextEvent();

            if (timeSteps++ % Config.getOutputInterval() == 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(collisionSystem.getTime()).append("\n");
                collisionSystem.getParticles().forEach(particle -> stringBuilder.append(String.format(Locale.US, "%f %f %f %f %f %f %f %s\n", particle.getX(), particle.getY(), particle.getVx(), particle.getVy(), particle.getFx(), particle.getFy(), particle.getRadius(), particle.getColor())));

                outputWriter.write(stringBuilder.toString());
            }
        }

    }

    private static List<Particle> parseParticles() throws IOException {
        List<Particle> particles = new ArrayList<>();

        Stream<String> stream = Files.lines(Paths.get(Config.getStaticFile()));

        // Load static inputs
        stream.forEach(line -> {
            String[] values = line.split(" ");
            double[] doubles = new double[values.length];
            String color = "black";
            for (int i = 0; i < values.length; i++) {
                if (i == values.length - 1)
                    color = values[i];
                else
                    doubles[i] = Double.parseDouble(values[i]);
            }
            double x = doubles[0];
            double y = doubles[1];
            double vx = doubles[2];
            double vy = doubles[3];
            double fx = doubles[4];
            double fy = doubles[5];
            double mass = doubles[6];
            double radius = doubles[7];
            Particle p = new Particle(x, y, vx, vy, fx, fy, mass, radius, color);
            p.setPrevX(Integrals.EulerPosition(x, vx, fx, -Config.getDtSimulation(), mass));
            p.setPrevY(Integrals.EulerPosition(y, vy, fy, -Config.getDtSimulation(), mass));
            particles.add(p);
        });

        return particles;
    }

}
