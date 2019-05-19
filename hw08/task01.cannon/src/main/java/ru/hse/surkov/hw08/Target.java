package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

/**
 * Abstraction for the target. The
 * target is the circle lying on the landscape.
 * The main player goal to collect all targets.
 * */
public class Target implements Drawable {

    @NotNull private GameState gameState;
    @NotNull private Vector2D center;
    private double radius;

    public Target(
            @NotNull GameState gameState,
            @NotNull Vector2D center,
            double radius) {
        this.gameState = gameState;
        this.center = center;
        this.radius = radius;
    }

    @NotNull public Vector2D getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    /**
     * {@link Drawable#draw(GraphicsContext)}
     *
     * Draws the target as the circle lying on
     * the landscape.
     */
    @Override
    public void draw(@NotNull GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.DARKRED);
        graphicsContext.fillOval(
                center.getX() - radius,
                gameState.getFieldHeight() - center.getY() - radius,
                2 * radius,
                2 * radius
        );
    }
}
