package rs.edu.raf.mtomic.snake;

import com.sun.javafx.UnmodifiableArrayList;
import javafx.util.Pair;
import rs.edu.raf.mtomic.snake.agent.PlayingAgent;
import rs.edu.raf.mtomic.snake.agent.player.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class GameState {

    private final FieldState[][] fields = new FieldState[28][31];
    private final List<PlayingAgent> agents;

    int[] pelletPosition;

    public GameState(Player player) {
        fillFieldStates();
        agents = new UnmodifiableArrayList<>(
                new PlayingAgent[]{player}, 1);
        player.setGameState(this);
    }

    public final List<PlayingAgent> getAgents() {
        return agents;
    }

    public final FieldState[][] getFields() {
        return fields.clone();
    }

    public final int[] getPelletPosition() {
        return pelletPosition.clone();
    }

    private void fillFieldStates() {
        try (Scanner sc = new Scanner(new File("level.txt"))) {
            for (int row = 0; row < 31; row++) {
                String next = sc.nextLine();
                for (int column = 0; column < 28; column++) {
                    switch (next.charAt(column)) {
                        case '*':
                            fields[column][row] = FieldState.B;
                            break;
                        case 'p':
                            fields[column][row] = FieldState.P;
                            pelletPosition = new int[]{column, row};
                            break;
                        case ' ':
                            fields[column][row] = FieldState.E;
                            break;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void updateFieldStates() {
        Set<Pair<Integer, Integer>> snakeFields = agents.get(agents.size() - 1).getUsedGridPositions();
        for (int i = 1; i < fields.length - 1; i++) {
            for (int j = 1; j < fields[i].length - 1; j++) {
                if (fields[i][j].equals(FieldState.B)) {
                    fields[i][j] = FieldState.E;
                }
            }
        }
        for (Pair<Integer, Integer> usedBlock : snakeFields) {
            fields[usedBlock.getKey()][usedBlock.getValue()] = FieldState.B;
        }
    }
}
