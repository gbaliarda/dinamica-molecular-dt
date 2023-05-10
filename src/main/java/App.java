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

        // Initial Conditions
        double x = 1;
        double v = -A*gamma / (2*m);

//        DampedOscillator.VerletEvolution(outputWriter, x, v, k, gamma, dt, m);
//        DampedOscillator.BeemanEvolution(outputWriter, x, v, k, gamma, dt, m);
        DampedOscillator.GearPredictorCorrectorEvolution(outputWriter, x, v, k, gamma, dt, m);
    }

    private static void exercise2(FileWriter outputWriter, double dt) throws IOException {
        // TODO:
    }

}
