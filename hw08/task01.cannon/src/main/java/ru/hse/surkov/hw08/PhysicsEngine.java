package ru.hse.surkov.hw08;

public class PhysicsEngine implements Engine {

    private static final double RATIO_DELTA_X_PER_TOUCH = 0.0025;
    private static final double RATIO_DELTA_ANGLE_PER_TOUCH = 0.015;
    private static final double GRAVITATION = 300;
    private static final double DETONATION_RADIUS_RATIO = 10;

    private GameState gameState;

    public PhysicsEngine(GameState gameState) {
        this.gameState = gameState;
    }

    void processKeys() {
        var activeKeys = gameState.getActiveKeys();
        for (var keyCode : activeKeys) {
            switch (keyCode) {
                case "LEFT":
                    gameState.moveCannon(
                            -RATIO_DELTA_X_PER_TOUCH * gameState.getFieldWidth());
                    break;
                case "RIGHT":
                    gameState.moveCannon(
                            RATIO_DELTA_X_PER_TOUCH * gameState.getFieldWidth());
                    break;
                case "UP":
                    gameState.rotateCannon(
                            RATIO_DELTA_ANGLE_PER_TOUCH * Math.PI / 2);
                    break;
                case "DOWN":
                    gameState.rotateCannon(
                            -RATIO_DELTA_ANGLE_PER_TOUCH * Math.PI / 2);
                    break;
            }
        }
    }

    void processBullets(double deltaTime) {
        var bullets = gameState.getBullets();
        var iterator = bullets.iterator();
        while (iterator.hasNext()) {
            var bullet = iterator.next();
            double x = bullet.getCenter().getX();
            double y = bullet.getCenter().getY();
            if (y < gameState.getLandscape().getY(x)) {
                iterator.remove();
                gameState.addBoom(x,bullet.getMass() * DETONATION_RADIUS_RATIO);
                continue;
            }
            double vx = bullet.getVelocity().getX();
            double vy = bullet.getVelocity().getY();
            x += vx * deltaTime;
            y += vy * deltaTime - GRAVITATION * deltaTime * deltaTime / 2;
            vy -= GRAVITATION * deltaTime;
            bullet.setCenter(new Vector2D(x, y));
            bullet.setVelocity(new Vector2D(vx, vy));
        }
    }

    @Override
    public void update(double deltaTime) {
        processKeys();
        processBullets(deltaTime);
    }
}
