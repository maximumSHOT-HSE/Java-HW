package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Detonation implements Drawable {

    private static final double DECREASE_DETONATION_COEFFICIENT = 0.1;
    private static final double DETONATION_LIFE_TIME = 20;

    private GameState gameState;
    private Vector2D center;
    private double radius;
    private double remainingLifeTime = DETONATION_LIFE_TIME;

    public double getRemainingLifeTime() {
        return remainingLifeTime;
    }

    public void decreaseLifeTime() {
        remainingLifeTime -= DECREASE_DETONATION_COEFFICIENT;
    }

    public Detonation(GameState gameState, Vector2D center, double radius) {
        this.gameState = gameState;
        this.center = center;
        this.radius = radius;
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.ORANGE);
        graphicsContext.fillOval(
                center.getX() - radius,
                gameState.getFieldHeight() - center.getY() - radius,
                2 * radius,
                2 * radius
        );
    }

    public Vector2D getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }
}
