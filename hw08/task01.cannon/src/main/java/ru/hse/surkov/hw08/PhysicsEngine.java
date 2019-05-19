package ru.hse.surkov.hw08;

import org.jetbrains.annotations.NotNull;

/**
 * Engines for the processing physical laws
 * and changing the states of all game entities
 * according to them.
 * */
public class PhysicsEngine implements Engine {

    private static final double RATIO_DELTA_X_PER_TOUCH = 0.0025;
    private static final double RATIO_DELTA_ANGLE_PER_TOUCH = 0.010;
    private static final double GRAVITATION = 600;
    private static final double DETONATION_RADIUS_RATIO = 10;

    @NotNull private GameState gameState;
    @NotNull private GameLoop gameLoop;

    public PhysicsEngine(@NotNull GameState gameState) {
        this.gameState = gameState;
    }

    public void setGameLoop(@NotNull GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    private void processKeys() {
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

    private void processBullets(double deltaTime) {
        var bullets = gameState.getBullets();
        var iterator = bullets.iterator();
        while (iterator.hasNext()) {
            var bullet = iterator.next();
            double x = bullet.getCenter().getX();
            double y = bullet.getCenter().getY();
            if (y < gameState.getLandscape().getY(x)) {
                iterator.remove();
                gameState.addDetonation(x,bullet.getMass() * DETONATION_RADIUS_RATIO);
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

    private boolean checkDetonation(
            @NotNull Target target,
            @NotNull Detonation detonation) {
        return
            target.getCenter().difference(detonation.getCenter()).getLength()
                    < target.getRadius() + detonation.getRadius();
    }

    private void processCollisions() {
        var targets = gameState.getTargets();
        var detonations = gameState.getDetonations();
        var iterator = targets.iterator();
        while (iterator.hasNext()) {
            var target = iterator.next();
            for (var detonation : detonations) {
                if (checkDetonation(target, detonation)) {
                    iterator.remove();
                    break;
                }
            }
        }
        if (gameState.getTargets().isEmpty()) {
            gameState.setGameStatus(GameState.GameStatus.FINISHED);
            gameLoop.stop();
        }
    }

    /**
     * {@link Engine#update(double)}
     *
     * Processes events related with
     * physical laws and player's actions.
     * Changes the cannon position and angle
     * if appropriate keys are pressed.
     * Checks whether some bullet reach the landscape.
     * Changes the states of the bullets according
     * to the physical laws called the movement of the
     * parabola.
     * Processes collisions between detonations and targets.
     * */
    @Override
    public void update(double deltaTime) {
        processKeys();
        processBullets(deltaTime);
        processCollisions();
    }
}
