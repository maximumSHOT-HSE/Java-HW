package ru.hse.surkov.hw08;

public class Cannon {

    private double width;
    private double height;
    private double angle;
    private Vector2D base;

    public Cannon(double width, double height, double angle, Vector2D base) {
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.base = base;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public Vector2D getBase() {
        return base;
    }

    public void setBase(Vector2D base) {
        this.base = base;
    }
}
