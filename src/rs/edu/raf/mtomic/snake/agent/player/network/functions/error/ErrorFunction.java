package rs.edu.raf.mtomic.snake.agent.player.network.functions.error;


import rs.edu.raf.mtomic.snake.agent.player.network.layers.OutputLayer;

public abstract class ErrorFunction{


    public abstract double overall_error(OutputLayer outputLayer, double[][][] expected);

    public abstract void apply(OutputLayer outputLayer, double[][][] expected);

}
