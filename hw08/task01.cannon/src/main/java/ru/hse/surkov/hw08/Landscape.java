package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Landscape implements Drawable {

    private static final double eps = 1e-9;

    private final double[] verticesHeights = new double[] {
        0.625, 0.4166666666666667, 0.6875, 0.630916666666666663, 0.3541666666666667,
        0.20833333333333334, 0.3125, 0.4625000000000001, 0.68333333333333334, 0.7708333333333334,
        0.4208333333333334, 0.4416666666666667, 0.80375, 0.7583333333333333, 0.6083333333333334
    };
    private final double[] verticesPositions = new double[] {
        0.0, 0.0875, 0.1375, 0.23125,
        0.2875, 0.4375, 0.575, 0.5875, 0.6125,
        0.6875, 0.7125, 0.8, 0.8, 0.9, 1.0
    };
    private final int POINTS_NUMBER = verticesHeights.length;

    private double fieldWidth;
    private double fieldHeight;
    private double[] vertexXCoordinates;
    private double[] vertexYCoordinates;

    public void generate() {
        vertexXCoordinates = new double[POINTS_NUMBER + 2];
        vertexYCoordinates = new double[POINTS_NUMBER + 2];
        for (int i = 0; i < POINTS_NUMBER; i++) {
            vertexXCoordinates[i] = verticesPositions[i] * fieldWidth;
            vertexYCoordinates[i] = fieldHeight - verticesHeights[i] * fieldHeight;
        }
        vertexXCoordinates[POINTS_NUMBER] = fieldWidth;
        vertexYCoordinates[POINTS_NUMBER] = fieldHeight - 0;
        vertexXCoordinates[POINTS_NUMBER + 1] = 0;
        vertexYCoordinates[POINTS_NUMBER + 1] = fieldHeight - 0;
    }

    public Landscape(double fieldWidth, double fieldHeight) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        generate();
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.rgb(25, 51, 0));
        graphicsContext.fillPolygon(vertexXCoordinates, vertexYCoordinates, POINTS_NUMBER + 2);
        graphicsContext.setFill(Color.rgb(51, 25, 0));
        graphicsContext.setLineWidth(5);
        for (int i = 0; i + 1 < POINTS_NUMBER; i++) {
            double fromX = verticesPositions[i] * fieldWidth;
            double fromY = fieldHeight - verticesHeights[i] * fieldHeight;
            double toX = verticesPositions[i + 1] * fieldWidth;
            double toY = fieldHeight - verticesHeights[i + 1] * fieldHeight;
            graphicsContext.strokeLine(fromX, fromY, toX, toY);
        }
    }

    public double getY(double x) {
        for (int i = 0; i + 1 < POINTS_NUMBER; i++) {
            double leftX = verticesPositions[i] * fieldWidth - eps;
            double rightX = verticesPositions[i + 1] * fieldWidth + eps;
            if (leftX <= x && x <= rightX) {
                double leftY = verticesHeights[i] * fieldHeight;
                double rightY = verticesHeights[i + 1] * fieldHeight;
                double ratio = (x - leftX) / (rightX - leftX);
                return rightY * ratio + (1 - ratio) * leftY;
            }
        }
        throw new IllegalArgumentException(
                "x should be within field borders, but found x = " + x +
                        ", when field fieldWidth = " + fieldWidth);
    }
}
