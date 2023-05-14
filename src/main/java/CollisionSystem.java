import java.util.*;
import java.util.stream.Collectors;

public class CollisionSystem {
    private List<Particle> particles;
    private final int TABLE_HOLES = 6;
    private double t;        // s
    private final double dt; // s

    public CollisionSystem(List<Particle> particles, double dt) {
        this.particles = particles;
        this.dt = dt;
    }

    public void nextEvent() {
        // Update each particle position and velocity
        if (Config.getIntegration().equals("gear"))
            particles = gearNextState();
        else {
            for (Particle p : particles) {
                if (p.isFixed()) continue;

                double newX = nextPosition(p.getX(), p.getVx(), p.getFx(), p.getMass(), p.getPrevX());
                double newY = nextPosition(p.getY(), p.getVy(), p.getFy(), p.getMass(), p.getPrevY());
                double newVx = nextVelocity(newX, p.getPrevX(), p.getVx(), p.getFx(), p.getMass());
                double newVy = nextVelocity(newY, p.getPrevY(), p.getVy(), p.getFy(), p.getMass());
                p.setX(newX);
                p.setY(newY);
                p.setVx(newVx);
                p.setVy(newVy);
            }
        }

        Set<Particle> toRemove = new HashSet<>();

        // Update forces on each particle
        for (Particle p : particles) {
            double[] force = getCollisionForce(p, particles, toRemove); // g * cm/s^2
            p.setFx(force[0]);
            p.setFy(force[1]);
        }

        // Remove particles that entered a hole
        particles = particles.stream().filter(particle -> !toRemove.contains(particle)).collect(Collectors.toList());

        // Update simulation time
        t += dt;
    }

    private double nextPosition(double r, double v, double f, double mass, double prevR) {
        switch (Config.getIntegration()) {
            case "euler":
                return Integrals.EulerPosition(r, v, f, dt, mass);
            case "verlet":
                return Integrals.VerletPosition(r, prevR, dt, mass, f);
            default:
                throw new RuntimeException("Invalid integration method");
        }
    }

    private double nextVelocity(double newR, double prevR, double v, double f, double mass) {
        switch (Config.getIntegration()) {
            case "euler":
                return Integrals.EulerVelocity(v, f, dt, mass);
            case "verlet":
                return Integrals.VerletVelocity(newR, prevR, dt);
            default:
                throw new RuntimeException("Invalid integration method");
        }
    }

    private double[] getCollisionForce(Particle p, List<Particle> particles, Set<Particle> toRemove) {
        double[] force = {0, 0};            // {fx, fy}: g * cm/s^2
        double K = Math.pow(10, 4) * Math.pow(10, 3); // g / s^2

        // Check collision with other particles
        for (Particle other : particles) {
            if (other.equals(p)) continue;

            double xDistance = Math.abs(other.getX() - p.getX());
            double yDistance = Math.abs(other.getY() - p.getY());
            double radiusSum = p.getRadius() + other.getRadius();

            if (Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2)) <= radiusSum) {
                if (other.isFixed()) {
                    toRemove.add(p);
                    break;
                }

                if (xDistance != 0)
                    force[0] += K * (xDistance - radiusSum) * ((other.getX() - p.getX()) / xDistance);
                else
                    force[0] = 0;

                if (yDistance != 0)
                    force[1] += K * (yDistance - radiusSum) * ((other.getY() - p.getY()) / yDistance);
                else
                    force[1] = 0;
            }
        }

        boolean collidesLeftWall = p.getX() - p.getRadius() <= 0;
        boolean collidesBottomWall = p.getY() - p.getRadius() <= 0;

        // Check collision with vertical walls
        if (collidesLeftWall || p.getX() + p.getRadius() >= Config.getTableWidth()) {
            double radiusInsideWall = collidesLeftWall ? p.getRadius() - p.getX() : p.getRadius() - (Config.getTableWidth() - p.getX());
            double df = K * radiusInsideWall;
            df *= collidesLeftWall ? 1 : -1;
            force[0] += df;
        }

        // Check collision with horizontal walls
        if (collidesBottomWall || p.getY() + p.getRadius() >= Config.getTableHeight()) {
            double radiusInsideWall = collidesBottomWall ? p.getRadius() - p.getY() : p.getRadius() - (Config.getTableHeight() - p.getY());
            double df = K * radiusInsideWall;
            df *= collidesBottomWall ? 1 : -1;
            force[1] += df;
        }

        return force;
    }

    private List<Particle> gearNextState() {
        // Create new list of particles because each particle needs to be predicted and corrected
        // with the original particles. e.g. Particle 1 predicts in t+dt, check forces
        // with particle 2 with t values and correct particle 1 and save it. Then particle 2 does the same with
        // particle 1 with t values.
        List<Particle> newParticles = new ArrayList<>();
        for (Particle p : particles) {
            if (p.isFixed()) {
                newParticles.add(p);
                continue;
            }
            Particle newParticle = new Particle(p.getX(), p.getY(), p.getVx(), p.getVy(), p.getFx(), p.getFy(), p.getMass(), p.getRadius(), p.getColor(), p.getX2(), p.getX3(), p.getX4(), p.getX5(), p.getY2(), p.getY3(), p.getY4(), p.getY5());
            // Predict new particle derivatives
            double[] xPredicts = getPredictions(newParticle.getX(), p.getVx(), p.getX2(), p.getX3(), p.getX4(), p.getX5());
            double[] yPredicts = getPredictions(newParticle.getY(), p.getVy(), p.getY2(), p.getY3(), p.getY4(), p.getY5());

            // Calculate a(t+dt) using particle p with predicted values in t+dt and the rest in time t
            newParticle.setX(xPredicts[0]);
            newParticle.setY(yPredicts[0]);
            newParticle.setVx(xPredicts[1]);
            newParticle.setVy(yPredicts[1]);
            Set<Particle> toRemove = new HashSet<>();
            double[] forces = getCollisionForce(newParticle, particles.stream().filter(o -> !o.equals(p)).collect(Collectors.toList()), toRemove);
            newParticle.setFx(forces[0]);
            newParticle.setFy(forces[1]);
            double deltaAx = newParticle.getFx()/newParticle.getMass() - xPredicts[2];
            double deltaAy = newParticle.getFy()/newParticle.getMass() - yPredicts[2];
            double deltaR2x = deltaAx * Math.pow(dt, 2) / factorial(2);
            double deltaR2y = deltaAy * Math.pow(dt, 2) / factorial(2);

            // Correct X and Y, note that this time f only depends on r so first coefficient is 3/20
            double[] gearCoefficients = {3/20.0, 251/360.0, 1, 11/18.0, 1/6.0, 1/60.0};
            newParticle.setX(xPredicts[0] + gearCoefficients[0] * deltaR2x);
            newParticle.setVx(xPredicts[1] + gearCoefficients[1] * deltaR2x / dt);
            newParticle.setX2(xPredicts[2] + gearCoefficients[2] * deltaR2x * factorial(2) / Math.pow(dt, 2));
            newParticle.setX3(xPredicts[3] + gearCoefficients[3] * deltaR2x * factorial(3) / Math.pow(dt, 3));
            newParticle.setX4(xPredicts[4] + gearCoefficients[4] * deltaR2x * factorial(4) / Math.pow(dt, 4));
            newParticle.setX5(xPredicts[5] + gearCoefficients[5] * deltaR2x * factorial(5) / Math.pow(dt, 5));
            newParticle.setY(yPredicts[0] + gearCoefficients[0] * deltaR2y);
            newParticle.setVy(yPredicts[1] + gearCoefficients[1] * deltaR2y / dt);
            newParticle.setY2(yPredicts[2] + gearCoefficients[2] * deltaR2y * factorial(2) / Math.pow(dt, 2));
            newParticle.setY3(yPredicts[3] + gearCoefficients[3] * deltaR2y * factorial(3) / Math.pow(dt, 3));
            newParticle.setY4(yPredicts[4] + gearCoefficients[4] * deltaR2y * factorial(4) / Math.pow(dt, 4));
            newParticle.setY5(yPredicts[5] + gearCoefficients[5] * deltaR2y * factorial(5) / Math.pow(dt, 5));

            newParticles.add(newParticle);
        }

        return newParticles;
    }

    private double[] getPredictions(double r, double r1, double r2, double r3, double r4, double r5) {
        double rp = r + r1 * dt + r2 * Math.pow(dt, 2) / factorial(2) + r3 * Math.pow(dt, 3) / factorial(3) + r4 * Math.pow(dt, 4) / factorial(4) + r5 * Math.pow(dt, 5) / factorial(5);
        double r1p = r1 + r2 * dt + r3 * Math.pow(dt, 2) / factorial(2) + r4 * Math.pow(dt, 3) / factorial(3) + r5 * Math.pow(dt, 4) / factorial(4);
        double r2p = r2 + r3 * dt + r4 * Math.pow(dt, 2) / factorial(2) + r5 * Math.pow(dt, 3) / factorial(3);
        double r3p = r3 + r4 * dt + r5 * Math.pow(dt, 2) / factorial(2);
        double r4p = r4 + r5 * dt;
        double r5p = r5;

        return new double[]{rp, r1p, r2p, r3p, r4p, r5p};
    }

    public boolean hasNextEvent() {
        return this.particles.size() > TABLE_HOLES;
    }

    public double getTime() { return t;}

    public List<Particle> getParticles() {
        return particles;
    }

    private static int factorial(int n) {
        if (n == 0)
            return 1;
        return n * factorial(n-1);
    }
}
