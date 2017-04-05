package cern.ais.gridwars.MachineLearning;

import cern.ais.gridwars.UniverseView;
import cern.ais.gridwars.bot.PlayerBot;
import cern.ais.gridwars.command.MovementCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kavan on 05/04/17.
 */
public class MachineLearningBot implements PlayerBot {

    private NeuralNet brain;
    private double fitness;
    private List<Double> weights;

    public MachineLearningBot() {
        brain = new NeuralNet();
        weights = new ArrayList<>();
    }

    public void putWeights(List<Double> weights) {
        brain.putWeights(weights);
        this.weights = weights;
    }

    @Override
    public void getNextCommands(UniverseView universeView, List<MovementCommand> list) {

    }
}
