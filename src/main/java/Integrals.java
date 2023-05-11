public class Integrals {

    public static double EulerPosition(double x, double v, double f, double dt, double m) {
        return x + dt*v + (Math.pow(dt, 2) / (2*m)) * f;
    }

    public static double EulerVelocity(double v, double f, double dt, double m) {
        return v + (dt / m) * f;
    }

    public static double VerletPosition(double x, double f, double prevX, double dt, double m, double gamma, double k) {
        double numerator = x * (2 - (Math.pow(dt, 2) * k) / m) + prevX * (dt*gamma / (2*m) - 1);
        double denominator = 1 + gamma*dt/(2*m);
        return numerator/denominator;
    }

    public static double BeemanPosition(double x, double v, double a, double prevA, double dt) {
        return x + v*dt + (2.0/3)*a*Math.pow(dt, 2) - (1.0/6)*prevA*Math.pow(dt, 2);
    }

}
