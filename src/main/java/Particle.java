import java.util.Objects;

public class Particle {
    private double x, y, vx, vy, fx, fy, mass, radius;
    private double prevX = 0, prevY = 0;
    private final String color;

    // Only for gear
    private double x2;
    private double x3;
    private double x4;
    private double x5;
    private double y2;
    private double y3;
    private double y4;
    private double y5;

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

        // Only for gear, no particles are close to each other so all x and y from 2 to 5 are 0
        this.x2 = 0;
        this.x3 = 0;
        this.x4 = 0;
        this.x5 = 0;
        this.y2 = 0;
        this.y3 = 0;
        this.y4 = 0;
        this.y5 = 0;
    }

    public Particle(double x, double y, double vx, double vy, double fx, double fy, double mass, double radius, String color, double x2, double x3, double x4, double x5, double y2, double y3, double y4, double y5) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.fx = fx;
        this.fy = fy;
        this.mass = mass;
        this.radius = radius;
        this.color = color;
        this.x2 = x2;
        this.x3 = x3;
        this.x4 = x4;
        this.x5 = x5;
        this.y2 = y2;
        this.y3 = y3;
        this.y4 = y4;
        this.y5 = y5;
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

    public double getX2() {
        return x2;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public double getX3() {
        return x3;
    }

    public void setX3(double x3) {
        this.x3 = x3;
    }

    public double getX4() {
        return x4;
    }

    public void setX4(double x4) {
        this.x4 = x4;
    }

    public double getX5() {
        return x5;
    }

    public void setX5(double x5) {
        this.x5 = x5;
    }


    public double getY2() {
        return y2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public double getY3() {
        return y3;
    }

    public void setY3(double y3) {
        this.y3 = y3;
    }

    public double getY4() {
        return y4;
    }

    public void setY4(double y4) {
        this.y4 = y4;
    }

    public double getY5() {
        return y5;
    }

    public void setY5(double y5) {
        this.y5 = y5;
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
