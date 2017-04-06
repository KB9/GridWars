package cern.ais.gridwars;

import cern.ais.gridwars.command.MovementCommand;

/**
 * Created by kavan on 06/04/17.
 */
public class HorizontalLineCommand implements CellCommand {

    @Override
    public void execute(Cell c) {
        if (c.troopCount() > 3) {
            c.moveTroops(MovementCommand.Direction.LEFT, c.troopCount() / 3);
            c.moveTroops(MovementCommand.Direction.RIGHT, c.troopCount() / 3);
        }
    }
}
