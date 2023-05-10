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

            double newX = Integrals.EulerPosition(p.getX(), p.getVx(), p.getFx(), dt, p.getMass());
            double newY = Integrals.EulerPosition(p.getY(), p.getVy(), p.getFy(), dt, p.getMass());
            double newVx = Integrals.EulerVelocity(p.getVx(), p.getFx(), dt, p.getMass());
            double newVy = Integrals.EulerVelocity(p.getVy(), p.getFy(), dt, p.getMass());
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

        // Check collision with horizontal and vertical walls
        for (Particle p : particles) {
            // TODO: check if this is correct
            if (p.getX() - p.getRadius() <= 0 || p.getX() + p.getRadius() >= Config.getTableWidth())
                p.bounceX();
            if (p.getY() - p.getRadius() <= 0 || p.getY() + p.getRadius() >= Config.getTableHeight())
                p.bounceY();
        }

        // Update simulation time
        t += dt;
    }

    private double[] getForceDelta(Particle p, Set<Particle> toRemove) {
        double[] df = {0, 0};
        double K = Math.pow(10, 4); // N/m

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
