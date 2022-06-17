package rs.edu.raf.mtomic.snake.agent.player.network;

import rs.edu.raf.mtomic.snake.agent.player.network.layers.InputLayer;
import rs.edu.raf.mtomic.snake.agent.player.network.layers.Layer;
import rs.edu.raf.mtomic.snake.agent.player.network.layers.OutputLayer;

import java.util.ArrayList;

public class NetworkBuilder {

    InputLayer inputLayer;

    ArrayList<Layer> layers = new ArrayList<>();

    public NetworkBuilder(int input_depth, int input_width, int input_height) {
        inputLayer = new InputLayer(input_depth, input_width, input_height);

        inputLayer.setOutput_error_values(new double[input_depth][input_width][input_height]);
        inputLayer.setOutput_derivative_values(new double[input_depth][input_width][input_height]);
        inputLayer.setOutput_values(new double[input_depth][input_width][input_height]);
    }

    public NetworkBuilder addLayer(Layer layer) {
        layers.add(layer);
        return this;
    }

    public Network buildNetwork() {
        try {
            Layer b = inputLayer;
            for (Layer l : layers) {
                l.connectToPreviousLayer(b);
                b = l;
            }
            OutputLayer outputLayer = new OutputLayer(b);
            outputLayer.connectToPreviousLayer(b);

            return new Network(inputLayer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
