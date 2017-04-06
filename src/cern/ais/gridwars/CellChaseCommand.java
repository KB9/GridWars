package cern.ais.gridwars;

import cern.ais.gridwars.CellCommand;
import cern.ais.gridwars.command.MovementCommand;

import java.util.ArrayList;

/**
 * Created by bruno on 06/04/17.
 */
public class CellChaseCommand implements CellCommand {

    class Coords {
        public Coords(int x, int y) {
            this.x = x;
            this.y = y;
        }
        int x;
        int y;
    }

    Coords averageEnemyPos(ArrayList<Cell> enemy) {
        int x = 0;
        int y = 0;

        for (Cell c : enemy) {
            x += c.coords.getX();
            y += c.coords.getY();
        }

        x /= enemy.size();
        y /= enemy.size();

        return new Coords(x, y);
    }
    public void execute(Cell c) {
        MovementCommand.Direction dirToEnemy = c.dirToClosestEnemy();
        if (dirToEnemy == null) {
            // There are no enemies directly in sight. Get average pos.
            Coords avg = averageEnemyPos(c.ctx.enemyCells());
            dirToEnemy = c.dirToCoordinates(avg.x, avg.y);
        }

        c.moveTroops(dirToEnemy, c.troopCount() - 5);
    }
}
