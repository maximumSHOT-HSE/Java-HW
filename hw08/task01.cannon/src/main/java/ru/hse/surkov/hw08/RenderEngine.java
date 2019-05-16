package ru.hse.surkov.hw08;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class RenderEngine implements Engine {

    private Stage primaryStage;
    private Group root;
    private Scene scene;
    private Canvas canvas;
    private GraphicsContext graphicsContext;

    private GameState gameState;

    public RenderEngine(Stage givenPrimaryStage, GameState gameState) {
        this.gameState = gameState;
        this.primaryStage = givenPrimaryStage;
        root = new Group();
        scene = new Scene(
            root,
            primaryStage.getWidth(),
            primaryStage.getHeight(),
            Color.GRAY
        );
        scene.setOnKeyPressed(
            event -> gameState.addKey(event.getCode().toString())
        );
        scene.setOnKeyReleased(
            event -> gameState.removeKey(event.getCode().toString())
        );
        primaryStage.setScene(scene);
        canvas = new Canvas(primaryStage.getWidth(), primaryStage.getHeight());
        root.getChildren().add(canvas);
        graphicsContext = canvas.getGraphicsContext2D();
    }

    @Override
    public void update(double deltaTime) {
        graphicsContext.clearRect(
                0, 0,
                primaryStage.getWidth(),
                primaryStage.getHeight());
        gameState.draw(graphicsContext);
    }
}
