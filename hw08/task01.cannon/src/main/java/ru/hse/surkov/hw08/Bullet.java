package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet implements Drawable {

    private GameState gameState;
    private Vector2D center;
    private double radius;
    private Vector2D velocity;
    private double mass;

    public Bullet(
            GameState gameState,
            Vector2D center,
            double radius,
            Vector2D velocity,
            double mass) {
        this.gameState = gameState;
        this.center = center;
        this.radius = radius;
        this.velocity = velocity;
        this.mass = mass;
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillOval(
            center.getX() - radius,
                gameState.getFieldHeight() - center.getY() - radius,
                2 * radius,
                2 * radius
        );
    }

    public double getRadius() {
        return radius;
    }

    public Vector2D getCenter() {
        return center;
    }

    public void setCenter(Vector2D center) {
        this.center = center;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public double getMass() {
        return mass;
    }
}
