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
        SPLIT_ALL,
        SPLIT_ALL_2,
        SPLIT_ALL_3,
        SPLIT_ALL_4,
    }

    private NeuralNet brain;
    public double fitness;
    private List<Double> weights;
    private GlobalContext context;

    public MachineLearningBot() {
        brain = new NeuralNet();
        weights = new ArrayList<>();
        context = new GlobalContext();
    }

    public void putWeights(List<Double> weights) {
        brain.putWeights(weights);
        this.weights = weights;
    }

    public List<Double> getWeights() {
        return weights;
    }

    @Override
    // NOTE: Equivalent to Car::update() in C++ version
    // TODO: Make bot perform a move. The results of this move (i.e. the number
    // TODO: of enemies destroyed AND the number of our troops destroyed) should be
    // TODO: available to update the fitness appropriately after this function returns.
    public void getNextCommands(UniverseView universeView, List<MovementCommand> list) {
        if (weights.size() == 0) return;

        context.updateUniverseView(universeView);

        // Get list of weights for every cell in the grid
        List<Double> inputs = convertGridToWeights();
        List<Double> outputs = brain.update(inputs);

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
                List<MovementCommand> commands = getCommands(tactic, cell);
                // Add all the commands to the queue
                list.addAll(commands);
            }
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

    private MovementTactic categorizeWeight(double weight) {
        int enumIndex = (int)(weight * 100) % MovementTactic.values().length;
        return MovementTactic.values()[enumIndex];
    }

    private List<MovementCommand> getCommands(MovementTactic tactic, Cell cell) {
        List<MovementCommand> commands = new ArrayList<>();
        switch (tactic) {
            case MOVE_UP:
                commands.add(new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.UP,
                        (long)cell.troopCount()));
                break;

            case MOVE_DOWN:
                commands.add(new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.DOWN,
                        (long)cell.troopCount()));
                break;

            case MOVE_LEFT:
                commands.add(new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.LEFT,
                        (long)cell.troopCount()));
                break;

            case MOVE_RIGHT:
                commands.add(new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.RIGHT,
                        (long)cell.troopCount()));
                break;

            case SPLIT_ALL:
            case SPLIT_ALL_2:
            case SPLIT_ALL_3:
            case SPLIT_ALL_4:
                if (cell.troopCount() < 5) break;
                commands.add(new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.UP,
                        (long)cell.troopCount() / 5));
                commands.add(new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.DOWN,
                        (long)cell.troopCount() / 5));
                commands.add(new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.LEFT,
                        (long)cell.troopCount() / 5));
                commands.add(new MovementCommand(
                        cell.coords,
                        MovementCommand.Direction.RIGHT,
                        (long)cell.troopCount() / 5));
                break;
        }
        return commands;
    }

    private Cell getCellFromCommand(MovementCommand command) {
        Coordinates from = command.getCoordinatesFrom();
        return context.cellAt(from).cellAt(command.getDirection());
    }

    private boolean isCellFull(Cell cell) {
        return cell.troopCount() < Cell.MAX_TROOPS && cell.belongsToMe();
    }

    private boolean isCellSurrounded(Cell cell) {
        return cell.surroundedByMe();
    }
}
