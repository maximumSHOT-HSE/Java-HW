package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class GameState implements Drawable {

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
    private List<Target> targets = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();
    private Set<String> activeKeys = new TreeSet<>();

    public double getFieldWidth() {
        return fieldWidth;
    }

    public double getFieldHeight() {
        return fieldHeight;
    }

    public Set<String> getActiveKeys() {
        return activeKeys;
    }

    void addKey(String keyCode) {
        activeKeys.add(keyCode);
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
        System.out.println("FIRE");
//        Bullet bullet = new Bullet(
//                cannon.getBase(),
//                cannon.getGunWidth(),
//                new Vector2D(0, 0),
//                1
//        );
    }
}
