package ru.hse.surkov.hw08;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LandscapeTest {

    private static final double FIELD_WIDTH = 10;
    private static final double FIELD_HEIGHT = 5;
    private static final double EPS = 1e-5;

    private Landscape landscape;

    @BeforeEach
    void setUp() {
        landscape = new Landscape(FIELD_WIDTH, FIELD_HEIGHT);
    }

    @Test
    void testOutOfBoundsX() {
        assertEquals(2 * FIELD_HEIGHT, landscape.getY(-0.0001));
        assertEquals(2 * FIELD_HEIGHT, landscape.getY(FIELD_WIDTH + 0.0001));
    }

    @Test
    void testSimpleLandscapeGetY() {
        landscape.setLandscape(
                new double[] {0, 0.3, 0.5, 0.7, 1},
                new double[] {0, 0, 0.5, 0, 0}
        );
        assertTrue(Math.abs(landscape.getY(0 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(0.1 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(0.2 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(0.25 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(0.3 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(0.5 * FIELD_WIDTH) - 0.5 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.4 * FIELD_WIDTH) - 0.25 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.6 * FIELD_WIDTH) - 0.25 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.45 * FIELD_WIDTH) - 0.375 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.55 * FIELD_WIDTH) - 0.375 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.7 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(0.75 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(0.8 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(0.85 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(0.9 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(0.95 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(1 * FIELD_WIDTH)) < EPS);
    }


    @Test
    void testPlateau() {
        landscape.setLandscape(
                new double[] {0, 0.1, 0.15, 0.85, 0.9, 1},
                new double[] {0, 0, 0.3, 0.3 ,0, 0}
        );
        assertTrue(Math.abs(landscape.getY(0 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(0.05 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(0.1 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(0.13 * FIELD_WIDTH) - 0.18 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.15 * FIELD_WIDTH) - 0.3 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.2 * FIELD_WIDTH) - 0.3 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.3 * FIELD_WIDTH) - 0.3 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.4 * FIELD_WIDTH) - 0.3 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.5 * FIELD_WIDTH) - 0.3 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.6 * FIELD_WIDTH) - 0.3 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.7 * FIELD_WIDTH) - 0.3 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.8 * FIELD_WIDTH) - 0.3 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.87 * FIELD_WIDTH) - 0.18 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.9 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(0.95 * FIELD_WIDTH)) < EPS);
        assertTrue(Math.abs(landscape.getY(1.0 * FIELD_WIDTH)) < EPS);
    }

    @Test
    void testComplicatedLandscape() {
        landscape.setLandscape(
                new double[] {0, 0.2, 0.4, 0.7, 0.8, 1},
                new double[] {0.5, 0.5, 0.7, 0.3, 0.6, 0}
        );
        assertTrue(Math.abs(landscape.getY(0 * FIELD_WIDTH) - 0.5 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.15 * FIELD_WIDTH) - 0.5 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.2 * FIELD_WIDTH) - 0.5 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.25 * FIELD_WIDTH) - 0.55 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.4 * FIELD_WIDTH) - 0.7 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.57 * FIELD_WIDTH) - 0.4733333333 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.7 * FIELD_WIDTH) - 0.3 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.75 * FIELD_WIDTH) - 0.45 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.8 * FIELD_WIDTH) - 0.6 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(0.85 * FIELD_WIDTH) - 0.45 * FIELD_HEIGHT) < EPS);
        assertTrue(Math.abs(landscape.getY(1 * FIELD_WIDTH) - 0 * FIELD_HEIGHT) < EPS);
    }
}