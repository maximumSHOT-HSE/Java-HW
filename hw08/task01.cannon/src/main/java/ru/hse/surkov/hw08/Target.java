package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Circle;

public class Target implements Drawable {

    private Circle body;

    public Target(Circle body) {
        this.body = body;
    }

    public Circle getBody() {
        return body;
    }

    public void setBody(Circle body) {
        this.body = body;
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {

    }
}
