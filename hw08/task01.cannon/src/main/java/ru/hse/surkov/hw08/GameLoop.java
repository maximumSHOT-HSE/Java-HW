package ru.hse.surkov.hw08;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class GameLoop {

    private static final double FPS = 60;

    private long previousNanoTime;
    private List<Engine> engines = new ArrayList<>();
    private Timeline timeline = new Timeline();
    private KeyFrame keyFrame;

    public GameLoop() {
        timeline.setCycleCount(Timeline.INDEFINITE);
        keyFrame = new KeyFrame(
                Duration.seconds(1 / FPS),
                event -> handle(System.currentTimeMillis()));
        timeline.getKeyFrames().add(keyFrame);
    }

    void addEngine(Engine engine) {
        engines.add(engine);
    }

    public void handle(long currentNanoTime) {
        double deltaTime = (currentNanoTime - previousNanoTime) * 1e-3;
        previousNanoTime = currentNanoTime;
        for (var engine : engines) {
            engine.update(deltaTime);
        }
    }

    public void start() {
        previousNanoTime = System.currentTimeMillis();
        timeline.play();
    }

    public void stop() {
        timeline.stop();
    }
}
