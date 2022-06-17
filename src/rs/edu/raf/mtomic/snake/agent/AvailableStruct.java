package rs.edu.raf.mtomic.snake.agent;

import javafx.util.Pair;
import rs.edu.raf.mtomic.snake.Direction;

public class AvailableStruct {
    public final Direction direction;
    public final Pair<Integer, Integer> gridPosition;
    public final Runnable method;

    public AvailableStruct(Direction direction, Pair<Integer, Integer> gridPosition, Runnable method) {
        this.direction = direction;
        this.gridPosition = gridPosition;
        this.method = method;
    }

    @Override
    public String toString() {
        return
                "\n\t{\n\t\tdir = " + direction +
                        ",\n\t\tgridPos = " + gridPosition +
                        ",\n\t\tmethod = " + method +
                        "\n\t}\n";
    }
}
