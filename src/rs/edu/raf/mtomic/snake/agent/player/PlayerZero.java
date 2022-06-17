package rs.edu.raf.mtomic.snake.agent.player;

import javafx.util.Pair;
import rs.edu.raf.mtomic.snake.Direction;
import rs.edu.raf.mtomic.snake.FieldState;
import rs.edu.raf.mtomic.snake.GameState;
import rs.edu.raf.mtomic.snake.agent.AvailableStruct;

import java.util.*;

import static rs.edu.raf.mtomic.snake.MathUtils.nextLeftGridX;
import static rs.edu.raf.mtomic.snake.MathUtils.nextRightGridX;

// FAILED
public class PlayerZero extends Player {

//    private final NeuralNet nn;

    private ArrayList<Double> netOutputs = new ArrayList<>();
    private ArrayList<Double> mapOutputs = new ArrayList<>();

    private int score = 0;
    private int step_count = 0;
    private float fitness_score = 0;
    private boolean isDead = false;
    private final Direction[] directions = new Direction[4];
    private Direction prevDirection;
    private boolean moved = false;
    private int[] pelletPos;
    private int[] prevPelletPos;

    public PlayerZero(GameState gameState) {
        super(gameState);
        int[] layers = {8, 12, 4};
        // nn = new NeuralNet();
        directions[0] = Direction.UP;
        directions[1] = Direction.LEFT;
        directions[2] = Direction.DOWN;
        directions[3] = Direction.RIGHT;
    }

    @Override
    protected Runnable generateNextMove() {
        if (moved) {
            switch (prevDirection) {
                case UP:
                    moved = false;
                    return this::goUp;
                case LEFT:
                    moved = false;
                    return this::goLeft;
                case DOWN:
                    moved = false;
                    return this::goDown;
                case RIGHT:
                    moved = false;
                    return this::goRight;
            }

        }
        step_count++;
        ArrayList<Double> inputs = getOutputs();
        try {
            update();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
//            netOutputs = nn.update(inputs);

            Map<Direction, Double> output = new HashMap<>();

            for (int i = 0; i < 4; i++) {
                output.put(directions[i], netOutputs.get(i));
            }

            List<Direction> available_moves = new ArrayList<>();

            for (Direction d : Direction.values()) {
                if (d != getCurrentDirection())
                    available_moves.add(d);
            }
            Direction next = null;
            float max_value = -99999;

            for (Map.Entry<Direction, Double> set : output.entrySet()) {

                if (available_moves.contains(set.getKey())) {
                    if (max_value < set.getValue()) {
                        next = set.getKey();
                    }
                }
            }

            System.out.println("Output values = " + output);
            System.out.println("Chosen next move = " + next);

            moved = true;
            prevDirection = next;
            if (next != null)
                switch (next) {
                    case UP:
                        return this::goUp;
                    case LEFT:
                        return this::goLeft;
                    case DOWN:
                        return this::goDown;
                    case RIGHT:
                        return this::goRight;
                }


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("NEKAKO JE DOSLO DO OVDE");

        return this::goLeft;

    }

    public float fitness(int totalPoints) {
        fitness_score = (float) (step_count + (Math.pow(2f, totalPoints) + Math.pow(totalPoints, 2.1f)) * 500 - (Math.pow(totalPoints, 1.2f) * Math.pow(0.25f * step_count, 1.3)));
        return fitness_score;
    }

    public void setDead(boolean dead, int score) {
        isDead = dead;
        this.score = score;
        fitness_score = fitness(this.score);
    }

    // public NeuralNet getNn() {
//        return nn;
//}

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStep_count() {
        return step_count;
    }

    public void setStep_count(int step_count) {
        this.step_count = step_count;
    }

    public float getFitness_score() {
        return fitness_score;
    }

    public void setFitness_score(float fitness_score) {
        this.fitness_score = fitness_score;
    }

    public boolean isDead() {
        return isDead;
    }

    private ArrayList<AvailableStruct> getAvailableFields2() {
        ArrayList<AvailableStruct> list = new ArrayList<>();
        FieldState[][] fields = gameState.getFields();
        int posX = getGridX();
        int posY = getGridY();

        for (Direction d : Direction.values()) {
            switch (d) {
                case UP: {
                    if (getCurrentDirection() == Direction.DOWN) {
                        continue;
                    }
                    // take into consideration
                    list.add(new AvailableStruct(d, new Pair<>(posX, posY - 1), this::goUp));
                    break;
                }
                case LEFT: {
                    if (getCurrentDirection() == Direction.RIGHT) {
                        continue;
                    }
                    // take into consideration
                    list.add(new AvailableStruct(d, new Pair<>(nextLeftGridX(posX), posY), this::goLeft));
                    break;
                }
                case DOWN: {
                    if (getCurrentDirection() == Direction.UP) {
                        continue;
                    }
                    // take into consideration
                    list.add(new AvailableStruct(d, new Pair<>(posX, posY + 1), this::goDown));
                    break;
                }
                case RIGHT: {
                    if (getCurrentDirection() == Direction.LEFT) {
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

    public void update() throws Exception {
        mapOutputs = getOutputs();
//        netOutputs = nn.update(mapOutputs);
//        map.updateWithInput(netOutputs);
    }

    private ArrayList<Double> getOutputs() {
        int[] inputs = new int[8];

        int head_pos_x = getUsedPositions().get(0).getKey();
        int head_pos_y = getUsedPositions().get(0).getValue();
        inputs[0] = head_pos_x;
        inputs[1] = head_pos_y;

        int food_pos_x = gameState.getPelletPosition()[0];
        int food_pos_y = gameState.getPelletPosition()[1];
        inputs[2] = food_pos_x;
        inputs[3] = food_pos_y;

        int left_block = 0;
        int right_block = 0;
        int up_block = 0;
        int down_block = 0;

        List<AvailableStruct> lst = getAvailableFields();

        for (AvailableStruct availableStruct : lst) {
            switch (availableStruct.direction) {
                case LEFT:
                    left_block = 1;
                    break;
                case DOWN:
                    down_block = 1;
                    break;
                case RIGHT:
                    right_block = 1;
                    break;
                case UP:
                    up_block = 1;
                    break;
            }
        }
        inputs[4] = up_block;
        inputs[5] = left_block;
        inputs[6] = down_block;
        inputs[7] = right_block;

        ArrayList<Double> inputs_float = new ArrayList<>();
        for (int i = 0; i < inputs.length; i++) {
            inputs_float.add((double) inputs[i]);
        }
        return inputs_float;
    }

    public ArrayList<Double> getNetOutputs() {
        return netOutputs;
    }

    public void setNetOutputs(ArrayList<Double> netOutputs) {
        this.netOutputs = netOutputs;
    }

    public ArrayList<Double> getMapOutputs() {
        return mapOutputs;
    }

    public void setMapOutputs(ArrayList<Double> mapOutputs) {
        this.mapOutputs = mapOutputs;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public Direction[] getDirections() {
        return directions;
    }

    public Direction getPrevDirection() {
        return prevDirection;
    }

    public void setPrevDirection(Direction prevDirection) {
        this.prevDirection = prevDirection;
    }

    public int[] getPelletPos() {
        return pelletPos;
    }

    public void setPelletPos() {
        this.pelletPos = gameState.getPelletPosition();
    }

    public int[] getPrevPelletPos() {
        return prevPelletPos;
    }

    public void setPrevPelletPos(int[] prevPelletPos) {
        this.prevPelletPos = prevPelletPos;
    }
}
