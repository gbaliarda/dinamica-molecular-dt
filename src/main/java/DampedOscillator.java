import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class DampedOscillator {

    public static double[] GearPredictorCorrector(double x, double v, double a, double x3, double x4, double x5, double dt, double m, double k, double gamma) {
        // Predict r(t+dt) y v(t+dt)
        double x1 = v;
        double x2 = a;

        double xp = x + x1 * dt + x2 * Math.pow(dt, 2) / factorial(2) + x3 * Math.pow(dt, 3) / factorial(3) + x4 * Math.pow(dt, 4) / factorial(4) + x5 * Math.pow(dt, 5) / factorial(5);
        double x1p = x1 + x2 * dt + x3 * Math.pow(dt, 2) / factorial(2) + x4 * Math.pow(dt, 3) / factorial(3) + x5 * Math.pow(dt, 4) / factorial(4);
        double x2p = x2 + x3 * dt + x4 * Math.pow(dt, 2) / factorial(2) + x5 * Math.pow(dt, 3) / factorial(3);
        double x3p = x3 + x4 * dt + x5 * Math.pow(dt, 2) / factorial(2);
        double x4p = x4 + x5 * dt;
        double x5p = x5;

        // Calculate a(t+dt) with f, xp(t) and vp(t)?
        double deltaA = (-k*xp - gamma*x1p) / m - x2p;
        double deltaR2 = deltaA * Math.pow(dt, 2) / factorial(2);

        // Correct x(t+dt) and v(t+dt)
        double[] gearCoefficients = {3/16.0, 251/360.0, 1, 11/18.0, 1/6.0, 1/60.0};
        double xc = xp + gearCoefficients[0] * deltaR2;
        double vc = x1p + gearCoefficients[1] * deltaR2 / dt;
        double ac = x2p + gearCoefficients[2] * deltaR2 * factorial(2) / Math.pow(dt, 2);
        double x3c = x3p + gearCoefficients[3] * deltaR2 * factorial(3) / Math.pow(dt, 3);
        double x4c = x4p + gearCoefficients[4] * deltaR2 * factorial(4) / Math.pow(dt, 4);
        double x5c = x5p + gearCoefficients[5] * deltaR2 * factorial(5) / Math.pow(dt, 5);

        return new double[]{xc, vc, ac, x3c, x4c, x5c};
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

    public static void VerletEvolution(FileWriter outputWriter, double x, double v, double k, double gamma, double dt, double m, double A) throws IOException {
        double t = 0, tf = 5;

        double prevX = Integrals.EulerPosition(x, v, -k*x - gamma*v, -dt, m);
        double difference = 0;
        int i;
        for (i = 0; t < tf; i++) {
            double f = -k*x - gamma * v;
            double auxX = x;

            x = DampedOscillator.VerletPosition(x, f, prevX, dt, m, gamma, k);
            v = (x - prevX) / (2*dt);

            if (Config.isVerbose()) System.out.printf("t=%.2f -> x=%.2f ; v=%.2f\n", t, x, v);
            if (i % Config.getOutputInterval() == 0) outputWriter.write(String.format(Locale.US, "%.2f\n%.4f %.4f\n", t, x, v));

            prevX = auxX;
            t += dt;
            difference += Math.pow(analyticSolution(A, gamma, m, t, k) - x, 2);
        }
        difference += Math.pow(analyticSolution(A, gamma, m, t, k) - x, 2);
        double error = difference/i;
        System.out.println(error);
    }

    private static double analyticSolution(double A, double gamma, double m, double t, double k) {
        return A*Math.exp(-t*(gamma/(2*m)))*Math.cos(Math.pow(k/m - Math.pow(gamma, 2)/(4*Math.pow(m,2)),0.5)*t);
    }

    public static double VerletPosition(double x, double f, double prevX, double dt, double m, double gamma, double k) {
        double numerator = x * (2 - (Math.pow(dt, 2) * k) / m) + prevX * (dt*gamma / (2*m) - 1);
        double denominator = 1 + gamma*dt/(2*m);
        return numerator/denominator;
    }

    public static void BeemanEvolution(FileWriter outputWriter, double x, double v, double k, double gamma, double dt, double m, double A) throws IOException {
        double t = 0, tf = 5;
        double f = -k*x - gamma*v;
        double prevX = Integrals.EulerPosition(x, v, f, -dt, m);
        double prevV = Integrals.EulerVelocity(v, f, -dt, m);
        double prevA = (-k*prevX - gamma*prevV)/m;
        double difference = 0;
        int i;

        for (i = 0; t < tf; i++) {

            f = -k*x - gamma * v;
            double a = f/m;

            x = Integrals.BeemanPosition(x, v, a, prevA, dt);
            v = DampedOscillator.BeemanVelocity(x, v, a, prevA, dt, m, gamma, k);

            if (Config.isVerbose()) System.out.printf("t=%.2f -> x=%.2f ; v=%.2f\n", t, x, v);
            if (i % Config.getOutputInterval() == 0) outputWriter.write(String.format(Locale.US, "%.2f\n%.4f %.4f\n", t, x, v));

            prevA = a;
            t += dt;
            difference += Math.pow(analyticSolution(A, gamma, m, t, k) - x, 2);
        }
        double error = difference/i;
        System.out.println(error);
    }

    public static void GearPredictorCorrectorEvolution(FileWriter outputWriter, double x, double v, double k, double gamma, double dt, double m, double A) throws IOException {
        double t = 0, tf = 5;
        double difference = 0;
        int i;

        double a = (-k*x-gamma*v)/m;
        double x3 = (-k*v - gamma*a)/m;
        double x4 = (-k*a - gamma*x3)/m;
        double x5 = (-k*x3 - gamma*x4)/m;

        for (i = 0; t < tf; i++) {

            double[] res = GearPredictorCorrector(x, v, a, x3, x4, x5, dt, m, k, gamma);
            x = res[0];
            v = res[1];
            a = res[2];
            x3 = res[3];
            x4 = res[4];
            x5 = res[5];

            if (Config.isVerbose()) System.out.printf("t=%.2f -> x=%.2f ; v=%.2f\n", t, x, v);
            if (i % Config.getOutputInterval() == 0) outputWriter.write(String.format(Locale.US, "%.2f\n%.4f %.4f\n", t, x, v));

            t += dt;
            difference += Math.pow(analyticSolution(A, gamma, m, t, k) - x, 2);
        }
        double error = difference/i;
        System.out.println(error);
    }
}
