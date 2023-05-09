import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main( String[] args ) {
        Path filePath = Paths.get(Config.getOutputFile());

        try {
            // Create any non-existent directories in th output path
            Files.createDirectories(filePath.getParent());
            // Delete old output file
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Time step
        double dt = Config.getDtSimulation();

        // ------ Exercise 1 - Damped Oscillator equation ------

        // Constants
        double m = 70; // kg
        double k = Math.pow(10, 4); // N/m
        double gamma = 100; // kg/s
        double A = 1; // m

        // Initial Conditions
        double x = 1;
        double v = -A*gamma / (2*m);

        double t = 0;

        double prevX = DampedOscillator.EulerIntegral(x, v, -k*x - gamma*v, -dt, m);

        for (int i = 0; i < Config.getTimeSteps(); i++) {
            double f = -k*x - gamma * v;
            double auxX = x;

            x = DampedOscillator.VerletIntegral(x, f, prevX, dt, m);
            v = (x - prevX) / 2*dt;

            System.out.printf("t=%.2f -> x=%.2f ; v=%.2f\n", t, x, v);

            prevX = auxX;
            t += Config.getDtSimulation();
        }

    }
}
