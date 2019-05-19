package ru.hse.surkov.hw08;

import org.jetbrains.annotations.NotNull;

/**
 * Two-dimensional vector.
 */
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

    public double getY() {
        return y;
    }

    /**
     * Finds the length of the vector.
     * */
    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Rotates the vector by given angle.
     * */
    @NotNull public Vector2D rotate(double alpha) {
        return new Vector2D(
                x * Math.cos(alpha) - y * Math.sin(alpha),
                y * Math.cos(alpha) + x * Math.sin(alpha)
        );
    }

    /**
     * Calculates the sum of this vector
     * and given vector.
     * */
    @NotNull public Vector2D add(Vector2D other) {
        return new Vector2D(
                x + other.getX(),
                y + other.getY()
        );
    }

    /**
     * Makes the length of the vector
     * equal to one by dividing the vector
     * by its length.
     * */
    @NotNull public Vector2D normalize() {
        double length = getLength();
        return new Vector2D(
                x / length,
                y / length
        );
    }

    /**
     * Finds the difference between this
     * and given vectors.
     * */
    @NotNull public Vector2D difference(Vector2D other) {
        return new Vector2D(
                x - other.getX(),
                y - other.getY()
        );
    }

    /**
     * Multiplies vector by given coefficient
     * and returns it as the result.
     * */
    @NotNull public Vector2D multiply(double coefficient) {
        return new Vector2D(
                x * coefficient,
                y * coefficient
        );
    }
}
