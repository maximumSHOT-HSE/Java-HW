package ru.hse.surkov.hw08;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstraction for game iteration, on each
 * of which all engines should update their states.
 * */
public class GameLoop {

    private static final double FPS = 60;

    private long previousNanoTime;
    private List<Engine> engines = new ArrayList<>();
    private Timeline timeline = new Timeline();

    public GameLoop() {
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(1 / FPS),
                event -> handle(System.currentTimeMillis()));
        timeline.getKeyFrames().add(keyFrame);
    }

    /**
     * Adds the engines to the list of controllable engines.
     * */
    public void addEngine(Engine engine) {
        engines.add(engine);
    }

    private void handle(long currentNanoTime) {
        double deltaTime = (currentNanoTime - previousNanoTime) * 1e-3;
        previousNanoTime = currentNanoTime;
        for (var engine : engines) {
            engine.update(deltaTime);
        }
    }

    /**
     * Starts the game loop.
     * */
    public void start() {
        previousNanoTime = System.currentTimeMillis();
        timeline.play();
    }

    /**
     * Finishes the game loop.
     * */
    public void stop() {
        timeline.stop();
    }
}
