package ru.hse.surkov.hw08;

public class Vector2D {

    private double x;
    private double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Vector2D convert(double width, double height) {
        return new Vector2D(x, height - y);
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D rotate(double alpha) {
        return new Vector2D(
                x * Math.cos(alpha) - y * Math.sin(alpha),
                y * Math.cos(alpha) + x * Math.sin(alpha)
        );
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(
                x + other.getX(),
                y + other.getY()
        );
    }

    public Vector2D normalize() {
        double length = getLength();
        return new Vector2D(
                x / length,
                y / length
        );
    }

    public Vector2D difference(Vector2D other) {
        return new Vector2D(
                x - other.getX(),
                y - other.getY()
        );
    }

    public Vector2D multiply(double coefficient) {
        return new Vector2D(
                x * coefficient,
                y * coefficient
        );
    }
}
