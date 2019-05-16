package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;

public class Bullet implements Drawable {

    private Vector2D center;
    private double radius;
    private Vector2D velocity;
    private double mass;

    public Bullet(Vector2D center, double radius, Vector2D velocity, double mass) {
        this.center = center;
        this.radius = radius;
        this.velocity = velocity;
        this.mass = mass;
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {

    }
}
