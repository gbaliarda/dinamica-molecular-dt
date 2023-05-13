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
            double[] force = getCollisionForce(p, toRemove);
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

    private double[] getCollisionForce(Particle p, Set<Particle> toRemove) {
        double[] force = {0, 0};    // {fx, fy}
        double K = Math.pow(10, 7); // g/s^2

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
            force[0] += K * radiusInsideWall;
            force[0] *= collidesLeftWall ? 1 : -1;
        }

        // Check collision with horizontal walls
        if (collidesBottomWall || p.getY() + p.getRadius() >= Config.getTableHeight()) {
            double radiusInsideWall = collidesBottomWall ? p.getRadius() - p.getY() : p.getRadius() - (Config.getTableHeight() - p.getY());
            force[1] += K * radiusInsideWall;
            force[1] *= collidesBottomWall ? 1 : -1;
        }

        return force;
    }

    public boolean hasNextEvent() {
        return this.particles.size() > TABLE_HOLES;
    }

    public double getTime() { return t;}

    public List<Particle> getParticles() {
        return particles;
    }
}
