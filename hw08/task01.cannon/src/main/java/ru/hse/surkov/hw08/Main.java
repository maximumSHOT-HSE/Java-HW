package ru.hse.surkov.hw08;

import javafx.application.Application;
import javafx.stage.Stage;

import java.awt.*;

/**
 * The Main class for the initialization, configuration
 * and launching of the game.
 * */
public class Main extends Application {

    private final String GAME_NAME = "Cannon";

    private RenderEngine renderEngine;
    private PhysicsEngine physicsEngine;

    /**
     * Initializes and starts the application.
     * */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(GAME_NAME);
        primaryStage.setResizable(false);
        primaryStage.setWidth(
                Toolkit
                        .getDefaultToolkit()
                        .getScreenSize()
                        .getWidth() * 0.8
        );
        primaryStage.setHeight(
                Toolkit
                        .getDefaultToolkit()
                        .getScreenSize()
                        .getHeight() * 0.6
        );
        var gameState = new GameState(
                primaryStage.getWidth(),
                primaryStage.getHeight()
        );
        renderEngine = new RenderEngine(primaryStage, gameState);
        primaryStage.show();
        physicsEngine = new PhysicsEngine(gameState);
        var gameLoop = new GameLoop();
        gameLoop.addEngine(physicsEngine);
        physicsEngine.setGameLoop(gameLoop);
        gameLoop.addEngine(renderEngine);
        gameLoop.start();
    }

    /**
     * Launches the application.
     * */
    public static void main(String[] args) {
        Application.launch(args);
    }
}
