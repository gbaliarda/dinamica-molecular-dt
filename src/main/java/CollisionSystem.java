import java.util.*;
import java.util.stream.Collectors;

public class CollisionSystem {
    private List<Particle> particles;
    private final int TABLE_HOLES = 6;
    private double t; // in seconds
    private double dt; // s

    public CollisionSystem(List<Particle> particles, double dt) {
        this.particles = particles;
        this.dt = dt;
    }

    public void nextEvent() {
        // Update each particle position and velocity
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

        Set<Particle> toRemove = new HashSet<>();

        // Update forces on each particle
        for (Particle p : particles) {
            double[] df = getForceDelta(p, toRemove);
            p.setFx(df[0]);
            p.setFy(df[1]);
        }

        // Remove particles that entered a hole
        particles = particles.stream().filter(particle -> !toRemove.contains(particle)).collect(Collectors.toList());

//        // Check collision with horizontal and vertical walls
//        for (Particle p : particles) {
//            if (p.getX() - p.getRadius() <= 0 || p.getX() + p.getRadius() >= Config.getTableWidth())
//                p.bounceX();
//            if (p.getY() - p.getRadius() <= 0 || p.getY() + p.getRadius() >= Config.getTableHeight())
//                p.bounceY();
//        }

        // Update simulation time
        t += dt;
    }

    private double nextPosition(double r, double v, double f, double mass, double prevR) {
        switch (Config.getIntegration()) {
            case "euler":
                return Integrals.EulerPosition(r, v, f, dt, mass);
            case "verlet":
                return Integrals.VerletPosition(r, prevR, dt, mass, f);
            case "gear":
                return 0;
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

    private double[] getForceDelta(Particle p, Set<Particle> toRemove) {
        double[] df = {0, 0};
        double K = Math.pow(10, 7); // g/s^2
        double gamma = 100000; // g/s

        for (Particle other : particles) {
            if (other.equals(p))
                continue;

            double xDistance = Math.abs(other.getX() - p.getX());
            double yDistance = Math.abs(other.getY() - p.getY());
            double radiusSum = p.getRadius() + other.getRadius();

            if (Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2)) <= radiusSum) {
                if (other.isFixed()) {
                    toRemove.add(p);
                    break;
                }
                // TODO: check if this is correct
                if (xDistance != 0)
                    df[0] += K * (xDistance - radiusSum) * ((other.getX() - p.getX()) / xDistance);
                else
                    df[0] = 0;

                if (yDistance != 0)
                    df[1] += K * (yDistance - radiusSum) * ((other.getY() - p.getY()) / yDistance);
                else
                    df[1] = 0;
            }
        }

        // Check elastic forces with horizontal and vertical walls
        if (p.getX() - p.getRadius() <= 0 || p.getX() + p.getRadius() >= Config.getTableWidth())
            df[0] += -K*p.getX()-gamma*p.getVx();

        if (p.getY() - p.getRadius() <= 0 || p.getY() + p.getRadius() >= Config.getTableHeight())
            df[1] += -K*p.getY()-gamma*p.getVy();

        return df;
    }

    public boolean hasNextEvent() {
        return this.particles.size() > TABLE_HOLES;
    }

    public double getTime() { return t;}

    public List<Particle> getParticles() {
        return particles;
    }
}
