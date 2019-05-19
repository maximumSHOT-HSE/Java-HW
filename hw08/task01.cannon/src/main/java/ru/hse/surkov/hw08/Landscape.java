package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

/**
 * The abstraction for the game landscape.
 * Landscape is the broken line, which is represented
 * as the sequence of the points in two-dimensional space.
 * The points presented by their relative position on
 * the game screen.
 */
public class Landscape implements Drawable {

    private static final double eps = 1e-9;
    @NotNull private final double[] relativeVertexHeights = new double[] {
        0.3125, 0.20833333333333334, 0.34375, 0.31545833333333334, 0.17708333333333334,
        0.10416666666666667, 0.15625, 0.23125000000000004, 0.6416666666666667, 0.6854166666666667,
        0.2104166666666667, 0.22083333333333335, 0.401875, 0.37916666666666665, 0.3041666666666667
    };
    @NotNull private final double[] relativeVertexPositions = new double[] {
        0.0, 0.0875, 0.1375, 0.23125,
        0.2875, 0.4375, 0.575, 0.5875, 0.6125,
        0.6875, 0.7125, 0.8, 0.8000200002, 0.9, 1.0
    };
    private final int POINTS_NUMBER = relativeVertexHeights.length;

    private double fieldWidth;
    private double fieldHeight;
    @NotNull private double[] vertexReversedXCoordinates = new double[POINTS_NUMBER + 2];;
    @NotNull private double[] vertexReversedYCoordinates = new double[POINTS_NUMBER + 2];;

    private void generateVertexCoordinates() {
        for (int i = 0; i < POINTS_NUMBER; i++) {
            vertexReversedXCoordinates[i] = relativeVertexPositions[i] * fieldWidth;
            vertexReversedYCoordinates[i] = fieldHeight - relativeVertexHeights[i] * fieldHeight;
        }
        vertexReversedXCoordinates[POINTS_NUMBER] = fieldWidth;
        vertexReversedYCoordinates[POINTS_NUMBER] = fieldHeight - 0;
        vertexReversedXCoordinates[POINTS_NUMBER + 1] = 0;
        vertexReversedYCoordinates[POINTS_NUMBER + 1] = fieldHeight - 0;
    }

    public Landscape(double fieldWidth, double fieldHeight) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        generateVertexCoordinates();
    }

    /**
     * {@link Drawable#draw(GraphicsContext)}
     *
     * Draws the landscape as the broken line. Underground
     * of the landscape represented as the filled polygon.
     * */
    @Override
    public void draw(@NotNull GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.rgb(25, 51, 0));
        graphicsContext.fillPolygon(
                vertexReversedXCoordinates,
                vertexReversedYCoordinates,
                POINTS_NUMBER + 2);
        graphicsContext.setFill(Color.rgb(51, 25, 0));
        graphicsContext.setLineWidth(5);
        for (int i = 0; i + 1 < POINTS_NUMBER; i++) {
            double fromX = vertexReversedXCoordinates[i];
            double fromY = vertexReversedYCoordinates[i];
            double toX = vertexReversedXCoordinates[i + 1];
            double toY = vertexReversedYCoordinates[i + 1];
            graphicsContext.strokeLine(fromX, fromY, toX, toY);
        }
    }

    /**
     * Gives the y coordinate of the intersection landscape
     * and the vertical line, which describes by given x.
     * */
    public double getY(double x) {
        for (int i = 0; i + 1 < POINTS_NUMBER; i++) {
            double leftX = relativeVertexPositions[i] * fieldWidth - eps;
            double rightX = relativeVertexPositions[i + 1] * fieldWidth + eps;
            if (leftX <= x && x <= rightX) {
                double leftY = relativeVertexHeights[i] * fieldHeight;
                double rightY = relativeVertexHeights[i + 1] * fieldHeight;
                double ratio = (x - leftX) / (rightX - leftX);
                return rightY * ratio + (1 - ratio) * leftY;
            }
        }
        return fieldHeight * 2;
    }
}
