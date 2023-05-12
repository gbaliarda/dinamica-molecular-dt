import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class Config {
    private static int tableWidth, tableHeight;
    private static double dtSimulation;
    private static int outputInterval, exercise;
    private static boolean verbose;
    private static String staticFile, outputFile, integration;

    static {
        try {
            Toml toml = new Toml().read(new File("config.toml"));

            tableWidth = toml.getLong("simulation.tableWidth").intValue();
            tableHeight = toml.getLong("simulation.tableHeight").intValue();
            dtSimulation = toml.getDouble("simulation.dtSimulation");
            outputInterval = toml.getLong("simulation.outputInterval").intValue();
            exercise = toml.getLong("simulation.exercise").intValue();
            verbose = toml.getBoolean("simulation.verbose");
            integration = toml.getString("simulation.integration");
            staticFile = toml.getString("files.staticInput");
            outputFile = toml.getString("files.output");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getTableWidth() {
        return tableWidth;
    }

    public static int getTableHeight() {
        return tableHeight;
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

    public static boolean isVerbose() {
        return verbose;
    }

    public static String getStaticFile() { return staticFile; }

    public static String getOutputFile() {
        return outputFile;
    }

    public static String getIntegration() {
        return integration;
    }
}
