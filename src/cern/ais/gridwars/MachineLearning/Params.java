package cern.ais.gridwars.MachineLearning;

/**
 * Created by kavan on 05/04/17.
 */
public class Params {

    // Used for the neural network
    public static final int NUM_INPUTS = 2500;
    public static final int NUM_HIDDEN_LAYERS = 1;
    public static final int NUM_NEURONS_PER_HIDDEN_LAYER = 4;
    public static final int NUM_OUTPUTS = 2500;

    // For tweaking the sigmoid function
    public static final double ACTIVATION_RESPONSE = 1.0;

    // Bias value
    public static final double BIAS = -1.0;

    // Genetic algorithm parameters
    public static final double CROSSOVER_RATE = 0.7;
    public static final double MUTATION_RATE = 0.1;

    // Maximum amount each weight may mutate by
    public static final double MAX_PERTURBATION = 0.3;

    // Used for elitism
    public static final int NUM_ELITE = 4;
    public static final int NUM_ELITE_COPIES = 1;
}
