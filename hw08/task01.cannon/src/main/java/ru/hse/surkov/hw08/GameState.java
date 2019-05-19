package ru.hse.surkov.hw08;

import javafx.scene.canvas.GraphicsContext;

import java.util.*;

/**
 * Abstraction for the game stae, which stores
 * all parameters of the game namely information
 * about
 * bullets, targets, detonations, landscape, cannon,
 * landscape and which keys on the keyboard are pressed.
 */
public class GameState implements Drawable {

    private static final int MAX_TARGETS_NUMBER = 10;
    private static final Random random = new Random(System.currentTimeMillis());

    /**
     * The state of the game.
     * */
    public enum GameStatus {
        IN_PROGRESS,
        FINISHED
    }

    private GameStatus gameStatus = GameStatus.IN_PROGRESS;

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    /*
    * Game field is a rectangle,
    * so all coordinates have restriction
    * x's between zero and width inclusive,
    * y's between zero and height inclusive
    * */
    private double fieldWidth;
    private double fieldHeight;

    private Landscape landscape;
    private Cannon cannon;
    private Set<Target> targets = new HashSet<>();
    private Set<Bullet> bullets = new HashSet<>();
    private Set<String> activeKeys = new HashSet<>();
    private Set<Detonation> detonations = new HashSet<>();

    /**
     * Adds the detonation to the list of processed detonations.
     * */
    public void addDetonation(double x, double radius) {
        detonations.add(
            new Detonation(
                this,
                new Vector2D(x, landscape.getY(x)),
                radius
            )
        );
    }

    public Set<Bullet> getBullets() {
        return bullets;
    }

    public double getFieldWidth() {
        return fieldWidth;
    }

    public Set<Target> getTargets() {
        return targets;
    }

    public Set<Detonation> getDetonations() {
        return detonations;
    }

    public Landscape getLandscape() {
        return landscape;
    }

    public Set<String> getActiveKeys() {
        return activeKeys;
    }

    public double getFieldHeight() {
        return fieldHeight;
    }

    /**
     * Adds the key to the processed keys in terms of
     * its key code, which is represented as the String.
     * */
    public void addKey(String keyCode) {
        if (keyCode.equals("ENTER")) {
            fire();
        } else if (keyCode.startsWith("DIGIT")) {
            cannon.setCurrentMassId(Integer.parseInt(keyCode.substring(5)) - 1);
        } else {
            activeKeys.add(keyCode);
        }
    }

    /**
     * Removes the key from the processed keys.
     * */
    public void removeKey(String keyCode) {
        activeKeys.remove(keyCode);
    }

    private void generateTargets() {
        int targetsNumber = random.nextInt(MAX_TARGETS_NUMBER) + 1;
        for (int i = 0; i < targetsNumber; i++) {
            double x = random.nextDouble() * fieldWidth;
            double y = landscape.getY(x);
            double radius = (random.nextInt(3) + 1) * 0.005 * fieldWidth;
            targets.add(new Target(this, new Vector2D(x, y), radius));
        }
    }

    public GameState(double fieldWidth, double fieldHeight) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        landscape = new Landscape(fieldWidth, fieldHeight);
        cannon = new Cannon(
                this,
                0.01 * fieldWidth,
                0.12 * fieldHeight,
                0,
                new Vector2D(fieldWidth / 2,
                        landscape.getY(fieldWidth / 2)));
        generateTargets();
    }

    /**
     * {@link Drawable#draw(GraphicsContext)}
     *
     * Draws the game by drawing each component of the game
     * in appropriate order.
     * */
    @Override
    public void draw(GraphicsContext graphicsContext) {
        landscape.draw(graphicsContext);
        cannon.draw(graphicsContext);
        for (Bullet bullet : bullets) {
            bullet.draw(graphicsContext);
        }
        for (Target target : targets) {
            target.draw(graphicsContext);
        }
        var iterator = detonations.iterator();
        while (iterator.hasNext()) {
            var detonation = iterator.next();
            if (detonation.getRemainingLifeTime() < 0) {
                iterator.remove();
                continue;
            }
            detonation.decreaseLifeTime();
            detonation.draw(graphicsContext);
        }
    }

    /**
     * Changes the cannon position at the landscape.
     * */
    public void moveCannon(double deltaX) {
        Vector2D cannonBase = cannon.getBase();
        double targetX = Math.max(0.0, Math.min(fieldWidth, cannonBase.getX() + deltaX));
        cannon.setBase(new Vector2D(
                targetX,
                landscape.getY(targetX)
        ));
    }

    /**
     * Changes the cannon angle.
     */
    public void rotateCannon(double deltaAngle) {
        double cannonAngle = cannon.getAngle();
        cannon.setAngle(cannonAngle + deltaAngle);
    }

    /**
     * Makes the gun fire. As the consequence new bullet
     * will be generated.
     * */
    public void fire() {
        bullets.add(cannon.generateBullet());
    }
}
