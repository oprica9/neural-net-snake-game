package rs.edu.raf.mtomic.snake.agent.player.evolution;

import rs.edu.raf.mtomic.snake.agent.player.network.Network;

public interface GeneticClient {

    Network getNetwork();

    double getScore();

}
