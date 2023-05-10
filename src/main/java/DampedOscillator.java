import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class DampedOscillator {

    public static double[] GearPredictorCorrector(double x, double v, double a, double dt, double m, double k, double gamma) {
        // Predict r(t+dt) y v(t+dt)
        double x1 = v;
        double x2 = a;
        double x3 = -k/m * x1;
        double x4 = -k/m * x2;
        double x5 = -k/m * x3;

        double xp = x + x1 * dt + x2 * Math.pow(dt, 2) / factorial(2) + x3 * Math.pow(dt, 3) / factorial(3) + x4 * Math.pow(dt, 4) / factorial(4) + x5 * Math.pow(dt, 5) / factorial(5);
        double x1p = x1 + x2 * dt + x3 * Math.pow(dt, 2) / factorial(2) + x4 * Math.pow(dt, 3) / factorial(3) + x5 * Math.pow(dt, 4) / factorial(4);
        double x2p = x2 + x3 * dt + x4 * Math.pow(dt, 2) / factorial(2) + x5 * Math.pow(dt, 3) / factorial(3);

        // Calculate a(t+dt) with f, xp(t) and vp(t)?
        double deltaA = (-k*xp - gamma*x1p) / m - x2p;
        double deltaR2 = deltaA * Math.pow(dt, 2) / factorial(2);

        // Correct x(t+dt) and v(t+dt)
        double[] gearCoefficients = {3/16.0, 251/360.0, 1, 11/18.0, 1/6.0, 1/60.0};
        double xc = xp + gearCoefficients[0] * deltaR2;
        double vc = x1p + gearCoefficients[1] * deltaR2 * dt;

        return new double[]{xc, vc};
    }

    public static double BeemanVelocity(double nextX, double v, double a, double prevA, double dt, double m, double gamma, double k) {
        double denominator = 1 + (dt * gamma) / (3*m);
        double numerator = v - (dt * k * nextX)/(3*m) + (5.0/6)*a*dt - (1.0/6)*prevA*dt;
        return numerator/denominator;
    }

    private static int factorial(int n) {
        if (n == 0)
            return 1;
        return n * factorial(n-1);
    }

    public static void VerletEvolution(FileWriter outputWriter, double x, double v, double k, double gamma, double dt, double m) throws IOException {
        double t = 0, tf = 5;

        double prevX = Integrals.EulerPosition(x, v, -k*x - gamma*v, -dt, m);

        for (int i = 0; t < tf; i++) {
            double f = -k*x - gamma * v;
            double auxX = x;

            x = Integrals.VerletPosition(x, f, prevX, dt, m);
            v = (x - prevX) / (2*dt);

            if (Config.isVerbose()) System.out.printf("t=%.2f -> x=%.2f ; v=%.2f\n", t, x, v);
            if (i % Config.getOutputInterval() == 0) outputWriter.write(String.format(Locale.US, "%.2f\n%.4f %.4f\n", t, x, v));

            prevX = auxX;
            t += dt;
        }
    }

    public static void BeemanEvolution(FileWriter outputWriter, double x, double v, double k, double gamma, double dt, double m) throws IOException {
        double t = 0, tf = 5;
        double f = -k*x - gamma*v;
        double prevX = Integrals.EulerPosition(x, v, f, -dt, m);
        double prevV = Integrals.EulerVelocity(v, f, -dt, m);
        double prevA = (-k*prevX - gamma*prevV)/m;

        for (int i = 0; t < tf; i++) {
            f = -k*x - gamma * v;
            double a = f/m;

            x = Integrals.BeemanPosition(x, v, a, prevA, dt);
            v = DampedOscillator.BeemanVelocity(x, v, a, prevA, dt, m, gamma, k);

            if (Config.isVerbose()) System.out.printf("t=%.2f -> x=%.2f ; v=%.2f\n", t, x, v);
            if (i % Config.getOutputInterval() == 0) outputWriter.write(String.format(Locale.US, "%.2f\n%.4f %.4f\n", t, x, v));

            prevA = a;
            t += dt;
        }
    }

    public static void GearPredictorCorrectorEvolution(FileWriter outputWriter, double x, double v, double k, double gamma, double dt, double m) throws IOException {
        double t = 0, tf = 5;

        for (int i = 0; t < tf; i++) {
            double a = (-k*x-v*gamma)/m;

            double[] res = GearPredictorCorrector(x, v, a, dt, m, k, gamma);
            x = res[0];
            v = res[1];

            if (Config.isVerbose()) System.out.printf("t=%.2f -> x=%.2f ; v=%.2f\n", t, x, v);
            if (i % Config.getOutputInterval() == 0) outputWriter.write(String.format(Locale.US, "%.2f\n%.4f %.4f\n", t, x, v));

            t += dt;
        }
    }
}
