package ru.hse.surkov.hw08;

import javafx.application.Application;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    private final String GAME_NAME = "Cannon";

    private RenderEngine renderEngine;
    private PhysicsEngine physicsEngine;
    private InputListener inputListener;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(GAME_NAME);
        primaryStage.setResizable(false);
        primaryStage.setWidth(
                Toolkit
                        .getDefaultToolkit()
                        .getScreenSize()
                        .getWidth() * 0.4
        );
        primaryStage.setHeight(
                Toolkit
                        .getDefaultToolkit()
                        .getScreenSize()
                        .getHeight() * 0.4
        );
        var gameState = new GameState(
                primaryStage.getWidth(),
                primaryStage.getHeight()
        );
        renderEngine = new RenderEngine(primaryStage, gameState);
        primaryStage.show();
        physicsEngine = new PhysicsEngine(gameState);
        inputListener = new InputListener(gameState);
        var gameLoop = new GameLoop();
        gameLoop.addEngine(inputListener);
        gameLoop.addEngine(physicsEngine);
        gameLoop.addEngine(renderEngine);
        gameLoop.start();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
