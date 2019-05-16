package ru.hse.surkov.hw08;

public class PhysicsEngine implements Engine {

    private static final double RATIO_DELTA_X_PER_TOUCH = 0.0025;
    private static final double RATIO_DELTA_ANGLE_PER_TOUCH = 0.015;

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
                case "ENTER":
                    gameState.fire();
                    break;
            }
        }
    }

    @Override
    public void update(double deltaTime) {
        processKeys();
    }
}
