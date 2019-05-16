package ru.hse.surkov.hw08;

import javafx.scene.shape.Circle;

public class Target {

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
}
