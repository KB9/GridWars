package cern.ais.gridwars;

import cern.ais.gridwars.command.MovementCommand;

import javax.xml.bind.annotation.XmlElementDecl;

/**
 * Created by bruno on 05/04/17.
 */
public class Cell {
    public Cell(GlobalContext gc, Coordinates coor){
        this.ctx = gc;
        this.coords = coor;
    }

    public boolean safeForNextTurn() {
        if (cellAt(coords.getUp()).belongsToEnemy() ||
            cellAt(coords.getDown()).belongsToEnemy() ||
            cellAt(coords.getLeft()).belongsToEnemy() ||
            cellAt(coords.getRight()).belongsToEnemy()) {
            return false;
        }

        return true;
    }

    public int bestNextTurnEnemyAttackCount() {
        int total = cellUp().enemyTroops()
                + cellDown().enemyTroops()
                + cellLeft().enemyTroops()
                + cellRight().enemyTroops();

        return (int)Math.round(total * 1.1);
    }

    public void moveTroops(MovementCommand.Direction dir, int count) {
        int troops = troopCount();
        count = count <= troops ? count : troops;

        if (belongsToEnemy()) {
            ctx.addCommand(this, null);
        }

        ctx.addCommand(this, new MovementCommand(coords, dir, new Long(count)));
    }

    public int bestNextTurnMyAttackCount() {
        int total = cellUp().myTroops()
                + cellDown().myTroops()
                + cellLeft().myTroops()
                + cellRight().myTroops();

        return (int)Math.round(total * 1.1);
    }
    public int nextTurnTroopCount() {
        return (int)Math.round(troopCount() * 1.1);
    }

    public boolean belongsToEnemy() {
        if (ctx.uv.belongsToMe(coords)) return false;
        if (ctx.uv.isEmpty(coords)) return false;
        return true;
    }

    public boolean hasIdealTroopCount(){
        int remainder = troopCount() % 10;

        return remainder == 5 || remainder == 6;
    }

    public boolean belongsToMe() {
        return ctx.uv.belongsToMe(coords);
    }

    public boolean isEmpty() {
        return ctx.uv.isEmpty(coords);
    }

    public Cell cellAt(Coordinates coords){
        return ctx.cellAt(coords);
    }

    public int troopCount(){
        return ctx.uv.getPopulation(coords).intValue();
    }
    public int enemyTroops(){
        if (belongsToMe()) return 0;

        return ctx.uv.getPopulation(coords).intValue();
    }

    public int myTroops(){
        if (belongsToEnemy()) return 0;

        return ctx.uv.getPopulation(coords).intValue();
    }

    public Cell cellUp() {
        return cellAt(coords.getUp());
    }
    public Cell cellDown() {
        return cellAt(coords.getDown());
    }
    public Cell cellLeft() {
        return cellAt(coords.getLeft());
    }
    public Cell cellRight() {
        return cellAt(coords.getRight());
    }

    public GlobalContext ctx;
    public Coordinates coords;
}
