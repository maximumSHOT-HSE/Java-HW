package ru.hse.surkov.hw08;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector2DTest {

    private static final double EPS = 1e-5;

    @Test
    void testLength() {
        for (int x = -10; x <= 10; x++) {
            for (int y = -10; y <= 10; y++) {
                var v = new Vector2D(x, y);
                assertTrue(Math.abs(v.getLength()
                        - Math.sqrt(x * x + y * y)) < EPS);
            }
        }
        var v = new Vector2D(-0.123, 0.00017);
        assertTrue(Math.abs(v.getLength() - 0.123000117) < EPS);
    }

    @Test
    void testNormalize() {
        for (double x = -2; x <= 2; x += 0.1) {
            for (double y = -2; y <= 2; y += 0.1) {
                var v = new Vector2D(x, y).normalize();
                assertTrue(Math.abs(v.getLength()
                        - (x == 0 && y == 0 ? 0 : 1)) < EPS);
            }
        }
        for (double x = -1e9; x <= 1e9; x += 1e8) {
            for (double y = -1e9; y <= 1e9; y += 1e8) {
                var v = new Vector2D(x, y).normalize();
                assertTrue(Math.abs(v.getLength()
                        - (x == 0 && y == 0 ? 0 : 1)) < EPS);
            }
        }
        for (double x = -1e3; x <= 1e3; x += 1e2) {
            for (double y = -1e3; y <= 1e3; y += 1e2) {
                var v = new Vector2D(x, y).normalize();
                assertTrue(Math.abs(v.getLength()
                        - (x == 0 && y == 0 ? 0 : 1)) < EPS);
            }
        }
    }

    @Test
    void testAdd() {
        for (double x1 = -2; x1 <= 2; x1 += 0.1) {
            for (double y1 = -2; y1 <= 2; y1 += 0.1) {
                for (double x2 = -2; x2 <= 2; x2 += 0.1) {
                    for (double y2 = -2; y2 <= 2; y2 += 0.1) {
                        var v = new Vector2D(x1, y1).add(new Vector2D(x2, y2));
                        double x3 = x1 + x2;
                        double y3 = y1 + y2;
                        assertTrue(Math.abs(v.getLength()
                                - Math.sqrt(x3 * x3 + y3 * y3)) < EPS);
                        assertTrue(Math.abs(v.getX() - x3) < EPS);
                        assertTrue(Math.abs(v.getY() - y3) < EPS);
                    }
                }
            }
        }
        for (double x1 = -1e9; x1 <= 1e9; x1 += 1e8) {
            for (double y1 = -1e9; y1 <= 1e9; y1 += 1e8) {
                for (double x2 = -1e9; x2 <= 1e9; x2 += 1e8) {
                    for (double y2 = -1e9; y2 <= 1e9; y2 += 1e8) {
                        var v = new Vector2D(x1, y1).add(new Vector2D(x2, y2));
                        double x3 = x1 + x2;
                        double y3 = y1 + y2;
                        assertTrue(Math.abs(v.getLength()
                                - Math.sqrt(x3 * x3 + y3 * y3)) < EPS);
                        assertTrue(Math.abs(v.getX() - x3) < EPS);
                        assertTrue(Math.abs(v.getY() - y3) < EPS);
                    }
                }
            }
        }
        for (double x1 = -1e3; x1 <= 1e3; x1 += 1e2) {
            for (double y1 = -1e3; y1 <= 1e3; y1 += 1e2) {
                for (double x2 = -1e3; x2 <= 1e3; x2 += 1e2) {
                    for (double y2 = -1e3; y2 <= 1e3; y2 += 1e2) {
                        var v = new Vector2D(x1, y1).add(new Vector2D(x2, y2));
                        double x3 = x1 + x2;
                        double y3 = y1 + y2;
                        assertTrue(Math.abs(v.getLength()
                                - Math.sqrt(x3 * x3 + y3 * y3)) < EPS);
                        assertTrue(Math.abs(v.getX() - x3) < EPS);
                        assertTrue(Math.abs(v.getY() - y3) < EPS);
                    }
                }
            }
        }
    }

    @Test
    void testDifference() {
        for (double x1 = -2; x1 <= 2; x1 += 0.1) {
            for (double y1 = -2; y1 <= 2; y1 += 0.1) {
                for (double x2 = -2; x2 <= 2; x2 += 0.1) {
                    for (double y2 = -2; y2 <= 2; y2 += 0.1) {
                        var v = new Vector2D(x1, y1).difference(new Vector2D(x2, y2));
                        double x3 = x1 - x2;
                        double y3 = y1 - y2;
                        assertTrue(Math.abs(v.getLength()
                                - Math.sqrt(x3 * x3 + y3 * y3)) < EPS);
                        assertTrue(Math.abs(v.getX() - x3) < EPS);
                        assertTrue(Math.abs(v.getY() - y3) < EPS);
                    }
                }
            }
        }
        for (double x1 = -1e9; x1 <= 1e9; x1 += 1e8) {
            for (double y1 = -1e9; y1 <= 1e9; y1 += 1e8) {
                for (double x2 = -1e9; x2 <= 1e9; x2 += 1e8) {
                    for (double y2 = -1e9; y2 <= 1e9; y2 += 1e8) {
                        var v = new Vector2D(x1, y1).difference(new Vector2D(x2, y2));
                        double x3 = x1 - x2;
                        double y3 = y1 - y2;
                        assertTrue(Math.abs(v.getLength()
                                - Math.sqrt(x3 * x3 + y3 * y3)) < EPS);
                        assertTrue(Math.abs(v.getX() - x3) < EPS);
                        assertTrue(Math.abs(v.getY() - y3) < EPS);
                    }
                }
            }
        }
        for (double x1 = -1e3; x1 <= 1e3; x1 += 1e2) {
            for (double y1 = -1e3; y1 <= 1e3; y1 += 1e2) {
                for (double x2 = -1e3; x2 <= 1e3; x2 += 1e2) {
                    for (double y2 = -1e3; y2 <= 1e3; y2 += 1e2) {
                        var v = new Vector2D(x1, y1).difference(new Vector2D(x2, y2));
                        double x3 = x1 - x2;
                        double y3 = y1 - y2;
                        assertTrue(Math.abs(v.getLength()
                                - Math.sqrt(x3 * x3 + y3 * y3)) < EPS);
                        assertTrue(Math.abs(v.getX() - x3) < EPS);
                        assertTrue(Math.abs(v.getY() - y3) < EPS);
                    }
                }
            }
        }
    }

    @Test
    void testMultiply() {
        for (double x = -2; x <= 2; x += 0.1) {
            for (double y = -2; y <= 2; y += 0.1) {
                for (double c = -2; c <= 2; c += 0.1) {
                    var v = new Vector2D(x, y).multiply(c);
                    double x2 = x * c;
                    double y2 = y * c;
                    assertTrue(Math.abs(v.getLength()
                            - Math.sqrt(x2 * x2 + y2 * y2)) < EPS);
                    assertTrue(Math.abs(v.getX() - x2) < EPS);
                    assertTrue(Math.abs(v.getY() - y2) < EPS);
                }
            }
        }
        for (double x = -1e9; x <= 1e9; x += 1e8) {
            for (double y = -1e9; y <= 1e9; y += 1e8) {
                for (double c = -1e9; c <= 1e9; c += 1e8) {
                    var v = new Vector2D(x, y).multiply(c);
                    double x2 = x * c;
                    double y2 = y * c;
                    assertTrue(Math.abs(v.getLength()
                            - Math.sqrt(x2 * x2 + y2 * y2)) < EPS);
                    assertTrue(Math.abs(v.getX() - x2) < EPS);
                    assertTrue(Math.abs(v.getY() - y2) < EPS);
                }
            }
        }
        for (double x = -1e3; x <= 1e3; x += 1e2) {
            for (double y = -1e3; y <= 1e3; y += 1e2) {
                for (double c = -1e3; c <= 1e3; c += 1e2) {
                    var v = new Vector2D(x, y).multiply(c);
                    double x2 = x * c;
                    double y2 = y * c;
                    assertTrue(Math.abs(v.getLength()
                            - Math.sqrt(x2 * x2 + y2 * y2)) < EPS);
                    assertTrue(Math.abs(v.getX() - x2) < EPS);
                    assertTrue(Math.abs(v.getY() - y2) < EPS);
                }
            }
        }
    }

    @Test
    void testRotate() {
        for (int k = -10; k <= 10; k++) {
            double angle = k * Math.PI / 2;
            for (double x = -2; x <= 2; x += 0.1) {
                for (double y = -2; y <= 2; y += 0.1) {
                    var v = new Vector2D(x, y).rotate(angle);
                    double x2 = x * Math.cos(angle) - y * Math.sin(angle);
                    double y2 = y * Math.cos(angle) + x * Math.sin(angle);
                    assertTrue(Math.abs(v.getLength()
                            - Math.sqrt(x2 * x2 + y2 * y2)) < EPS);
                    assertTrue(Math.abs(v.getX() - x2) < EPS);
                    assertTrue(Math.abs(v.getY() - y2) < EPS);
                }
            }
            for (double x = -1e9; x <= 1e9; x += 1e8) {
                for (double y = -1e9; y <= 1e9; y += 1e8) {
                    var v = new Vector2D(x, y).rotate(angle);
                    double x2 = x * Math.cos(angle) - y * Math.sin(angle);
                    double y2 = y * Math.cos(angle) + x * Math.sin(angle);
                    assertTrue(Math.abs(v.getLength()
                            - Math.sqrt(x2 * x2 + y2 * y2)) < EPS);
                    assertTrue(Math.abs(v.getX() - x2) < EPS);
                    assertTrue(Math.abs(v.getY() - y2) < EPS);
                }
            }
            for (double x = -1e3; x <= 1e3; x += 1e2) {
                for (double y = -1e3; y <= 1e3; y += 1e2) {
                    var v = new Vector2D(x, y).rotate(angle);
                    double x2 = x * Math.cos(angle) - y * Math.sin(angle);
                    double y2 = y * Math.cos(angle) + x * Math.sin(angle);
                    assertTrue(Math.abs(v.getLength()
                            - Math.sqrt(x2 * x2 + y2 * y2)) < EPS);
                    assertTrue(Math.abs(v.getX() - x2) < EPS);
                    assertTrue(Math.abs(v.getY() - y2) < EPS);
                }
            }
        }
        for (int k = -10; k <= 10; k++) {
            if (k == 0) {
                continue;
            }
            double angle = (Math.PI / 2) / k;
            for (double x = -2; x <= 2; x += 0.1) {
                for (double y = -2; y <= 2; y += 0.1) {
                    var v = new Vector2D(x, y).rotate(angle);
                    double x2 = x * Math.cos(angle) - y * Math.sin(angle);
                    double y2 = y * Math.cos(angle) + x * Math.sin(angle);
                    assertTrue(Math.abs(v.getLength()
                            - Math.sqrt(x2 * x2 + y2 * y2)) < EPS);
                    assertTrue(Math.abs(v.getX() - x2) < EPS);
                    assertTrue(Math.abs(v.getY() - y2) < EPS);
                }
            }
            for (double x = -1e9; x <= 1e9; x += 1e8) {
                for (double y = -1e9; y <= 1e9; y += 1e8) {
                    var v = new Vector2D(x, y).rotate(angle);
                    double x2 = x * Math.cos(angle) - y * Math.sin(angle);
                    double y2 = y * Math.cos(angle) + x * Math.sin(angle);
                    assertTrue(Math.abs(v.getLength()
                            - Math.sqrt(x2 * x2 + y2 * y2)) < EPS);
                    assertTrue(Math.abs(v.getX() - x2) < EPS);
                    assertTrue(Math.abs(v.getY() - y2) < EPS);
                }
            }
            for (double x = -1e3; x <= 1e3; x += 1e2) {
                for (double y = -1e3; y <= 1e3; y += 1e2) {
                    var v = new Vector2D(x, y).rotate(angle);
                    double x2 = x * Math.cos(angle) - y * Math.sin(angle);
                    double y2 = y * Math.cos(angle) + x * Math.sin(angle);
                    assertTrue(Math.abs(v.getLength()
                            - Math.sqrt(x2 * x2 + y2 * y2)) < EPS);
                    assertTrue(Math.abs(v.getX() - x2) < EPS);
                    assertTrue(Math.abs(v.getY() - y2) < EPS);
                }
            }
        }
    }
}