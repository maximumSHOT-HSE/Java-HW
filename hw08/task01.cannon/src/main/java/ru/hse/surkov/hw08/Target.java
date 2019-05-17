package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Target implements Drawable {

    private GameState gameState;
    private Vector2D center;
    private double radius;

    public Target(GameState gameState, Vector2D center, double radius) {
        this.gameState = gameState;
        this.center = center;
        this.radius = radius;
    }

    public Vector2D getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.DARKRED);
        graphicsContext.fillOval(
                center.getX() - radius,
                gameState.getFieldHeight() - center.getY() - radius,
                2 * radius,
                2 * radius
        );
    }
}
