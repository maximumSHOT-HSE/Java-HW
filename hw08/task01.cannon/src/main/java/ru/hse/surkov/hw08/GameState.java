package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;

import java.util.*;

public class GameState implements Drawable {

    private static final double VELOCITY_COEFFICIENT = 7;

    /*
    * Game work is a rectangle,
    * so all coordinates have restriction
    * x's between zero and width inclusive,
    * y's between zero and height inclusive
    * */
    private double fieldWidth;
    private double fieldHeight;

    private Landscape landscape;
    private Cannon cannon;
    private Set<Target> targets = new HashSet<>();
    private Set<Bullet> bullets = new HashSet<>();
    private Set<String> activeKeys = new TreeSet<>();
    private Set<Detonation> detonations = new HashSet<>();

    public void addBoom(double x, double radius) {
        detonations.add(
            new Detonation(
                fieldWidth,
                fieldHeight,
                new Vector2D(x, landscape.getY(x)),
                radius
            )
        );
    }

    public Set<Bullet> getBullets() {
        return bullets;
    }

    public double getFieldWidth() {
        return fieldWidth;
    }

    public Landscape getLandscape() {
        return landscape;
    }

    public Set<String> getActiveKeys() {
        return activeKeys;
    }

    void addKey(String keyCode) {
        if (keyCode.equals("ENTER")) {
            fire();
        } else {
            activeKeys.add(keyCode);
        }
    }

    void removeKey(String keyCode) {
        activeKeys.remove(keyCode);
    }

    public GameState(double fieldWidth, double fieldHeight) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        landscape = new Landscape(fieldWidth, fieldHeight);
        cannon = new Cannon(
                fieldWidth,
                fieldHeight,
                0.01 * fieldWidth,
                0.12 * fieldHeight,
                0,
                new Vector2D(fieldWidth / 2,
                        landscape.getY(fieldWidth / 2)));
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        landscape.draw(graphicsContext);
        cannon.draw(graphicsContext);
        for (Bullet bullet : bullets) {
            bullet.draw(graphicsContext);
        }
        for (Target target : targets) {
            target.draw(graphicsContext);
        }
        var iterator = detonations.iterator();
        while (iterator.hasNext()) {
            var detonation = iterator.next();
            if (detonation.getRemainingLifeTime() < 0) {
                iterator.remove();
                continue;
            }
            detonation.decreaseLifeTime();
            detonation.draw(graphicsContext);
        }
    }

    public void moveCannon(double deltaX) {
        Vector2D cannonBase = cannon.getBase();
        double targetX = Math.max(0.0, Math.min(fieldWidth, cannonBase.getX() + deltaX));
        cannon.setBase(new Vector2D(
                targetX,
                landscape.getY(targetX)
        ));
    }

    public void rotateCannon(double deltaAngle) {
        double cannonAngle = cannon.getAngle();
        cannon.setAngle(cannonAngle + deltaAngle);
    }

    public void fire() {
        Bullet bullet = new Bullet(
                fieldWidth,
                fieldHeight,
                cannon.getGunpointPosition(),
                cannon.getGunWidth() / 2,
                cannon
                        .getGunpointPosition()
                        .difference(cannon.getBase())
                        .multiply(VELOCITY_COEFFICIENT),
                1
        );
        bullets.add(bullet);
    }
}
