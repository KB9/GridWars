package cern.ais.gridwars.MachineLearning;

import java.util.List;

/**
 * Created by kavan on 04/04/17.
 */
public class NeuronLayer {

    public int numNeurons;
    public List<Neuron> neurons;

    public NeuronLayer(int numNeurons, int numInputsPerNeuron) {
        for (int i = 0; i < numNeurons; ++i) {
            neurons.add(new Neuron(numInputsPerNeuron));
        }
    }
}
