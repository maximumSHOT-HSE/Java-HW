package ru.hse.surkov.hw08;

import javafx.scene.shape.Circle;

public class Bullet {

    private Circle body;
    private Vector2D velocity;
    private double mass;

    public Bullet(Circle body, Vector2D velocity, double mass) {
        this.body = body;
        this.velocity = velocity;
        this.mass = mass;
    }

    public Circle getBody() {
        return body;
    }

    public void setBody(Circle body) {
        this.body = body;
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

    public void setMass(double mass) {
        this.mass = mass;
    }
}
