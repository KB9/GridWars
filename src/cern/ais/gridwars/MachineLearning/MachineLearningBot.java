package cern.ais.gridwars.MachineLearning;

import cern.ais.gridwars.Cell;
import cern.ais.gridwars.Coordinates;
import cern.ais.gridwars.GlobalContext;
import cern.ais.gridwars.UniverseView;
import cern.ais.gridwars.bot.PlayerBot;
import cern.ais.gridwars.command.MovementCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kavan on 05/04/17.
 */
public class MachineLearningBot implements PlayerBot {

    private enum MovementTactic {
        MOVE_UP,
        MOVE_DOWN,
        MOVE_LEFT,
        MOVE_RIGHT,
        SPLIT_HALF_UP,
        SPLIT_HALF_DOWN,
        SPLIT_HALF_LEFT,
        SPLIT_HALF_RIGHT
    }

    private NeuralNet brain;
    public double fitness;
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
    // NOTE: Equivalent to Car::update() in C++ version
    // TODO: Make bot perform a move. The results of this move (i.e. the number
    // TODO: of enemies destroyed AND the number of our troops destroyed) should be
    // TODO: available to update the fitness appropriately after this function returns.
    public void getNextCommands(UniverseView universeView, List<MovementCommand> list) {
        if (weights.size() == 0) return;

        // Get list of weights for every cell in the grid
        List<Double> inputs = convertGridToWeights(universeView);
        List<Double> outputs = brain.update(inputs);

        GlobalContext context = new GlobalContext(universeView);
        for (int col = 0; col < GlobalContext.GRID_WIDTH; col++) {
            for (int row = 0; row < GlobalContext.GRID_HEIGHT; row++) {
                Cell cell = context.cells[col][row];
                if (!cell.belongsToMe()) continue;

                // Get the index of the weight which corresponds to the current cell
                // TODO: This may not be the correct corresponding index
                int index = (cell.coords.getY() * GlobalContext.GRID_WIDTH) + cell.coords.getX();
                // Categorize the weight into a movement tactic
                MovementTactic tactic = categorizeWeight(outputs.get(index));
                // Convert the movement tactic into an actual movement command
                MovementCommand command = getCommand(tactic, cell);
                System.out.println(command.toString());
                // Add the command to the list of commands
                list.add(command);
            }
        }
//        for (Cell cell : context.myCells()) {
//            // Get the index of the weight which corresponds to the current cell
//            // TODO: This may not be the correct corresponding index
//            int index = (cell.coords.getY() * GlobalContext.GRID_WIDTH) + cell.coords.getX();
//            // Categorize the weight into a movement tactic
//            MovementTactic tactic = categorizeWeight(outputs.get(index));
//            // Convert the movement tactic into an actual movement command
//            MovementCommand command = getCommand(tactic, cell);
//            System.out.println(command.toString());
//            // Add the command to the list of commands
//            list.add(command);
//        }
    }

    private List<Double> convertGridToWeights(UniverseView universeView) {
        GlobalContext context = new GlobalContext(universeView);

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

    private MovementTactic categorizeWeight(double weight) {
//        int enumIndex = (int)(weight * MovementTactic.values().length);
//        return MovementTactic.values()[enumIndex];
        int enumIndex = (int)(weight * 100) % MovementTactic.values().length;
        return MovementTactic.values()[enumIndex];
    }

    private MovementCommand getCommand(MovementTactic tactic, Cell cell) {
        MovementCommand command = null;
        switch (tactic) {
            case MOVE_UP:
                command = new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.UP,
                        (long)cell.troopCount());
                break;

            case MOVE_DOWN:
                command = new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.DOWN,
                        (long)cell.troopCount());
                break;

            case MOVE_LEFT:
                command = new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.LEFT,
                        (long)cell.troopCount());
                break;

            case MOVE_RIGHT:
                command = new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.RIGHT,
                        (long)cell.troopCount());
                break;

            case SPLIT_HALF_UP:
                command = new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.UP,
                        (long)cell.troopCount() / 2);
                break;

            case SPLIT_HALF_DOWN:
                command = new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.DOWN,
                        (long)cell.troopCount() / 2);
                break;

            case SPLIT_HALF_LEFT:
                command = new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.LEFT,
                        (long)cell.troopCount() / 2);
                break;

            case SPLIT_HALF_RIGHT:
                command = new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.RIGHT,
                        (long)cell.troopCount() / 2);
                break;
        }
        return command;
    }
}
