package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class GameState implements Drawable {

    private static final double RATIO_DELTA_X_PER_TOUCH = 0.01;
    private static final double RATIO_DELTA_ANGLE_PER_TOUCH = 0.05;

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

    void pressKey(String keyCode) {
        switch (keyCode) {
            case "LEFT":
                moveCannon(-RATIO_DELTA_X_PER_TOUCH * fieldWidth);
                break;
            case "RIGHT":
                moveCannon(RATIO_DELTA_X_PER_TOUCH * fieldWidth);
                break;
            case "UP":
                rotateCannon(RATIO_DELTA_ANGLE_PER_TOUCH * Math.PI / 2);
                break;
            case "DOWN":
                rotateCannon(-RATIO_DELTA_ANGLE_PER_TOUCH * Math.PI / 2);
                break;
            case "ENTER":
                fire();
                break;
        }
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
    }

    public void moveCannon(double deltaX) {
        Vector2D cannonBase = cannon.getBase();
        cannon.setBase(new Vector2D(
                cannonBase.getX() + deltaX,
                landscape.getY(cannonBase.getX() + deltaX)
        ));
    }

    public void rotateCannon(double deltaAngle) {
        double cannonAngle = cannon.getAngle();
        cannon.setAngle(cannonAngle + deltaAngle);
    }

    public void fire() {
        System.out.println("FIRE");
    }
}
