package rs.edu.raf.mtomic.snake.agent.player;

import javafx.util.Pair;
import rs.edu.raf.mtomic.snake.Direction;
import rs.edu.raf.mtomic.snake.FieldState;
import rs.edu.raf.mtomic.snake.GameState;
import rs.edu.raf.mtomic.snake.agent.AvailableStruct;
import rs.edu.raf.mtomic.snake.agent.PlayingAgent;
import rs.edu.raf.mtomic.snake.sprite.Sprite;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static rs.edu.raf.mtomic.snake.FieldState.B;
import static rs.edu.raf.mtomic.snake.MathUtils.nextLeftGridX;
import static rs.edu.raf.mtomic.snake.MathUtils.nextRightGridX;

public abstract class Player extends PlayingAgent {
    public Player(GameState gameState) {
        super(gameState);
        LEFT_OFFSET = 4;
        UP_OFFSET = 8;
        DOWN_OFFSET = 12;
        currentOffset = LEFT_OFFSET;
        activeSpriteId = LEFT_OFFSET;
        spriteCenterX = 14 * 8 + 4;
        spriteCenterY = 23 * 8 + 4;
        usedPositions.add(new Pair<>(spriteCenterX, spriteCenterY));
    }

    public final void playMove() {
        Runnable best = generateNextMove();
        best.run();
    }

    protected abstract Runnable generateNextMove();

    @Override
    protected final void fillSprites() {
        sprites = new Sprite[16];
        sprites[0] = Sprite.PAC_UP_OPEN;
        sprites[1] = sprites[3] = Sprite.PAC_UP_CLOSED;
        sprites[2] = Sprite.PAC_FULL_CLOSED;
        sprites[4] = Sprite.PAC_LEFT_OPEN;
        sprites[5] = sprites[7] = Sprite.PAC_LEFT_CLOSED;
        sprites[6] = Sprite.PAC_FULL_CLOSED;
        sprites[8] = Sprite.PAC_RIGHT_OPEN;
        sprites[9] = sprites[11] = Sprite.PAC_RIGHT_CLOSED;
        sprites[10] = Sprite.PAC_FULL_CLOSED;
        sprites[12] = Sprite.PAC_DOWN_OPEN;
        sprites[13] = sprites[15] = Sprite.PAC_DOWN_CLOSED;
        sprites[14] = Sprite.PAC_FULL_CLOSED;
    }

    protected final ArrayList<AvailableStruct> getAvailableFields() {
        ArrayList<AvailableStruct> list = new ArrayList<>();
        FieldState[][] fields = gameState.getFields();
        int posX = getGridX();
        int posY = getGridY();

        for (Direction d : Direction.values()) {
            switch (d) {
                case UP: {
                    if (fields[posX][posY - 1].equals(B) || getCurrentDirection() == Direction.DOWN) {
                        continue;
                    }
                    // take into consideration
                    list.add(new AvailableStruct(d, new Pair<>(posX, posY - 1), this::goUp));
                    break;
                }
                case LEFT: {
                    if (fields[nextLeftGridX(posX)][posY].equals(B) || getCurrentDirection() == Direction.RIGHT) {
                        continue;
                    }
                    // take into consideration
                    list.add(new AvailableStruct(d, new Pair<>(nextLeftGridX(posX), posY), this::goLeft));
                    break;
                }
                case DOWN: {
                    if (fields[posX][posY + 1].equals(B) || getCurrentDirection() == Direction.UP) {
                        continue;
                    }
                    // take into consideration
                    list.add(new AvailableStruct(d, new Pair<>(posX, posY + 1), this::goDown));
                    break;
                }
                case RIGHT: {
                    if (fields[nextRightGridX(posX)][posY].equals(B) || getCurrentDirection() == Direction.LEFT) {
                        continue;
                    }
                    // take into consideration
                    list.add(new AvailableStruct(d, new Pair<>(nextRightGridX(posX), posY), this::goRight));
                    break;
                }
            }
        }
        return list;
    }

    public final void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public BufferedImage getBodySpriteImage() {
        return images[14];
    }
}
