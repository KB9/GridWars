package cern.ais.gridwars.MachineLearning;

import cern.ais.gridwars.*;
import cern.ais.gridwars.bot.PlayerBot;
import cern.ais.gridwars.command.MovementCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kavan on 05/04/17.
 */
public class MachineLearningBot implements PlayerBot {

    private NeuralNet brain;
    public double fitness;
    private List<Double> weights;
    public GlobalContext context;

    private boolean useDefaultWeights;

    public MachineLearningBot(boolean useDefaultWeights) {
        brain = new NeuralNet();
        weights = new ArrayList<>();
        context = new GlobalContext();

        this.useDefaultWeights = useDefaultWeights;

        if (useDefaultWeights) {
            weights.addAll(Arrays.asList(DefaultWeights.ARRAY));
            weights.addAll(Arrays.asList(DefaultWeights2.ARRAY));
            brain.putWeights(weights);
        }
    }

    public void putWeights(List<Double> weights) {
        brain.putWeights(weights);
        this.weights = weights;
    }

    public List<Double> getWeights() {
        return weights;
    }

    @Override
    public void getNextCommands(UniverseView universeView, List<MovementCommand> list) {
        if (weights.size() == 0) return;

        context.updateUniverseView(universeView);

        // Get list of weights for every cell in the grid
        List<Double> inputs = convertGridToWeights();
        double output = brain.update(inputs).get(0);

        Policy policy = getPolicyFromOutput(output);
        if (policy != null) {
            policy.execute(context);
            context.dumpTurnCommands(list);
        }
    }

    private List<Double> convertGridToWeights() {
        List<Double> weights = new ArrayList<>();
        for (int column = 0; column < context.cells.length; column++) {
            for (int row = 0; row < context.cells[column].length; row++) {
                double normalized = getNormalizedTroopCount(context.cells[column][row]);
                weights.add(normalized);
            }
        }
        return weights;
    }

    private double getNormalizedTroopCount(Cell cell) {
        double value = (double)cell.troopCount() / (double)Cell.MAX_TROOPS;
        if (cell.belongsToEnemy()) value = -value;
        return value;
    }

    private Policy getPolicyFromOutput(double output) {
        if (output >= 0 && output < 0.25)
            return new ExpandPolicy(true);
        else if (output >= 0.25 && output < 0.5)
            return new CircularGrowPolicy();
        else if (output >= 0.5 && output < 0.75)
            return new HorizontalLinePolicy();
        else if (output >= 0.75 && output <= 1)
            return new ChasePolicy();
        return null;
    }
}
