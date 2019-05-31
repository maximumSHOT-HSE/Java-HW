package ru.hse.surkov.hw08;

/**
 * Abstraction of the engine,
 * which can control some part
 * of the game.
 */
public interface Engine {

    /**
     * Eeach engine has its own state, which
     * should be updated at certain small time intervals.
     */
    void update(double deltaTime);
}
