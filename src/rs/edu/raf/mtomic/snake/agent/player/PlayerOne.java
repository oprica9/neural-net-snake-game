package rs.edu.raf.mtomic.snake.agent.player;

import rs.edu.raf.mtomic.snake.Direction;
import rs.edu.raf.mtomic.snake.GameState;
import rs.edu.raf.mtomic.snake.agent.AvailableStruct;

import java.util.ArrayList;
import java.util.List;

public class PlayerOne extends Player {
    private Runnable nextMove = this::goLeft;

    public PlayerOne(GameState gameState) {
        super(gameState);
    }

    @Override
    protected Runnable generateNextMove() {

        System.out.println("used pos x: " + getGridX() + ", used pos y: " + getGridY());

        int[] pelletPosition = gameState.getPelletPosition();
        List<AvailableStruct> lst = getAvailableFields();
        List<Direction> chosenDirections = new ArrayList<>();

        if (getGridY() > pelletPosition[1]) {
            chosenDirections.add(Direction.UP);
        } else if (getGridY() < pelletPosition[1]){
            chosenDirections.add(Direction.DOWN);
        }
        if (getGridX() < pelletPosition[0]) {
            chosenDirections.add(Direction.RIGHT);
        } else if (getGridX() > pelletPosition[0]) {
            chosenDirections.add(Direction.LEFT);
        }
        for (Direction d : chosenDirections) {
            for (AvailableStruct a : lst) {
                if (a.direction.equals(d)) {
                    return a.method;
                }
            }
        }
        if (lst.size() > 0) {
            return lst.get(0).method;
        } else {
            return this::goUp;
        }
    }

}
