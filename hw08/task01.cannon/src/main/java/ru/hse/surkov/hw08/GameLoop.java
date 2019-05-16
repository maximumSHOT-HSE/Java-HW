package ru.hse.surkov.hw08;

import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameLoop extends AnimationTimer {

    private long previousNanoTime;
    private List<Engine> engines = new ArrayList<>();

    void addEngine(Engine engine) {
        engines.add(engine);
    }

    @Override
    public void handle(long currentNanoTime) {
        double deltaTime = TimeUnit.NANOSECONDS.toSeconds(currentNanoTime - previousNanoTime);
        previousNanoTime = currentNanoTime;
        for (var engine : engines) {
            engine.update(deltaTime);
        }
    }

    @Override
    public void start() {
        previousNanoTime = System.currentTimeMillis();
        super.start();
    }
}
