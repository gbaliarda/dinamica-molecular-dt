import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class Config {
    private static double dtSimulation;
    private static int timeSteps, outputInterval, exercise;
    private static boolean verbose;
    private static String staticFile, outputFile;

    static {
        try {
            Toml toml = new Toml().read(new File("config.toml"));

            dtSimulation = toml.getDouble("simulation.dtSimulation");
            outputInterval = toml.getLong("simulation.outputInterval").intValue();
            timeSteps = toml.getLong("simulation.timeSteps").intValue();
            exercise = toml.getLong("simulation.exercise").intValue();
            verbose = toml.getBoolean("simulation.verbose");
            staticFile = toml.getString("files.staticInput");
            outputFile = toml.getString("files.output");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getExercise() {
        return exercise;
    }

    public static double getDtSimulation() {
        return dtSimulation;
    }

    public static int getOutputInterval() {
        return outputInterval;
    }

    public static int getTimeSteps() {
        return timeSteps;
    }

    public static boolean isVerbose() {
        return verbose;
    }

    public static String getStaticFile() { return staticFile; }

    public static String getOutputFile() {
        return outputFile;
    }
}
