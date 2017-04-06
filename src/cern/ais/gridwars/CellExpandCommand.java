package cern.ais.gridwars;

import cern.ais.gridwars.command.MovementCommand;

/**
 * Created by bruno on 05/04/17.
 */
public class CellExpandCommand implements CellCommand {
    private int boolToInt(boolean b) {
        return b ? 1 : 0;
    }
    public void execute(Cell c) {
        int currTroops = c.troopCount();
        currTroops -= 5;

        if (currTroops < 8) {
            return;
        }

        int countToUp = c.cellsToBoundary(MovementCommand.Direction.UP);
        int countToDown = c.cellsToBoundary(MovementCommand.Direction.DOWN);
        int countToLeft = c.cellsToBoundary(MovementCommand.Direction.LEFT);
        int countToRight = c.cellsToBoundary(MovementCommand.Direction.RIGHT);

        boolean verticalStrip = countToUp == -1;
        boolean horizontalStrip = countToLeft == -1;
        boolean shouldMoveUp = countToUp < countToDown;
        boolean shouldMoveLeft = countToLeft < countToRight;

        int troopsLeftRight = currTroops / 4;
        int troopsUpDown = currTroops / 4;

        if (verticalStrip) {
            troopsLeftRight *= 2;
            troopsUpDown = 0;
        }
        if (horizontalStrip) {
            troopsLeftRight = 0;
            troopsUpDown *= 2;
        }


        if(c.surroundedByMe()) {

            if (!verticalStrip) {
                if (shouldMoveUp) {
                    c.moveTroops(MovementCommand.Direction.UP, troopsUpDown);
                } else
                    c.moveTroops(MovementCommand.Direction.DOWN, troopsUpDown);
            }

            if (!horizontalStrip) {
                if (shouldMoveLeft) {
                    c.moveTroops(MovementCommand.Direction.LEFT, troopsLeftRight);
                } else
                    c.moveTroops(MovementCommand.Direction.RIGHT, troopsLeftRight);
            }

            //c.moveTroops(MovementCommand.Direction.LEFT, troopsPerSide);
            //c.moveTroops(MovementCommand.Direction.RIGHT, troopsPerSide);

            return;
        }

        if (/*c.cellUp().safeForNextTurn() &&*/ shouldMoveUp){
            c.moveTroops(MovementCommand.Direction.UP, troopsUpDown);
        }
        if (/*c.cellRight().safeForNextTurn() &&*/ !shouldMoveLeft){
            c.moveTroops(MovementCommand.Direction.RIGHT, troopsLeftRight);
        }
        if (/*c.cellDown().safeForNextTurn() &&*/ !shouldMoveUp){
            c.moveTroops(MovementCommand.Direction.DOWN, troopsUpDown);
        }
        if (/*c.cellLeft().safeForNextTurn() &&*/ shouldMoveLeft){
            c.moveTroops(MovementCommand.Direction.LEFT, troopsLeftRight);
        }

    }
}
