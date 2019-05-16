package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Landscape implements Drawable {

    private final double[] verticesHeights = new double[] {
        0.625, 0.4166666666666667, 0.6875, 0.47916666666666663, 0.3541666666666667,
        0.20833333333333334, 0.3125, 0.5625000000000001, 0.8333333333333334, 0.7708333333333334,
        0.5208333333333334, 0.5416666666666667, 0.9375, 0.9583333333333333, 0.7083333333333334
    };
    private final double[] verticesPositions = new double[] {
        0.0, 0.0875, 0.1875, 0.23125,
        0.2875, 0.4375, 0.575, 0.5875, 0.6125,
        0.6875, 0.7125, 0.8, 0.8, 0.9, 1.0
    };
    private final int POINTS_NUMBER = verticesHeights.length;

    private double width;
    private double height;

    public Landscape(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.rgb(25, 51, 0));
        double[] xs = new double[POINTS_NUMBER + 2];
        double[] ys = new double[POINTS_NUMBER + 2];
        for (int i = 0; i < POINTS_NUMBER; i++) {
            xs[i] = verticesPositions[i] * width;
            ys[i] = height - verticesHeights[i] * height;
        }
        xs[POINTS_NUMBER] = width;
        ys[POINTS_NUMBER] = height - 0;
        xs[POINTS_NUMBER + 1] = 0;
        ys[POINTS_NUMBER + 1] = height - 0;
        graphicsContext.fillPolygon(xs, ys, POINTS_NUMBER + 2);

        graphicsContext.setFill(Color.rgb(51, 25, 0));
        graphicsContext.setLineWidth(5);
        for (int i = 0; i + 1 < POINTS_NUMBER; i++) {
            double fromX = verticesPositions[i] * width;
            double fromY = height - verticesHeights[i] * height;
            double toX = verticesPositions[i + 1] * width;
            double toY = height - verticesHeights[i + 1] * height;
            graphicsContext.moveTo(fromX, fromY);
            graphicsContext.lineTo(toX, toY);
            graphicsContext.stroke();
        }
    }
}
