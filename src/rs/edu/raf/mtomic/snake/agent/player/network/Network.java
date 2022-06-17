package rs.edu.raf.mtomic.snake.agent.player.network;


import rs.edu.raf.mtomic.snake.agent.player.network.functions.activation.Sigmoid;
import rs.edu.raf.mtomic.snake.agent.player.network.functions.error.ErrorFunction;
import rs.edu.raf.mtomic.snake.agent.player.network.functions.error.MSE;
import rs.edu.raf.mtomic.snake.agent.player.network.layers.*;
import rs.edu.raf.mtomic.snake.agent.player.network.tools.ArrayTools;
import rs.edu.raf.mtomic.snake.parser.parser.Parser;
import rs.edu.raf.mtomic.snake.parser.parser.ParserTools;
import rs.edu.raf.mtomic.snake.parser.tree.Attribute;
import rs.edu.raf.mtomic.snake.parser.tree.Node;

import java.util.Arrays;

public class Network {

    private InputLayer inputLayer;
    private OutputLayer outputLayer;

    public Network(InputLayer inputLayer) {
        Layer cur = inputLayer;
        this.inputLayer = inputLayer;
        while (cur.getNext_layer() != null) {
            cur = cur.getNext_layer();
        }
        if (!(cur instanceof OutputLayer)) {
            System.err.println("Network does not have a Output Layer");
            System.exit(-1);
        } else {
            this.outputLayer = (OutputLayer) cur;
        }
    }

    public void printArchitecture() {
        Layer cur = inputLayer;

        while (cur.getNext_layer() != null) {
            System.out.println(cur);
            cur = cur.getNext_layer();
        }
    }

    public Network setErrorFunction(ErrorFunction errorFunction) {
        this.outputLayer.setErrorFunction(errorFunction);
        return this;
    }

    public ErrorFunction getErrorFunction() {
        return this.outputLayer.getErrorFunction();
    }

    public InputLayer getInputLayer() {
        return inputLayer;
    }

    public OutputLayer getOutputLayer() {
        return outputLayer;
    }

    public double[][][] calculate(double[][][] in) {
        if (!this.getInputLayer().matchingDimensions(in)) return null;
        this.inputLayer.setInput(in);
        this.inputLayer.feedForwardRecursive();
        return getOutput();
    }

    public void backpropagateError(double[][][] expectedOutput) {
        if (!this.getOutputLayer().matchingDimensions(expectedOutput)) return;
        this.outputLayer.calculateOutputErrorValues(expectedOutput);
        this.outputLayer.backpropagateErrorRecursive();
    }

    public void updateWeights(double eta) {
        this.inputLayer.updateWeightsRecursive(eta);
    }

    public void train(double[][][] input, double[][][] expected, double eta) {
        if (!this.getInputLayer().matchingDimensions(input) ||
                !this.getOutputLayer().matchingDimensions(expected)) {
            System.err.println(
                    this.inputLayer.getOUTPUT_DEPTH() + " - " + input.length + "\n" +
                            this.inputLayer.getOUTPUT_WIDTH() + " - " + input[0].length + "\n" +
                            this.inputLayer.getOUTPUT_HEIGHT() + " - " + input[0][0].length + "\n" +
                            this.outputLayer.getOUTPUT_DEPTH() + " - " + expected.length + "\n" +
                            this.outputLayer.getOUTPUT_WIDTH() + " - " + expected[0].length + "\n" +
                            this.outputLayer.getOUTPUT_HEIGHT() + " - " + expected[0][0].length + "\n");
            return;
        }
        this.calculate(input);
        this.backpropagateError(expected);
        this.updateWeights(eta);
    }

    public void save_network(String file) {
        Parser p = new Parser();
        p.create(file);
        Node root = new Node("Network");
        root.addAttribute("loss_function", this.getErrorFunction().getClass().getSimpleName());
        root.addAttribute("input_dimensions", "[" + this.getINPUT_DEPTH() + "," + this.getINPUT_WIDTH() + "," + this.getINPUT_HEIGHT() + "]");
        Layer layer = this.getInputLayer();
        int index = 0;
        while (layer != this.getOutputLayer()) {
            layer = layer.getNext_layer();
            index++;
            Node layer_node = null;

            if (layer instanceof DenseLayer) {
                layer_node = new Node("DenseLayer " + index);
                layer_node.addAttribute(new Attribute("neurons", "" + layer.getOUTPUT_HEIGHT()));
                layer_node.addAttribute(new Attribute("bias", Arrays.toString(((DenseLayer) layer).getBias())));
                layer_node.addAttribute("activation_function", ((DenseLayer) layer).getActivationFunction().getClass().getSimpleName());
                Node weights = new Node("weights");
                int id = 0;
                for (double[] d : ((DenseLayer) layer).getWeights()) {
                    weights.addAttribute(id + "", Arrays.toString(d));
                    id++;
                }
                layer_node.addChild(weights);
            }

            root.addChild(layer_node);
        }

        p.getContent().addChild(root);

        try {
            p.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Network load_network(String file) {
        Parser p = new Parser();
        try {
            p.load(file);
            Node root = p.getContent().getChild("Network");

            int[] dim = ParserTools.parseIntArray(root.getAttribute("input_dimensions").getValue());
            NetworkBuilder builder = new NetworkBuilder(dim[0], dim[1], dim[2]);

            DenseLayer l = null;
            for (Node n : root.getChilds()) {
                String name = n.getName().split(" ")[0];
                if (name.equals("DenseLayer")) {
                    l = new DenseLayer(Integer.parseInt(n.getAttribute("neurons").getValue()));
                    if ("Sigmoid".equals(n.getAttribute("activation_function").getValue())) {
                        l.setActivationFunction(new Sigmoid());
                    }
                    l.setBias(ParserTools.parseDoubleArray(n.getAttribute("bias").getValue()));
                    Node w = n.getChild("weights");
                    double[][] weights = new double[w.getAttributes().size()][];
                    for (Attribute b : w.getAttributes()) {
                        weights[Integer.parseInt(b.getName())] = ParserTools.parseDoubleArray(b.getValue());
                    }
                    l.setWeights(weights);
                }
                builder.addLayer(l);
            }

            Network network = builder.buildNetwork();
            if ("MSE".equals(root.getAttribute("loss_function").getValue())) {
                network.setErrorFunction(new MSE());
            }
            return network;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public double[][][] getOutput() {
        return ArrayTools.copyArray(this.outputLayer.getOutput_values());
    }

    public double[][][] getInput() {
        return ArrayTools.copyArray(this.inputLayer.getOutput_values());
    }

    public int getINPUT_DEPTH() {
        return inputLayer.getOUTPUT_DEPTH();
    }

    public int getINPUT_WIDTH() {
        return inputLayer.getOUTPUT_WIDTH();
    }

    public int getINPUT_HEIGHT() {
        return inputLayer.getOUTPUT_HEIGHT();
    }

    public int getOUTPUT_DEPTH() {
        return outputLayer.getOUTPUT_DEPTH();
    }

    public int getOUTPUT_WIDTH() {
        return outputLayer.getOUTPUT_WIDTH();
    }

    public int getOUTPUT_HEIGHT() {
        return outputLayer.getOUTPUT_HEIGHT();
    }
}
