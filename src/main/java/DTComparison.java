import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public class DTComparison {
    public static void main(String[] args) throws IOException {
        Path kDifferencePath = Paths.get("./out/k_difference5.txt");
        // Create any non-existent directories in the output path
        Files.createDirectories(kDifferencePath.getParent());
        // Delete old output file
        Files.deleteIfExists(kDifferencePath);

        File kDifference = new File("./out/k_difference5.txt");
        FileWriter outputWriter = new FileWriter(kDifference, true);

        List<Particle> particles = App.parseParticles();
        double dt = Math.pow(10,-5);
        CollisionSystem collisionSystem1 = new CollisionSystem(particles, dt);
        CollisionSystem collisionSystem2 = new CollisionSystem(particles, dt/10);
        double now = System.currentTimeMillis();
        double tf = 10; // s
        double comparisonTime = 0;
        while (comparisonTime < tf) {
            collisionSystem1.nextEvent();
            for (int i = 0; i < 10; i++)
                collisionSystem2.nextEvent();
            writeToOutputFile(collisionSystem1, collisionSystem2, outputWriter);
            comparisonTime += dt;
        }
        outputWriter.close();
        System.out.printf(Locale.US, "Took %f seconds\n%n", (System.currentTimeMillis()-now)/1000);
    }

    private static void writeToOutputFile(CollisionSystem collisionSystem1, CollisionSystem collisionSystem2, FileWriter outputWriter) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(Locale.US, "%f\n", collisionSystem1.getTime()));
        double result = 0;
        for (int i = 0; i < collisionSystem1.getParticles().size(); i++) {
            Particle p1 = collisionSystem1.getParticles().get(i);
            Particle p2 = collisionSystem2.getParticles().get(i);
            result += Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
        }
        stringBuilder.append(String.format(Locale.US, "%f\n",result));
        outputWriter.write(stringBuilder.toString());
    }
}
