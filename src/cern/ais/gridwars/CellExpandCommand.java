package cern.ais.gridwars;

import cern.ais.gridwars.command.MovementCommand;

/**
 * Created by bruno on 05/04/17.
 */
public class CellExpandCommand implements CellCommand {
    private boolean aggressive;

    public CellExpandCommand(boolean aggressive) {
        this.aggressive = aggressive;
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
                    if (c.cellUp().troopCount() < 90)
                        c.moveTroops(MovementCommand.Direction.UP, troopsUpDown);
                } else
                    if(c.cellDown().troopCount() < 90)
                        c.moveTroops(MovementCommand.Direction.DOWN, troopsUpDown);
            }

            if (!horizontalStrip) {
                if (shouldMoveLeft) {
                    if (c.cellLeft().troopCount() < 90)
                        c.moveTroops(MovementCommand.Direction.LEFT, troopsLeftRight);
                } else
                    if (c.cellRight().troopCount() < 90)
                        c.moveTroops(MovementCommand.Direction.RIGHT, troopsLeftRight);
            }


            if (horizontalStrip && verticalStrip) {
                // We are stuck between two strips. Just move depending on your location
                MovementCommand.Direction upDown = c.coords.getY() < 25
                        ? MovementCommand.Direction.UP
                        : MovementCommand.Direction.DOWN;
                MovementCommand.Direction leftRight = c.coords.getX() < 25
                        ? MovementCommand.Direction.LEFT
                        : MovementCommand.Direction.RIGHT;
                c.moveTroops(upDown, currTroops / 2);
                c.moveTroops(leftRight, currTroops / 2);

            }





            //c.moveTroops(MovementCommand.Direction.LEFT, troopsPerSide);
            //c.moveTroops(MovementCommand.Direction.RIGHT, troopsPerSide);

            return;
        }

        if (aggressive){
            if (c.cellUp().belongsToEnemy()) {
                c.moveTroops(MovementCommand.Direction.UP, currTroops  );
                return;
            }
            if (c.cellRight().belongsToEnemy()) {
                c.moveTroops(MovementCommand.Direction.RIGHT, currTroops );
                return;
            }
            if (c.cellDown().belongsToEnemy()) {
                c.moveTroops(MovementCommand.Direction.DOWN, currTroops );
                return;
            }
            if (c.cellLeft().belongsToEnemy()) {
                c.moveTroops(MovementCommand.Direction.LEFT, currTroops);
                return;
            }
        }

        if ((aggressive || c.cellUp().safeForNextTurn()) && shouldMoveUp){
            c.moveTroops(MovementCommand.Direction.UP, currTroops / 2);
        }
        if ((aggressive || c.cellRight().safeForNextTurn()) && !shouldMoveLeft){
            c.moveTroops(MovementCommand.Direction.RIGHT, currTroops / 2);
        }
        if ((aggressive || c.cellDown().safeForNextTurn()) && !shouldMoveUp){
            c.moveTroops(MovementCommand.Direction.DOWN, currTroops / 2);
        }
        if ((aggressive || c.cellLeft().safeForNextTurn()) && shouldMoveLeft){
            c.moveTroops(MovementCommand.Direction.LEFT, currTroops / 2);
        }
    }
}
