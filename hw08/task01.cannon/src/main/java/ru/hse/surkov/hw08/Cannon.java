package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Cannon is the gun, which is represented
 * as the rectangle, which can move and rotate
 * about the base of the cannon. Cannon can shoot
 * bullets, which will start their movement at the
 * gunpoint with a certain velocity, start angle of
 * the bullet movement depends on the angle of the
 * cannon.
 * */
public class Cannon implements Drawable {

    private static final double VELOCITY_COEFFICIENT = 10;
    @NotNull private static final double[] bulletMasses = new double[] {
        1, 2, 3
    };

    @NotNull private GameState gameState;
    private double gunWidth;
    private double gunHeight;
    private double angle;
    @NotNull private Vector2D base;
    private int currentMassId = 0;

    public void setCurrentMassId(int id) {
        if (0 <= id && id < bulletMasses.length) {
            currentMassId = id;
        }
    }

    public double getGunWidth() {
        return gunWidth;
    }

    @NotNull public Vector2D getGunpointPosition() {
        return new Vector2D(
                base.getX() + gunHeight * Math.sin(angle),
                base.getY() + gunHeight * Math.cos(angle)
        );
    }

    public Cannon(
            @NotNull GameState gameState,
            double gunWidth,
            double gunHeight,
            double angle,
            @NotNull Vector2D base) {
        this.gameState = gameState;
        this.gunWidth = gunWidth;
        this.gunHeight = gunHeight;
        this.angle = angle;
        this.base = base;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    @NotNull public Vector2D getBase() {
        return base;
    }

    public void setBase(@NotNull Vector2D base) {
        this.base = base;
    }

    @NotNull public Bullet generateBullet() {
        return new Bullet(
                gameState,
                getGunpointPosition(),
                getGunWidth() / 2,
                getGunpointPosition()
                        .difference(getBase())
                        .multiply(VELOCITY_COEFFICIENT),
                bulletMasses[currentMassId]
        );
    }

    private void drawBulletTypes(@NotNull GraphicsContext graphicsContext) {
        for (int i = 0; i < bulletMasses.length; i++) {
            RenderEngine.drawCircleText(
                    Integer.toString(i + 1),
                    (0.85 + 0.15 * i / bulletMasses.length) * gameState.getFieldWidth(),
                    0.01 * gameState.getFieldHeight(),
                    0.05,
                    gameState,
                    graphicsContext,
                    currentMassId == i ? Color.DARKRED : Color.GRAY
            );
        }
    }

    /**
     * {@link Drawable#draw(GraphicsContext)}
     *
     * Draws the cannon as the rectangle rotated by the
     * angle and located at the landscape. Precise position
     * is defined by the position of the cannon base.
     * */
    @Override
    public void draw(@NotNull GraphicsContext graphicsContext) {
        var gunpoint = getGunpointPosition();
        var direction = gunpoint.difference(base);
        var rotated = direction
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
            ys[i] = gameState.getFieldHeight() - vertices.get(i).getY();
        }
        graphicsContext.setFill(Color.rgb(102, 0, 51));
        graphicsContext.fillPolygon(xs, ys, 4);
        drawBulletTypes(graphicsContext);
    }
}
