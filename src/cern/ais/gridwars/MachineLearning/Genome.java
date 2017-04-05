package cern.ais.gridwars.MachineLearning;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kavan on 05/04/17.
 */
public class Genome implements Comparable<Genome> {

    public List<Double> weights = new ArrayList<>();
    public double fitness;

    public Genome() {
        fitness = 0;
    }

    public Genome(List<Double> weights, double fitness) {
        this.weights = weights;
        this.fitness = fitness;
    }

    @Override
    public int compareTo(Genome genome) {
        if (fitness < genome.fitness) return -1;
        else if (fitness == genome.fitness) return 0;
        else return 1;
    }
}
