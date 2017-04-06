package cern.ais.gridwars;

import cern.ais.gridwars.command.MovementCommand;

/**
 * Created by kavan on 06/04/17.
 */
public class CircularGrowCommand implements CellCommand {

    private MovementCommand.Direction randomDir() {
        int random = (int)(Math.random() * (MovementCommand.Direction.values().length + 1));

        return MovementCommand.Direction.values()[random];
    }

    @Override
    public void execute(Cell c) {
        int currTroops = c.troopCount();
        currTroops -= 5;
        if (currTroops > 5) {

            int countToUp = c.cellsToBoundary(MovementCommand.Direction.UP);
            int countToDown = c.cellsToBoundary(MovementCommand.Direction.DOWN);
            int countToLeft = c.cellsToBoundary(MovementCommand.Direction.LEFT);
            int countToRight = c.cellsToBoundary(MovementCommand.Direction.RIGHT);

            boolean shouldMoveUp = countToUp < countToDown;
            boolean shouldMoveLeft = countToLeft < countToRight;

            int troopsLR = currTroops / 2;
            int troopsUD = currTroops /2;

            if (countToLeft == countToRight) {
                troopsLR = 1;
                troopsUD = troopsUD * 2 - 1;
            }
            if (countToDown == countToUp) {
                troopsUD = 1;
                troopsLR = troopsLR * 2 - 1;
            }

            if (troopsLR == troopsUD && troopsLR == 0) {
                c.moveTroops(randomDir(), currTroops);
                return;
            }


            MovementCommand.Direction upDown = shouldMoveUp
                    ? MovementCommand.Direction.UP
                    : MovementCommand.Direction.DOWN;
            MovementCommand.Direction leftRight = shouldMoveLeft
                    ? MovementCommand.Direction.LEFT
                    : MovementCommand.Direction.RIGHT;


            c.moveTroops(upDown, troopsUD);
            c.moveTroops(leftRight, troopsLR);
        }
    }
}
