import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class Config {
    private static double dtSimulation, dtOutput;
    private static int timeSteps;
    private static boolean verbose;
    private static String staticFile, outputFile;

    static {
        try {
            Toml toml = new Toml().read(new File("config.toml"));

            dtSimulation = toml.getDouble("simulation.dt_simulation");
            dtOutput = toml.getDouble("simulation.dt_output");
            timeSteps = toml.getLong("simulation.time_steps").intValue();
            verbose = toml.getBoolean("simulation.verbose");
            staticFile = toml.getString("files.staticInput");
            outputFile = toml.getString("files.output");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double getDtSimulation() {
        return dtSimulation;
    }

    public static double getDtOutput() {
        return dtOutput;
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
