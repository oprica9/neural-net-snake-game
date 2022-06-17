package rs.edu.raf.mtomic.snake.agent.player;

import rs.edu.raf.mtomic.snake.Direction;
import rs.edu.raf.mtomic.snake.FieldState;
import rs.edu.raf.mtomic.snake.GameState;
import rs.edu.raf.mtomic.snake.SnakeLike;
import rs.edu.raf.mtomic.snake.agent.player.evolution.GeneticClient;
import rs.edu.raf.mtomic.snake.agent.player.network.Network;
import rs.edu.raf.mtomic.snake.agent.player.network.NetworkBuilder;
import rs.edu.raf.mtomic.snake.agent.player.network.functions.activation.Sigmoid;
import rs.edu.raf.mtomic.snake.agent.player.network.layers.DenseLayer;
import rs.edu.raf.mtomic.snake.agent.player.network.tools.ArrayTools;

import java.util.*;

import static rs.edu.raf.mtomic.snake.agent.player.evolution.Controller.genNum;

public class PlayerTwo extends Player implements GeneticClient {
    private Runnable nextMove = this::goLeft;

    private boolean alive;
    private double score;
    private double fitness;
    private int step_count;
    private final Direction[] directions = new Direction[4];
    private Direction prev;
    private int counter = 0;
    private int curr_score = 0;

    private Network network;

    public PlayerTwo(GameState gameState) {
        super(gameState);
//        NetworkBuilder networkBuilder = new NetworkBuilder(1, 1, 24);
//        networkBuilder.addLayer(new DenseLayer(16));
//        networkBuilder.addLayer(new DenseLayer(10));
//        networkBuilder.addLayer(new DenseLayer(4).setActivationFunction(new Sigmoid()));
//        this.network = networkBuilder.buildNetwork();
        this.network = Network.load_network("./src/rs/edu/raf/mtomic/snake/agent/player/evolution/rezultati/2052-maxIndex161.txt");
        step_count = 0;
        directions[0] = Direction.UP;
        directions[1] = Direction.LEFT;
        directions[2] = Direction.DOWN;
        directions[3] = Direction.RIGHT;
        this.alive = true;
    }

    @Override
    protected Runnable generateNextMove() {
        if (genNum < 1000) {
            if (step_count > 500) {
                return this::goUp;
            }
        } else if (genNum < 5000) {
            if (step_count > 800) {
                return this::goUp;
            }
        } else if (genNum < 10000) {
            if (step_count > 1000) {
                return this::goUp;
            }
        } else if (genNum < 25000) {
            if (step_count > 1500) {
                return this::goUp;
            }
        } else if (genNum < 35000) {
            if (step_count > 1800) {
                return this::goUp;
            }
        } else if (genNum < 45000) {
            if (step_count > 2500) {
                return this::goUp;
            }
        } else if (genNum < 50000) {
            if (step_count > 3000) {
                return this::goUp;
            }
        }
        // jer se zove 2 puta
        if (counter % 2 == 0) {
            if (prev != null) {
                switch (prev) {
                    case UP:
                        counter++;
                        return this::goUp;
                    case DOWN:
                        counter++;
                        return this::goDown;
                    case RIGHT:
                        counter++;
                        return this::goRight;
                    case LEFT:
                        counter++;
                        return this::goLeft;
                }
            }
        }
        counter++;
        step_count++;

        double[][][] acc = network.calculate(getInputs());

        double[] res = ArrayTools.convertFlattenedArray(acc);

        //System.out.println(Arrays.toString(res));

        // top-left-bottom-right
        LinkedHashMap<Direction, Double> map = new LinkedHashMap<>();
        if (getCurrentDirection().equals(Direction.UP) || getCurrentDirection().equals(Direction.LEFT) || getCurrentDirection().equals(Direction.RIGHT))
            map.put(Direction.UP, res[0]);
        if (getCurrentDirection().equals(Direction.UP) || getCurrentDirection().equals(Direction.LEFT) || getCurrentDirection().equals(Direction.DOWN))
            map.put(Direction.LEFT, res[1]);
        if (getCurrentDirection().equals(Direction.DOWN) || getCurrentDirection().equals(Direction.LEFT) || getCurrentDirection().equals(Direction.RIGHT))
            map.put(Direction.DOWN, res[2]);
        if (getCurrentDirection().equals(Direction.UP) || getCurrentDirection().equals(Direction.DOWN) || getCurrentDirection().equals(Direction.RIGHT))
            map.put(Direction.RIGHT, res[3]);

        map = sortByValue(map);

        Direction best = null;

        int count = 1;

        for (Map.Entry<Direction, Double> it :
                map.entrySet()) {
            if (count == map.size()) {
                best = it.getKey();
            }
            count++;
        }

        //System.out.println(best);

        if (best != null) {
            switch (best) {
                case UP:
                    prev = Direction.UP;
                    return this::goUp;
                case DOWN:
                    prev = Direction.DOWN;
                    return this::goDown;
                case RIGHT:
                    prev = Direction.RIGHT;
                    return this::goRight;
                case LEFT:
                    prev = Direction.LEFT;
                    return this::goLeft;
            }
        }
        // if all else fails somehow
        return this::goLeft;
    }

    public Runnable getNextMove() {
        return nextMove;
    }

    public void setNextMove(Runnable nextMove) {
        this.nextMove = nextMove;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public Network getNetwork() {
        return network;
    }

    @Override
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public double getFitness() {
        int steps = step_count / 2;
        double val = steps + (Math.pow(2, score) + Math.pow(score, 2.1) * 500) - Math.pow(score, 1.2) * Math.pow((0.25 * steps), 1.3);
        //        this.fitness = score * 8000;
//        if (!this.alive) {
//            this.fitness -= 2000;
//        }
//        this.fitness -= step_count * 10;
        this.fitness = val;
        return val;
    }

    public double[][][] getInputs() {

        double[][][] inputsFinal = new double[1][8][3];

        double[][][] inputsFinal2 = new double[1][1][24];

        for (int i = 0; i < 8; i++) {
            inputsFinal[0][i] = getVectorInDirection(i);
        }

        int k = 0;
        for (int i = 0; i < inputsFinal[0].length; i++) {
            for (int j = 0; j < inputsFinal[0][i].length; j++) {
                inputsFinal2[0][0][k] = inputsFinal[0][i][j];
                k++;
            }
        }

        return inputsFinal2;
    }

    public static LinkedHashMap<Direction, Double> sortByValue(LinkedHashMap<Direction, Double> hm) {
        List<Map.Entry<Direction, Double>> list = new LinkedList<>(hm.entrySet());
        list.sort(Map.Entry.comparingByValue());
        LinkedHashMap<Direction, Double> temp = new LinkedHashMap<>();
        for (Map.Entry<Direction, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public double[] getVectorInDirection(int direction) {

        double[] value = new double[3];

        FieldState[][] fields = gameState.getFields();

        int[][] bodyPos = new int[getUsedPositions().size()][2];
//        System.out.println("used pos x: " + getGridX() + ", used pos y: " + getGridY());

        for (int i = 0; i < getUsedPositions().size(); i++) {
            int[] val = {getUsedPositions().get(i).getKey(), getUsedPositions().get(i).getValue()};
            bodyPos[i] = val;
        }

        int[][] polja = new int[28][31];

        for (int j = 0; j < fields[0].length; j++) {
            for (int i = 0; i < fields.length; i++) {
                if (fields[i][j].equals(FieldState.P))
                    polja[i][j] = 2;
                if (fields[i][j].equals(FieldState.B)) {
                    polja[i][j] = 1;
                }
                if (fields[i][j].equals(FieldState.E)) {
                    polja[i][j] = 0;
                    for (int[] zmija : bodyPos) {
                        // System.out.println("i = " + i + ", zmija[0] = " + zmija[0]/8 + ", j = " + j + ", zmija[1] = " + zmija[1]/8);
                        if (i == zmija[0] / 8 && j == zmija[1] / 8) {
                            polja[i][j] = 3;
                            break;
                        }
                    }
                }
            }
        }

        int[] headPos = new int[]{getUsedPositions().get(0).getKey(), getUsedPositions().get(0).getValue()};
        int x = headPos[0] / 8;
        int y = headPos[1] / 8;

//        for (int i = 0; i < polja.length; i++) {
//            System.out.println(Arrays.toString(polja[i]));
//        }

        int distance = 0;
        int distance_p = 0;
        boolean found_p = false;
        int distance_b = 0;
        boolean found_b = false;

        // 0 - levo
        if (direction == 0) {
            x = x - 1;
            while (polja[x][y] != 1) {
                int vrednost = polja[x][y];
                switch (vrednost) {
                    case 2:
                        value[1] = distance_p;
                        break;
                    case 3:
                        value[2] = distance_b;
                        break;
                }
                distance++;
                if (!found_b) {
                    found_b = true;
                    distance_b++;
                }
                if (!found_p) {
                    found_p = true;
                    distance_p++;
                }
                x--;
            }
        }
        // 1 - desno
        if (direction == 1) {
            x = x + 1;
            while (polja[x][y] != 1) {
                int vrednost = polja[x][y];
                switch (vrednost) {
                    case 2:
                        value[1] = distance_p;
                        break;
                    case 3:
                        value[2] = distance_b;
                        break;
                }
                distance++;
                if (!found_b) {
                    found_b = true;
                    distance_b++;
                }
                if (!found_p) {
                    found_p = true;
                    distance_p++;
                }
                x++;
            }
        }
        // 2 - dole
        if (direction == 2) {
            y = y + 1;
            while (polja[x][y] != 1) {
                int vrednost = polja[x][y];
                switch (vrednost) {
                    case 2:
                        value[1] = distance_p;
                        break;
                    case 3:
                        value[2] = distance_b;
                        break;
                }
                distance++;
                if (!found_b) {
                    found_b = true;
                    distance_b++;
                }
                if (!found_p) {
                    found_p = true;
                    distance_p++;
                }
                y++;
            }
        }
        // 3 - gore
        if (direction == 3) {
            y = y - 1;
            while (polja[x][y] != 1) {
                int vrednost = polja[x][y];
                switch (vrednost) {
                    case 2:
                        value[1] = distance_p;
                        break;
                    case 3:
                        value[2] = distance_b;
                        break;
                }
                distance++;
                if (!found_b) {
                    found_b = true;
                    distance_b++;
                }
                if (!found_p) {
                    found_p = true;
                    distance_p++;
                }
                y--;
            }
        }
        // 4 - gore-desno
        if (direction == 4) {
            x = x + 1;
            y = y + 1;
            while (polja[x][y] != 1) {
                int vrednost = polja[x][y];
                switch (vrednost) {
                    case 2:
                        value[1] = distance_p;
                        break;
                    case 3:
                        value[2] = distance_b;
                        break;
                }
                distance++;
                if (!found_b) {
                    found_b = true;
                    distance_b++;
                }
                if (!found_p) {
                    found_p = true;
                    distance_p++;
                }
                x++;
                y++;
            }
        }
        // 5 - gore-levo
        if (direction == 5) {
            x = x - 1;
            y = y + 1;
            while (polja[x][y] != 1) {
                int vrednost = polja[x][y];
                switch (vrednost) {
                    case 2:
                        value[1] = distance_p;
                        break;
                    case 3:
                        value[2] = distance_b;
                        break;
                }
                distance++;
                if (!found_b) {
                    found_b = true;
                    distance_b++;
                }
                if (!found_p) {
                    found_p = true;
                    distance_p++;
                }
                x--;
                y++;
            }
        }
        // 6 - dole-desno
        if (direction == 6) {
            x = x + 1;
            y = y - 1;
            while (polja[x][y] != 1) {
                int vrednost = polja[x][y];
                switch (vrednost) {
                    case 2:
                        value[1] = distance_p;
                        break;
                    case 3:
                        value[2] = distance_b;
                        break;
                }
                distance++;
                if (!found_b) {
                    found_b = true;
                    distance_b++;
                }
                if (!found_p) {
                    found_p = true;
                    distance_p++;
                }
                x++;
                y--;
            }
        }
        // 7 - dole-levo
        if (direction == 7) {
            x = x - 1;
            y = y - 1;
            while (polja[x][y] != 1) {
                int vrednost = polja[x][y];
                switch (vrednost) {
                    case 2:
                        value[1] = distance_p;
                        break;
                    case 3:
                        value[2] = distance_b;
                        break;
                }
                distance++;
                if (!found_b) {
                    found_b = true;
                    distance_b++;
                }
                if (!found_p) {
                    found_p = true;
                    distance_p++;
                }
                x--;
                y--;
            }
        }
        value[0] = distance;

//        System.out.println("used pos x: " + getGridX() + ", used pos y: " + getGridY());

//        for (int i = 0; i < polja.length; i++) {
//            System.out.println(Arrays.toString(polja[i]));
//        }
//
//        System.out.println(Arrays.toString(value));
        return value;
    }


    // no time
    public double angleWithPellet() {
        int pellet_x = gameState.getPelletPosition()[0];
        int pellet_y = gameState.getPelletPosition()[1];

        int[] headPos = new int[]{getUsedPositions().get(0).getKey(), getUsedPositions().get(0).getValue()};
        int head_x = headPos[0] / 8;
        int head_y = headPos[1] / 8;

        return Math.toDegrees(Math.atan2(pellet_y - head_y, pellet_x - head_x));
    }

    // no time
    public double generateNextDirection(int[] snakePosition, double angle) {
        double direction = 0;

        if (angle > 0) {
            direction = 1;
        } else if (angle < 0) {
            direction = -1;
        } else {
            direction = 0;
        }

        //double[] curr_direction_vector = snakePosition[0] - snakePosition[1];
        //double left_direction_vector = curr_direction_vector[1] - curr_direction_vector[0];


        return 0d;
    }

    @Override
    public String toString() {
        return "network:" + network + ", step count: " + step_count;
    }
}
