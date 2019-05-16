package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Cannon implements Drawable {

    private double fieldWidth;
    private double fieldHeight;

    private double gunWidth;
    private double gunHeight;
    private double angle;
    private Vector2D base;

    private Vector2D getGunpointPosition() {
        return new Vector2D(
                base.getX() + gunHeight * Math.sin(angle),
                base.getY() + gunHeight * Math.cos(angle)
        );
    }

    public Cannon(double fieldWidth,
                  double fieldHeight,
                  double gunWidth,
                  double gunHeight,
                  double angle,
                  Vector2D base) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.gunWidth = gunWidth;
        this.gunHeight = gunHeight;
        this.angle = angle;
        this.base = base;
    }

    public double getFieldWidth() {
        return fieldWidth;
    }

    public void setFieldWidth(double fieldWidth) {
        this.fieldWidth = fieldWidth;
    }

    public double getFieldHeight() {
        return fieldHeight;
    }

    public void setFieldHeight(double fieldHeight) {
        this.fieldHeight = fieldHeight;
    }

    public double getGunWidth() {
        return gunWidth;
    }

    public void setGunWidth(double gunWidth) {
        this.gunWidth = gunWidth;
    }

    public double getGunHeight() {
        return gunHeight;
    }

    public void setGunHeight(double gunHeight) {
        this.gunHeight = gunHeight;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public Vector2D getBase() {
        return base;
    }

    public void setBase(Vector2D base) {
        this.base = base;
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        Vector2D gunpoint = getGunpointPosition();
        Vector2D direction = gunpoint.difference(base);
        Vector2D rotated = direction
                .rotate(Math.PI / 2)
                .normalize()
                .multiply(gunWidth / 2);
        List<Vector2D> vertices = new ArrayList<>();
        vertices.add(base.add(rotated));
        vertices.add(gunpoint.add(rotated));
        rotated = rotated.multiply(-1);
        vertices.add(gunpoint.add(rotated));
        vertices.add(base.add(rotated));
        double[] xs = new double[vertices.size()];
        double[] ys = new double[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            xs[i] = vertices.get(i).getX();
            ys[i] = fieldHeight - vertices.get(i).getY();
        }
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillPolygon(xs, ys, 4);
    }
}
