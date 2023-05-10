import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        double tf = 5; // s

        // Initial Conditions
        double x = 1;
        double v = -A*gamma / (2*m);

        double t = 0;

        double prevX = DampedOscillator.EulerIntegral(x, v, -k*x - gamma*v, -dt, m);

        for (int i = 0; t < tf; i++) {
            double f = -k*x - gamma * v;
            double auxX = x;

            x = DampedOscillator.VerletIntegral(x, f, prevX, dt, m);
            v = (x - prevX) / (2*dt);

            if (Config.isVerbose()) System.out.printf("t=%.2f -> x=%.2f ; v=%.2f\n", t, x, v);
            if (i % Config.getOutputInterval() == 0) outputWriter.write(String.format("%.2f\n%.4f %.4f\n", t, x, v));

            prevX = auxX;
            t += dt;
        }
    }

    private static void exercise2(FileWriter outputWriter, double dt) throws IOException {
        // TODO:
    }

}
