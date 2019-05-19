package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;
import org.jetbrains.annotations.NotNull;

/**
 * Abstraction for the entities, which
 * can be drawn by javafx library.
 * */
public interface Drawable {

    /**
     * Method of drawing a certain entity
     * using javafx library.
     * */
    void draw(@NotNull GraphicsContext graphicsContext);
}
