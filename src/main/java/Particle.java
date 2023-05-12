import java.util.Objects;

public class Particle {
    private double x, y, vx, vy, fx, fy, mass, radius;
    private double prevX = 0, prevY = 0;
    private final String color;

    public Particle(double x, double y, double vx, double vy, double fx, double fy, double mass, double radius, String color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.fx = fx;
        this.fy = fy;
        this.mass = mass;
        this.radius = radius;
        this.color = color;
    }

    /**
     *  update the invoking particle to simulate it bouncing off a vertical wall.
     */
    public void bounceX() {
        this.vx *= -1;
    }

    /**
     * update the invoking particle to simulate it bouncing off a horizontal wall
     */
    public void bounceY() {
        this.vy *= -1;
    }

    public double getX() {
        return x;
    }

    public double getPrevX() {
        return prevX;
    }

    public double getPrevY() {
        return prevY;
    }

    public double getY() {
        return y;
    }

    public double getVx() {
        return vx;
    }
    
    public double getVy() {
        return vy;
    }

    public double getFx() {
        return fx;
    }

    public double getFy() {
        return fy;
    }

    public double getRadius() {
        return radius;
    }

    public double getMass() {
        return mass;
    }

    public boolean isFixed() {
        return mass == 0;
    }

    public String getColor() {
        return color;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setPrevX(double prevX) {
        this.prevX = prevX;
    }

    public void setPrevY(double prevY) {
        this.prevY = prevY;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setFx(double fx) {
        this.fx = fx;
    }

    public void setFy(double fy) {
        this.fy = fy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return Double.compare(particle.x, x) == 0 && Double.compare(particle.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
