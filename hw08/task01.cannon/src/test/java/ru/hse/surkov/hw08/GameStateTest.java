package ru.hse.surkov.hw08;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    private static final double EPS = 1e-5;
    private static final long SEED = 42;
    private static final double FIELD_WIDTH = 10;
    private static final double FIELD_HEIGHT = 5;

    private GameState gameState;

    @BeforeEach
    void setUp() {
        gameState = new GameState(
            FIELD_WIDTH,
            FIELD_HEIGHT
        );
        gameState.setRandomSeed(SEED);
    }

    @Test
    void testFire() {
        for (int iter = 0; iter < 10; iter++) {
            assertEquals(iter, gameState.getBullets().size());
            gameState.fire();
            assertEquals(iter + 1, gameState.getBullets().size());
        }
    }

    @Test
    void testAddDetonation() {
        int detonationNumber = 0;
        for (double x = 0;x <= FIELD_HEIGHT; x += 0.1) {
            for (double r = 0.1; r <= 1; r += 0.05) {
                assertEquals(detonationNumber, gameState.getDetonations().size());
                gameState.addDetonation(x, r);
                detonationNumber++;
                assertEquals(detonationNumber, gameState.getDetonations().size());
            }
        }
    }

    @Test
    void testAddKey() {
        var keys = new String[] {
            "LEFT", "X", "RIGHT", "AHAHAHAHHAHA",
            "UP", "DOWN",
            "DIGIT1", "DIGIT2", "DIGIT3", "DIGIT4", "DIGIT5", "DIGIT6", "DIGIT7", "DIGIT8", "DIGIT9", "DIGIT0",
            "ENTER"
        };
        int fires = 0;
        for (int iteration = 0; iteration < 10; iteration++) {
            for (int i = 0; i < keys.length; i++) {
                var code = keys[i];
                if (code.equals("ENTER")) {
                    assertEquals(fires, gameState.getBullets().size());
                    gameState.addKey(code);
                    fires++;
                    assertEquals(fires, gameState.getBullets().size());
                } else if (code.startsWith("DIGIT")) {
                    int digit = Integer.parseInt(code.substring(5));
                    if (digit < 1 || digit > 3) {
                        int massBefore = gameState.getCannon().getCurrentMassId();
                        gameState.addKey(code);
                        int massAfter = gameState.getCannon().getCurrentMassId();
                        assertEquals(massBefore, massAfter);
                    } else {
                        gameState.addKey(code);
                        int massAfter = gameState.getCannon().getCurrentMassId();
                        assertEquals(digit - 1, massAfter);
                    }
                }
            }
        }
    }

    @Test
    void testRotateCannon() {
        for (double x = -10; x <= 10; x += 0.1) {
            double angleBefore = gameState.getCannon().getAngle();
            gameState.rotateCannon(x);
            double angleAfter = gameState.getCannon().getAngle();
            double rotation = angleAfter - angleBefore;
            assertTrue(Math.abs(rotation - x) < EPS);
        }
    }

    @Test
    void testMoveCannon() {
        for (double x = 0; x <= FIELD_WIDTH; x += 0.1) {
            for (double dx = -50; dx <= 50; dx += 0.1) {
                double y = gameState.getLandscape().getY(x);
                gameState.getCannon().setBase(new Vector2D(x, y));
                double x2 = Math.max(0.0, Math.min(FIELD_WIDTH, x + dx));
                gameState.moveCannon(dx);
                assertTrue(Math.abs(gameState.getCannon().getBase().getX() - x2) < EPS);
            }
        }
    }
}