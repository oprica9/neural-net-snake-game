package rs.edu.raf.mtomic.snake.agent.player.evolution;

import rs.edu.raf.mtomic.snake.SnakeLike;
import rs.edu.raf.mtomic.snake.agent.player.PlayerTwo;

import java.util.ArrayList;

public class Controller {
    private GeneticAlgorithm ga;
    public static int POPULATION_SIZE = 500;
    public static int MAX_ITERATIONS = 50000;
    public static int genNum = 1;
    public static int index = 0;

    private ArrayList<PlayerTwo> population;

    public Controller() {
        this.ga = new GeneticAlgorithm();
        this.ga.MUTATION_RATE = 0.2;
        this.ga.MUTATION_STRENGTH = 0.4;
        this.ga.AMOUNT_SURVIVORS = 0.25;
        population = new ArrayList<>();
    }

    public void initGen() {
        initPopulation();
    }

    private void initPopulation() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(new PlayerTwo(null));
        }

        for (int i = 0; i < POPULATION_SIZE; i++) {
            SnakeLike snakeLike = new SnakeLike(population.get(i));
            try {
                snakeLike.join();
                population.get(i).setScore(snakeLike.getTotalPoints());
                population.get(i).setAlive(false);

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        if (!hasActive()) {
            nextGeneration();
        }
    }

    private void nextGeneration() {
        int passed = 1;
        while (passed < MAX_ITERATIONS) {
            //System.out.println("pop" + population);
            double max = 0;
            if (!hasActive()) {
                System.out.println("=== Gen = " + genNum++ + " ===");
                ga.evolve(population);
                passed++;

                for (int i = 0; i < POPULATION_SIZE; i++) {
                    PlayerTwo novi = new PlayerTwo(null);
                    novi.setNetwork(population.get(i).getNetwork());
                    population.set(i, novi);
                }

                for (int i = 0; i < POPULATION_SIZE; i++) {
                    SnakeLike snakeLike = new SnakeLike(population.get(i));
                    try {
                        snakeLike.join();
                        population.get(i).setScore(snakeLike.getTotalPoints());
                        population.get(i).setAlive(false);
                        if (max < snakeLike.getTotalPoints()) {
                            max = snakeLike.getTotalPoints();
                            index = i;
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                population.get(index).getNetwork().save_network("./src/rs/edu/raf/mtomic/snake/agent/player/evolution/rezultati/"+genNum+"-maxIndex"+index+".txt");
                System.out.println("Max points = " + max);
            }
        }

    }

    private PlayerTwo getFittest() {
        double maxFitness = -9999999;
        int index = 0;
        for (int i = 0; (i < this.population.size()); i++) {
            if ((this.population.get(i).getFitness() > maxFitness)) {
                maxFitness = this.population.get(i).getFitness();
                index = i;
            }
        }
        PlayerTwo fittest = this.population.get(index);
        // so we dont get the same fittest every time
        this.population.remove(fittest);
        return fittest;
    }

    private boolean hasActive() {
        for (PlayerTwo playerTwo : this.population) {
            if (playerTwo.isAlive()) {
                return true;
            }
        }
        return false;
    }
}
