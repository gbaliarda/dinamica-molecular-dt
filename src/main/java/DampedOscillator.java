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

    public static double BeemanIntegral(double x, double v, double a, double prev_a, double dt, double m) {
        return x + v*dt + (2.0/3)*a*Math.pow(dt, 2) - (1.0/6)*prev_a*Math.pow(dt, 2);
    }

    public static double VerletIntegral(double x, double f, double prevX, double dt, double m) {
        return 2*x - prevX + (Math.pow(dt, 2) / m) * f;
    }

    public static double EulerIntegral(double x, double v, double f, double dt, double m) {
        return x + dt*v + (Math.pow(dt, 2) / (2*m)) * f;
    }

    private static int factorial(int n) {
        if (n == 0)
            return 1;
        return n * factorial(n-1);
    }

}
