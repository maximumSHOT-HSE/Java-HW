package ru.hse.surkov.hw08;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class CannonTest {

    private static final double GUN_WIDTH = 10;
    private static final double GUN_HEIGHT = 100;

    private static final double FIELD_WIDTH = 10;
    private static final double FIELD_HEIGHT = 100;

    @NotNull private GameState gameState;
    @NotNull private Cannon cannon;

    @BeforeEach
    void setUp() {
        gameState = new GameState(
            FIELD_WIDTH,
            FIELD_HEIGHT
        );
//        cannon = new Cannon(
//            gameState,
//            GUN_WIDTH,
//            GUN_HEIGHT,
//            0,
//            new Vector2D(0, gameState.)
//        );
    }
}