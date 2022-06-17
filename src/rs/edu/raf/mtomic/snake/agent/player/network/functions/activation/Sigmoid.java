package rs.edu.raf.mtomic.snake.agent.player.network.functions.activation;

public class Sigmoid extends ActivationFunction{

    @Override
    public double activation(double x) {
        return 1d / (1d + Math.exp(-x));
    }

    @Override
    public double activation_prime(double x) {
        return activation(x) * (1 - activation(x));
    }
}
