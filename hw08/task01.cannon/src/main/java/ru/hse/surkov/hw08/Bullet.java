package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

/**
 * Bullet of cannon, which is circle
 * with defined radius, mass and
 * start velocity. Bullet moves
 * by physical law namely the movement
 * of the parabola.
 * */
public class Bullet implements Drawable {

    @NotNull private GameState gameState;
    @NotNull private Vector2D center;
    private double radius;
    @NotNull private Vector2D velocity;
    private double mass;

    public Bullet(
            @NotNull GameState gameState,
            @NotNull Vector2D center,
            double radius,
            @NotNull Vector2D velocity,
            double mass) {
        this.gameState = gameState;
        this.center = center;
        this.radius = radius;
        this.velocity = velocity;
        this.mass = mass;
    }

    /**
     * {@link Drawable#draw(GraphicsContext)}
     *
     * Draws the bullet as the circle with a certain center
     * and radius.
     * * */
    @Override
    public void draw(@NotNull GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillOval(
            center.getX() - radius,
                gameState.getFieldHeight() - center.getY() - radius,
                2 * radius,
                2 * radius
        );
    }

    @NotNull public Vector2D getCenter() {
        return center;
    }

    public void setCenter(@NotNull Vector2D center) {
        this.center = center;
    }

    @NotNull public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(@NotNull Vector2D velocity) {
        this.velocity = velocity;
    }

    public double getMass() {
        return mass;
    }
}
