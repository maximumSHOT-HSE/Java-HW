package ru.hse.surkov.hw08;

import javafx.application.Application;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * The Main class for the initialization, configuration
 * and launching of the game.
 * */
public class Main extends Application {

    /**
     * Initializes and starts the application.
     * */
    @Override
    public void start(@NotNull Stage primaryStage) {
        primaryStage.setTitle("Cannon");
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
        var renderEngine = new RenderEngine(primaryStage, gameState);
        primaryStage.show();
        var physicsEngine = new PhysicsEngine(gameState);
        var gameLoop = new GameLoop();
        gameLoop.addEngine(physicsEngine);
        physicsEngine.setGameLoop(gameLoop);
        gameLoop.addEngine(renderEngine);
        gameLoop.start();
    }

    /**
     * Launches the application.
     * */
    public static void main(@NotNull String[] args) {
        Application.launch(args);
    }
}
