package rs.edu.raf.mtomic.snake.agent;

import javafx.util.Pair;
import rs.edu.raf.mtomic.snake.Direction;
import rs.edu.raf.mtomic.snake.FieldState;
import rs.edu.raf.mtomic.snake.GameState;
import rs.edu.raf.mtomic.snake.sprite.Sprite;
import rs.edu.raf.mtomic.snake.sprite.SpriteLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public abstract class PlayingAgent {
    protected int RIGHT_OFFSET = 0;
    protected int LEFT_OFFSET = 2;
    protected int UP_OFFSET = 4;
    protected int DOWN_OFFSET = 6;
    protected int currentOffset = RIGHT_OFFSET;

    protected Sprite[] sprites = new Sprite[8];
    protected BufferedImage[] images;
    protected int activeSpriteId;
    protected int spriteCenterX;
    protected int spriteCenterY;
    protected int speed = 2; // frames for 1 pixel movement
    protected int frameJump = 8;
    protected GameState gameState;
    private int frameCounter = 0;
    private Direction currentDirection = Direction.LEFT;

    protected LinkedList<Pair<Integer, Integer>> usedPositions = new LinkedList<>();
    protected Set<Pair<Integer, Integer>> usedGridPositions = new HashSet<>();

    protected int eaten = 0;

    public PlayingAgent(GameState gameState) {
        this.gameState = gameState;
    }

    public BufferedImage getActiveSpriteImage() {
        return images[activeSpriteId];
    }

    public abstract BufferedImage getBodySpriteImage();

    public final void updateSprite() {
        activeSpriteId = currentOffset + ((1 + activeSpriteId) % (sprites.length / 4));
    }

    private boolean move() {
        frameCounter = (frameCounter + 1) % speed;
        return frameCounter == 0;
    }

    public void eat() {
        eaten++;
    }

    private boolean checkAvailableCoords(int centerX, int centerY) {
        int gridX = getGridX(centerX);
        int gridY = getGridY(centerY);
        return !gameState.getFields()[gridX][gridY].equals(FieldState.B);
    }

    public Set<Pair<Integer, Integer>> getUsedGridPositions() {
        return usedGridPositions;
    }

    private void adjustSpriteCenterY() {
        int mod = spriteCenterY % 8 - 4;
        if (mod > 0) {
            spriteCenterY-=frameJump;
        }
        if (mod < 0) {
            spriteCenterY+=frameJump;
        }
    }

    private void adjustSpriteCenterX() {
        int mod = spriteCenterX % 8 - 4;
        if (mod > 0) {
            spriteCenterX-=frameJump;
        }
        if (mod < 0) {
            spriteCenterX+=frameJump;
        }
    }

    public final void goLeft() {
        currentOffset = LEFT_OFFSET;
        currentDirection = Direction.LEFT;

        if (move()) {// && checkAvailableCoords(spriteCenterX - frameJump, spriteCenterY)) {
            spriteCenterX-=frameJump;
            adjustSpriteCenterY();
            // eaten?
            if (!gameState.getFields()[getGridX()][getGridY()].equals(FieldState.P)) {
                Pair<Integer, Integer> last = usedPositions.removeLast();
                usedGridPositions.remove(new Pair<>(getGridX(last.getKey()), getGridY(last.getValue())));
            }
            if (usedPositions.size() > 0) {
                usedGridPositions.add(new Pair<>(getGridX(usedPositions.getFirst().getKey()), getGridY(usedPositions.getFirst().getValue())));
            }
            usedPositions.addFirst(new Pair<>(spriteCenterX, spriteCenterY));
        }
    }

    public final void goRight() {
        currentOffset = RIGHT_OFFSET;
        currentDirection = Direction.RIGHT;
        if (move()) {// && checkAvailableCoords(spriteCenterX + frameJump, spriteCenterY)) {
            spriteCenterX+=frameJump;
            adjustSpriteCenterY();
            // eaten?
            if (!gameState.getFields()[getGridX()][getGridY()].equals(FieldState.P)) {
                Pair<Integer, Integer> last = usedPositions.removeLast();
                usedGridPositions.remove(new Pair<>(getGridX(last.getKey()), getGridY(last.getValue())));
            }
            if (usedPositions.size() > 0) {
                usedGridPositions.add(new Pair<>(getGridX(usedPositions.getFirst().getKey()), getGridY(usedPositions.getFirst().getValue())));
            }
            usedPositions.addFirst(new Pair<>(spriteCenterX, spriteCenterY));
        }
    }

    public final void goDown() {
        currentOffset = DOWN_OFFSET;
        currentDirection = Direction.DOWN;
        if (move()) {// && checkAvailableCoords(spriteCenterX, spriteCenterY + frameJump)) {
            spriteCenterY+=frameJump;
            adjustSpriteCenterX();
            // eaten?
            if (!gameState.getFields()[getGridX()][getGridY()].equals(FieldState.P)) {
                Pair<Integer, Integer> last = usedPositions.removeLast();
                usedGridPositions.remove(new Pair<>(getGridX(last.getKey()), getGridY(last.getValue())));
            }
            if (usedPositions.size() > 0) {
                usedGridPositions.add(new Pair<>(getGridX(usedPositions.getFirst().getKey()), getGridY(usedPositions.getFirst().getValue())));
            }
            usedPositions.addFirst(new Pair<>(spriteCenterX, spriteCenterY));
        }
    }

    public final void goUp() {
        currentOffset = UP_OFFSET;
        currentDirection = Direction.UP;
        if (move()) {// && checkAvailableCoords(spriteCenterX, spriteCenterY - frameJump)) {
            spriteCenterY-=frameJump;
            adjustSpriteCenterX();
            // eaten?
            if (!gameState.getFields()[getGridX()][getGridY()].equals(FieldState.P)) {
                Pair<Integer, Integer> last = usedPositions.removeLast();
                usedGridPositions.remove(new Pair<>(getGridX(last.getKey()), getGridY(last.getValue())));
            }
            if (usedPositions.size() > 0) {
                usedGridPositions.add(new Pair<>(getGridX(usedPositions.getFirst().getKey()), getGridY(usedPositions.getFirst().getValue())));
            }
            usedPositions.addFirst(new Pair<>(spriteCenterX, spriteCenterY));
        }
    }

    public final LinkedList<Pair<Integer, Integer>> getUsedPositions() {
        return usedPositions;
    }

    public final int getGridX() {
        return spriteCenterX / 8;
    }

    public final int getGridX(int centerX) {
        return centerX / 8;
    }

    public final int getGridY() {
        return spriteCenterY / 8;
    }

    public final int getGridY(int centerY) {
        return centerY / 8;
    }

    public final int getSpriteTopX() {
        return spriteCenterX - 7;
    }

    public final int getSpriteTopY() {
        return spriteCenterY - 7;
    }

    public abstract void playMove();

    protected abstract void fillSprites();

    public final void loadSpriteImages(BufferedImage spriteResourceImage) {
        final Color color = new Color(spriteResourceImage.getRGB(0, 0));
        fillSprites();
        images = new BufferedImage[sprites.length];
        for (int i = 0; i < sprites.length; i++) {
            Sprite active = sprites[i];
            images[i] =
                    SpriteLoader.makeTransparent(
                            spriteResourceImage.getSubimage(active.getX(), active.getY(), active.getW(), active.getH()),
                            color
                    );
        }
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }
}
