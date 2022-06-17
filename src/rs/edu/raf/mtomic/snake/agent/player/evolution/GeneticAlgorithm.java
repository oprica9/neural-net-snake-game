package rs.edu.raf.mtomic.snake.agent.player.evolution;

import rs.edu.raf.mtomic.snake.agent.player.network.layers.DenseLayer;
import rs.edu.raf.mtomic.snake.agent.player.network.layers.Layer;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {

    public double MUTATION_RATE = 0.25;
    public double MUTATION_STRENGTH = 0.2;
    public double AMOUNT_SURVIVORS = 50;

    public <T extends GeneticClient> void evolve(ArrayList<T> clients) {
        clients.sort((Comparator<GeneticClient>) (o1, o2) -> Double.compare(o2.getScore(), o1.getScore()));

        ArrayList<T> selection = selection(clients);
        crossover(clients, selection);
        mutate(clients);
        merge(clients, selection);
    }

    public static <T extends GeneticClient> void printClients(ArrayList<T> clients) {
        int index = 0;
        double[] in = new double[]{0.6, 0.4, 0.6, 0.1};
        for (T t : clients) {
            index++;
            System.out.format("index %3s     score %-20s alive: %-10s \n", index, t.getScore(), t.getNetwork());
        }
    }

    protected <T extends GeneticClient> ArrayList<T> selection(ArrayList<T> clients) {
        ArrayList<T> selection = new ArrayList<>();

        for (int i = 0; i < Math.min(AMOUNT_SURVIVORS, clients.size()); i++) {
            selection.add(clients.get(i));
        }
        for (GeneticClient g : selection) {
            clients.remove(g);
        }

        //System.out.println("=== SELECTION === :" + selection);

        return selection;
    }

    protected <T extends GeneticClient> void merge(ArrayList<T> newborns, ArrayList<T> survivors) {
        newborns.addAll(survivors);
    }

    protected <T extends GeneticClient> void crossover(ArrayList<T> clients, ArrayList<T> selection) {

        double parentSum = 0;
        for (GeneticClient c : selection) {
            parentSum += c.getScore();
        }
        // System.out.println("parent sum = " + parentSum);
        for (GeneticClient g : clients) {

            double r = Math.random();
            double c = 0;
            GeneticClient parent = null;
            for (GeneticClient sel : selection) {
                // System.out.println("sel.score = " + sel.getScore());
                c += (sel.getScore() + 0.0001) / (parentSum + 0.0001);
                // System.out.println("c = " + c + ", r = " + r);
                if (r < c) {
                    parent = sel;
                    break;
                }
            }

            Layer left = g.getNetwork().getInputLayer();
            Layer right = parent.getNetwork().getInputLayer();

            while (left != g.getNetwork().getOutputLayer()) {
                left = left.getNext_layer();
                right = right.getNext_layer();
                if (left instanceof DenseLayer && right instanceof DenseLayer) {
                    ((DenseLayer) left).setWeights(copyArray(((DenseLayer) right).getWeights()));
                    ((DenseLayer) left).setBias(copyArray(((DenseLayer) right).getBias()));
                }
            }

        }
    }

    protected <T extends GeneticClient> List<T> blxCrossover(T p1, T p2) {
        Layer left = p1.getNetwork().getInputLayer();
        Layer right = p2.getNetwork().getInputLayer();

        while (left != p1.getNetwork().getOutputLayer()) {
            left = left.getNext_layer();
            right = right.getNext_layer();
            if (left instanceof DenseLayer && right instanceof DenseLayer) {
                ((DenseLayer) left).setWeights(copyArray(((DenseLayer) right).getWeights()));
                ((DenseLayer) left).setBias(copyArray(((DenseLayer) right).getBias()));
            }
        }
        return null;
    }

    protected <T extends GeneticClient> void mutate(ArrayList<T> clients) {
        for (GeneticClient g : clients) {

            Layer l = g.getNetwork().getInputLayer();

            while (l != g.getNetwork().getOutputLayer()) {
                l = l.getNext_layer();
                if (l instanceof DenseLayer) {
                    mutateArray(((DenseLayer) l).getWeights(), MUTATION_RATE, MUTATION_STRENGTH);
                    mutateArray(((DenseLayer) l).getBias(), MUTATION_RATE, MUTATION_STRENGTH);
                }
            }
        }
    }


    public static void mutateArray(double[][] values, double rate, double strength) {
        Random random = new Random();
        for (int i = 0; i < values.length; i++) {
            for (int n = 0; n < values[0].length; n++) {
                if (Math.random() < rate) {
                    values[i][n] += random.nextGaussian() * strength;
                }
            }
        }
    }

    public static void mutateArray(double[] values, double rate, double strength) {
        Random random = new Random();
        for (int i = 0; i < values.length; i++) {
            if (Math.random() < rate) {
                values[i] += random.nextGaussian() * strength;
            }
        }
    }

    public static double[][] copyArray(double[][] values) {
        double[][] out = new double[values.length][values[0].length];
        for (int i = 0; i < values.length; i++) {
            for (int n = 0; n < values[0].length; n++) {
                out[i][n] = values[i][n];
            }
        }
        return out;
    }

    public static double[] copyArray(double[] values) {
        double[] out = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            out[i] = values[i];
        }
        return out;
    }


}