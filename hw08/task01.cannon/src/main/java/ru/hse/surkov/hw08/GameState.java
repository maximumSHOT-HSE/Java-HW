package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class GameState implements Drawable {

    /*
    * Game work is a rectangle,
    * so all coordinates have restriction
    * x's between zero and width inclusive,
    * y's between zero and height inclusive
    * */
    private double width;
    private double height;

    private Landscape landscape;
    private Cannon cannon;
    private List<Target> targets = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();

    public GameState(double width, double height) {
        this.width = width;
        this.height = height;
        landscape = new Landscape(width, height);
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        landscape.draw(graphicsContext);
    }
}
