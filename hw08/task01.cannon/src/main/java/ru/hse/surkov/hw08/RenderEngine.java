package ru.hse.surkov.hw08;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * Engine for the drawing entities of the game.
 */
public class RenderEngine implements Engine {

    @NotNull private Stage primaryStage;
    @NotNull private GraphicsContext graphicsContext;
    @NotNull private GameState gameState;

    @NotNull private WritableImage enterHintImage;
    @NotNull private WritableImage digitsHintImage;
    @NotNull private WritableImage leftRightHintImage;
    @NotNull private WritableImage upDownHintImage;

    public RenderEngine(
            @NotNull Stage givenPrimaryStage,
            @NotNull GameState gameState) {
        this.gameState = gameState;
        this.primaryStage = givenPrimaryStage;
        var root = new Group();
        var scene = new Scene(
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
        var canvas = new Canvas(primaryStage.getWidth(), primaryStage.getHeight());
        root.getChildren().add(canvas);
        graphicsContext = canvas.getGraphicsContext2D();
        createHintImages();
    }

    /**
     * Draws the circle and the given text inside
     * at the given position and paints it
     * in the given color.
     */
    public static void drawCircleText(
            @NotNull String textString,
            double x,
            double y,
            double radiusRatio,
            @NotNull GameState gameState,
            @NotNull GraphicsContext graphicsContext,
            @NotNull Color color) {
        var stackPane = new StackPane();
        stackPane.setPrefSize(26, 26);
        var circle = new Circle(radiusRatio * gameState.getFieldHeight());
        circle.setStroke(Color.BLACK);
        circle.setFill(color);
        circle.setStrokeWidth(3);
        stackPane.getChildren().add(circle);
        var text = new Text(textString);
        text.setFont(Font.font(24));
        stackPane.getChildren().add(text);
        var parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        var image = stackPane.snapshot(parameters, null);
        graphicsContext.drawImage(image, x, y);
    }

    /**
     * Clears whole field and redraws all entities.
     */
    @Override
    public void update(double deltaTime) {
        graphicsContext.clearRect(
                0, 0,
                primaryStage.getWidth(),
                primaryStage.getHeight());
        gameState.draw(graphicsContext);
        drawCircleText(
                Integer.toString(gameState.getTargets().size()),
                0.01 * gameState.getFieldWidth(),
                0.01 * gameState.getFieldHeight(),
                0.05,
                gameState,
                graphicsContext,
                Color.DARKRED
        );
        drawHints();
        if (gameState.getGameStatus().equals(GameState.GameStatus.FINISHED)) {
            drawFinish();
        }
    }

    private void drawFinish() {
        drawCircleText(
                "YOU WIN!",
                gameState.getFieldWidth() / 3.0,
                gameState.getFieldHeight() / 2.0,
                0.13,
                gameState,
                graphicsContext,
                Color.DARKRED
        );
    }

    private void drawImage(@NotNull WritableImage image, double x, double y) {
        graphicsContext.drawImage(image, x, y);
    }

    private void drawHints() {
        drawImage(enterHintImage,
                0.82 * gameState.getFieldWidth(),
                0.7 * gameState.getFieldHeight());
        drawImage(digitsHintImage,
                0.82 * gameState.getFieldWidth(),
                0.75 * gameState.getFieldHeight());
        drawImage(leftRightHintImage,
                0.82 * gameState.getFieldWidth(),
                0.8 * gameState.getFieldHeight());
        drawImage(upDownHintImage,
                0.82 * gameState.getFieldWidth(),
                0.85 * gameState.getFieldHeight());
    }

    @NotNull private WritableImage createTextImage(@NotNull String message) {
        var text = new Text(message);
        text.setFont(Font.font(20));
        var stackPane = new StackPane();
        stackPane.getChildren().add(text);
        var parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        return stackPane.snapshot(parameters, null);
    }

    private void createHintImages() {
        enterHintImage = createTextImage("ENTER to shoot");
        digitsHintImage = createTextImage("DIGITS to change bullet");
        leftRightHintImage = createTextImage("LEFT/RIGHT to move");
        upDownHintImage = createTextImage("UP/DOWN to rotate");
    }
}
