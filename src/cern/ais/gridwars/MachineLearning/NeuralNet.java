package cern.ais.gridwars.MachineLearning;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kavan on 05/04/17.
 */
public class NeuralNet {

    private int numInputs;
    private int numOutputs;
    private int numHiddenLayers;
    private int numNeuronsPerHiddenLayer;

    private List<NeuronLayer> layers;

    public NeuralNet() {
        numInputs = Params.NUM_INPUTS;
        numOutputs = Params.NUM_OUTPUTS;
        numHiddenLayers = Params.NUM_HIDDEN_LAYERS;
        numNeuronsPerHiddenLayer = Params.NUM_NEURONS_PER_HIDDEN_LAYER;

        createNet();
    }

    // Builds the artificial neural network. The weights are all initially set
    // to random values: -1 < w < 1
    public void createNet() {
        // Create the layers of the network
        if (numHiddenLayers > 0) {
            // Create the first hidden layer
            layers.add(new NeuronLayer(numNeuronsPerHiddenLayer, numInputs));

            // Create the hidden layers
            for (int i = 0; i < numHiddenLayers; ++i) {
                layers.add(new NeuronLayer(
                        numNeuronsPerHiddenLayer,
                        numNeuronsPerHiddenLayer
                ));
            }

            // Create the output layer
            layers.add(new NeuronLayer(numOutputs, numNeuronsPerHiddenLayer));
        }
        else {
            // Create the output layer
            layers.add(new NeuronLayer(numOutputs, numNeuronsPerHiddenLayer));
        }
    }

    // Gets the weights from the neural network
    public List<Double> getWeights() {
        List<Double> weights = new ArrayList<>();
        // For each layer
        for (int i = 0; i < numHiddenLayers + 1; ++i) {
            // For each neuron
            for (int j = 0; j < layers.get(i).numNeurons; ++j) {
                // For each weight
                for (int k = 0; k < layers.get(i).neurons.get(j).numInputs; ++k) {
                    weights.add(layers.get(i).neurons.get(j).weights.get(k));
                }
            }
        }
        return weights;
    }

    // Gets the total number of weights in the network
    public int getNumberOfWeights() {
        int weights = 0;
        // For each layer
        for (int i = 0; i < numHiddenLayers; ++i) {
            // For each neuron
            for (int j = 0; j < layers.get(i).numNeurons; ++j) {
                // For each weight
                for (int k = 0; k < layers.get(i).neurons.get(j).numInputs; ++k) {
                    weights++;
                }
            }
        }
        return weights;
    }

    // Replaces the weights with the new ones
    public void putWeights(List<Double> weights) {
        int weight = 0;
        // For each layer
        for (int i = 0; i < numHiddenLayers; ++i) {
            // For each neuron
            for (int j = 0; j < layers.get(i).numNeurons; ++j) {
                // For each weight
                for (int k = 0; k < layers.get(i).neurons.get(j).numInputs; ++k) {
                    // Replace the old weight with the new weight
                    layers.get(i).neurons.get(j).weights.remove(k);
                    layers.get(i).neurons.get(j).weights.add(k, weights.get(weight++));
                }
            }
        }
    }

    // Calculates the outputs from a set of inputs
    public List<Double> update(List<Double> inputs) {
        List<Double> outputs = new ArrayList<>();
        int weight = 0;

        // First check that we have the correct number of inputs
        if (inputs.size() != numInputs) {
            // Just return an empty vector if incorrect
            return outputs;
        }

        // For each layer
        for (int i = 0; i < numHiddenLayers + 1; ++i) {
            if (i > 0) {
                inputs = outputs;
            }

            outputs.clear();
            weight = 0;

            // For each neuron, sum the (inputs * corresponding weights).
            // Throw the total at the sigmoid function to get the output
            for (int j = 0; j < layers.get(i).numNeurons; ++j) {
                double netInput = 0;
                int numInputs = layers.get(i).neurons.get(j).numInputs;

                // For each weight
                for (int k = 0; k < numInputs - 1; ++k) {
                    // Sum the weights * inputs
                    netInput += layers.get(i).neurons.get(j).weights.get(k) *
                            inputs.get(weight++);
                }

                // Add in the bias
                netInput +=
                        layers.get(i).neurons.get(j).weights.get(numInputs-1) *
                        Params.BIAS;

                // Outputs from each layer can be stored as they are generated.
                // The combined activation is first filtered through the sigmoid
                // function
                outputs.add(sigmoid(netInput, Params.ACTIVATION_RESPONSE));

                weight = 0;
            }
        }
        return outputs;
    }

    // Sigmoid response curve
    public double sigmoid(double netInput, double response) {
        return (1 / (1 + Math.exp(-netInput / response)));
    }
}
