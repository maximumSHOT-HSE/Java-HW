package ru.hse.surkov.hw08;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstraction for game iteration, on each
 * of which all engines should update their states.
 */
public class GameLoop {

    private static final double FPS = 60;
    private static final double MILLISECONDS_IN_SECOND = 1e3;

    private long previousNanoTime;
    @NotNull private List<Engine> engines = new ArrayList<>();
    @NotNull private Timeline timeline = new Timeline();

    public GameLoop() {
        timeline.setCycleCount(Timeline.INDEFINITE);
        var keyFrame = new KeyFrame(
                Duration.seconds(1 / FPS),
                event -> handle(System.currentTimeMillis()));
        timeline.getKeyFrames().add(keyFrame);
    }

    /**
     * Adds the engines to the list of controllable engines.
     */
    public void addEngine(@NotNull Engine engine) {
        engines.add(engine);
    }

    // translates time from milliseconds to seconds
    private double millisecondsToSeconds(long milliseconds) {
        return milliseconds / MILLISECONDS_IN_SECOND;
    }

    private void handle(long currentNanoTime) {
        double deltaTime = millisecondsToSeconds(currentNanoTime - previousNanoTime);
        previousNanoTime = currentNanoTime;
        for (var engine : engines) {
            engine.update(deltaTime);
        }
    }

    /**
     * Starts the game loop.
     */
    public void start() {
        previousNanoTime = System.currentTimeMillis();
        timeline.play();
    }

    /**
     * Finishes the game loop.
     */
    public void stop() {
        timeline.stop();
    }
}
