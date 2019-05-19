package ru.hse.surkov.hw08;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CannonTest {

    private static final double EPS = 1e-5;

    private static final double GUN_WIDTH = 10;
    private static final double GUN_HEIGHT = 100;

    private static final double FIELD_WIDTH = 10;
    private static final double FIELD_HEIGHT = 100;

    @NotNull private GameState gameState;
    @NotNull private Cannon cannon;

    @BeforeEach
    void setUp() {
        gameState = new GameState(
            FIELD_WIDTH,
            FIELD_HEIGHT
        );
        cannon = new Cannon(
            gameState,
            GUN_WIDTH,
            GUN_HEIGHT,
            0,
                new Vector2D(0,0)
        );
    }

    @Test
    void testGunpointPosition() {
        double[] xs = new double[] {
            -1e9, -1e5, -1e2, -12, -7, -5.41, -3.1, -1, -0.001,
            0, 0.001, 1, 2, 5.23, 7.35, 28.7, 1e2 + 5, 1e7, 1e9 + 7
        };
        double[] angles = new double[] {
            0, Math.PI / 2, -Math.PI / 2, Math.PI / 3, -Math.PI / 3, Math.PI / 4, -Math.PI / 4,
            Math.PI, -Math.PI, 2 * Math.PI, -2 * Math.PI, 1000 * Math.PI, -1000 * Math.PI,
            999 * Math.PI, -999 * Math.PI, 0.123, 3.132, 10.1000, 22.8, 13.37
        };
        for (var x : xs) {
            for (var y : xs) {
                for (var a : angles) {
                    cannon.setAngle(a);
                    cannon.setBase(new Vector2D(x, y));
                    double x2 = x + FIELD_HEIGHT * Math.sin(a);
                    double y2 = y + FIELD_HEIGHT * Math.cos(a);
                    var v = cannon.getGunpointPosition();
                    assertTrue(Math.abs(v.getLength()
                            - Math.sqrt(x2 * x2 + y2 * y2)) < EPS);
                    assertTrue(Math.abs(v.getX() - x2) < EPS);
                    assertTrue(Math.abs(v.getY() - y2) < EPS);
                }
            }
        }
    }

    @Test
    void testGenerateBullet() {
        double[] xs = new double[] {
                -1e9, -1e5, -1e2, -12, -7, -5.41, -3.1, -1, -0.001,
                0, 0.001, 1, 2, 5.23, 7.35, 28.7, 1e2 + 5, 1e7, 1e9 + 7
        };
        double[] angles = new double[] {
                0, Math.PI / 2, -Math.PI / 2, Math.PI / 3, -Math.PI / 3, Math.PI / 4, -Math.PI / 4,
                Math.PI, -Math.PI, 2 * Math.PI, -2 * Math.PI, 1000 * Math.PI, -1000 * Math.PI,
                999 * Math.PI, -999 * Math.PI, 0.123, 3.132, 10.1000, 22.8, 13.37
        };
        for (var x : xs) {
            for (var y : xs) {
                for (var a : angles) {
                    cannon.setAngle(a);
                    cannon.setBase(new Vector2D(x, y));
                    var v1 = cannon.getGunpointPosition();
                    var v2 = cannon.generateBullet().getCenter();
                    assertTrue(Math.abs(v1.getLength() - v2.getLength()) < EPS);
                    assertTrue(Math.abs(v1.getX() - v2.getX()) < EPS);
                    assertTrue(Math.abs(v1.getY() - v2.getY()) < EPS);
                    var v3 = cannon.generateBullet().getVelocity();
                    var v4 = cannon.getGunpointPosition().difference(cannon.getBase());
                    assertTrue(Math.abs(v3.vectorMultiply(v4)) < EPS);
                    assertTrue(v3.scalarMultiply(v4) >= 0);
                }
            }
        }
    }
}