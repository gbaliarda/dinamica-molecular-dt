import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class DampedOscillator {

    public static double GearPredictorCorrectorIntegral(double x, double v, double a, double dt, double m, double k, double gamma) {
        // Predigo r(t+dt) y v(t+dt)
        // double x1 = v;
        double x2 = a;
        double x3 = -k/m * v;
        double x4 = -k/m * x2;
        double x5 = -k/m * x3;

        double xp = x + v * dt + x2 * Math.pow(dt, 2) / factorial(2) + x3 * Math.pow(dt, 3) / factorial(3) + x4 * Math.pow(dt, 4) / factorial(4) + x5 * Math.pow(dt, 5) / factorial(5);
        double vp = v + x2 * dt + x3 * Math.pow(dt, 2) / factorial(2) + x4 * Math.pow(dt, 3) / factorial(3) + x5 * Math.pow(dt, 4) / factorial(4);
        double x2p = x2 + x3 * dt + x4 * Math.pow(dt, 2) / factorial(2) + x5 * Math.pow(dt, 3) / factorial(3);
//        double x3p = x3 + x4 * dt + x5 * Math.pow(dt, 2) / factorial(2);
//        double x4p = x4 + x5 * dt;
//        double x5p = x5;

        // Evaluo la fuerza en r y v anterior para calcular a(t+dt)
        double delta_a = (-k*xp - gamma*vp) / m - x2p;
        double deltaR2 = delta_a * Math.pow(dt, 2) / factorial(2);

        // Vuelvo a calcular r(t+dt) pero usando a(t+dt) y v(t+dt)
        double[] gearCoefficients = {3/16.0, 251/360.0, 1, 11/18.0, 1/6.0, 1/60.0};
        double xc = xp + gearCoefficients[0] * deltaR2;
//        double vc = vp + gearCoefficients[1] * deltaR2 * dt;
//        double x2c = x2p + gearCoefficients[2] * deltaR2 * Math.pow(dt, 2) / factorial(2);
//        double x3c = x3p + gearCoefficients[3] * deltaR2 * Math.pow(dt, 3) / factorial(3);
//        double x4c = x4p + gearCoefficients[4] * deltaR2 * Math.pow(dt, 4) / factorial(4);
//        double x5c = x5p + gearCoefficients[5] * deltaR2 * Math.pow(dt, 5) / factorial(5);

        return xc;
    }

    public static double BeemanPosition(double x, double v, double a, double prevA, double dt) {
        return x + v*dt + (2.0/3)*a*Math.pow(dt, 2) - (1.0/6)*prevA*Math.pow(dt, 2);
    }

    public static double BeemanVelocity(double nextX, double v, double a, double prevA, double dt, double m, double gamma, double k) {
//        return v + (1.0/3)*next_a*dt + (5.0/6)*a*dt - (1.0/6)*prev_a*dt;
        double denominator = 1 + (dt * gamma) / (3*m);
        double numerator = v - (dt * k * nextX)/(3*m) + (5.0/6)*a*dt - (1.0/6)*prevA*dt;
        return numerator/denominator;
    }

    public static double VerletIntegral(double x, double f, double prevX, double dt, double m) {
        return 2*x - prevX + (Math.pow(dt, 2) / m) * f;
    }

    public static double EulerPosition(double x, double v, double f, double dt, double m) {
        return x + dt*v + (Math.pow(dt, 2) / (2*m)) * f;
    }

    public static double EulerVelocity(double v, double f, double dt, double m) {
        return v + (dt / m) * f;
    }

    private static int factorial(int n) {
        if (n == 0)
            return 1;
        return n * factorial(n-1);
    }

    public static void VerletEvolution(FileWriter outputWriter, double x, double v, double k, double gamma, double dt, double m) throws IOException {
        double t = 0, tf = 5;

        double prevX = DampedOscillator.EulerPosition(x, v, -k*x - gamma*v, -dt, m);

        for (int i = 0; t < tf; i++) {
            double f = -k*x - gamma * v;
            double auxX = x;

            x = DampedOscillator.VerletIntegral(x, f, prevX, dt, m);
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
        double prevX = DampedOscillator.EulerPosition(x, v, f, -dt, m);
        double prevV = DampedOscillator.EulerVelocity(v, f, -dt, m);
        double prevA = (-k*prevX - gamma*prevV)/m;

        for (int i = 0; t < tf; i++) {
            f = -k*x - gamma * v;
            double a = f/m;

            x = DampedOscillator.BeemanPosition(x, v, a, prevA, dt);
            v = DampedOscillator.BeemanVelocity(x, v, a, prevA, dt, m, gamma, k);

            if (Config.isVerbose()) System.out.printf("t=%.2f -> x=%.2f ; v=%.2f\n", t, x, v);
            if (i % Config.getOutputInterval() == 0) outputWriter.write(String.format(Locale.US, "%.2f\n%.4f %.4f\n", t, x, v));

            prevA = a;
            t += dt;
        }
    }
}
