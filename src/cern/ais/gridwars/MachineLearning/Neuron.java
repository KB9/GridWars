package cern.ais.gridwars.MachineLearning;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kavan on 04/04/17.
 */
public class Neuron {

    public int numInputs;
    public List<Double> weights = new ArrayList<>();

    public Neuron() {

    }

    Neuron(int numInputs) {
        this.numInputs = numInputs + 1;
        this.weights = new ArrayList<>();

        // Need an additional weight for the bias (hence +1)
        for (int i = 0; i < numInputs + 1; ++i) {
            weights.add(Utils.randomClamped());
        }
    }
}
