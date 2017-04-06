package cern.ais.gridwars;

import cern.ais.gridwars.command.MovementCommand;

import javax.xml.bind.annotation.XmlElementDecl;

/**
 * Created by bruno on 05/04/17.
 */
public class Cell {

    public static final int MAX_TROOPS = 100;

    public enum Owner {
        EMPTY,
        MINE,
        ENEMY
    }

    public Cell(GlobalContext gc) {
        this.ctx = gc;
    }

    public void updateCoordinates(Coordinates c){
        this.coords = c;
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

    public boolean surroundedByMe() {
        return cellUp().belongsToMe() &&
                cellDown().belongsToMe() &&
                cellLeft().belongsToMe() &&
                cellRight().belongsToMe();
    }

    public boolean surroundedByEnemy() {
        return cellUp().belongsToEnemy() &&
                cellDown().belongsToEnemy() &&
                cellLeft().belongsToEnemy() &&
                cellRight().belongsToEnemy();
    }

    public int cellsToBoundary(MovementCommand.Direction d) {
        if(isEmpty()) return 0;

        boolean isMine = belongsToMe();


        Cell next = cellAt(d);
        int cnt = 1;
        while(next.belongsToMe() == isMine){
            if (next == this) return -1;
            ++cnt;
            next = next.cellAt(d);
        }

        return cnt;
    }

    public int cellsToEnemy(MovementCommand.Direction d) {
        if(isEmpty()) return 0;

        boolean isMine = belongsToMe();

        Cell next = cellAt(d);
        int cnt = 1;
        while(!next.belongsToEnemy()){
            if (next == this) return Integer.MAX_VALUE;
            ++cnt;
            next = next.cellAt(d);
        }

        return cnt;
    }

    private boolean smallestOf(int first, int... rest){
        for (int j : rest) {
            if (first >= j) return false;
        }

        return true;
    }

    public MovementCommand.Direction dirToCoordinates(Coordinates c) {
        return dirToCoordinates(c.getX(), c.getY());
    }
    public MovementCommand.Direction dirToCoordinates(int x, int y) {
        int diffX = x - this.coords.getX();
        int diffY = x - this.coords.getY();

        if (Math.abs(diffX) < Math.abs(diffY)) {
            if (diffX > 25 || diffX < -25) {
                return MovementCommand.Direction.RIGHT;
            } else {
                return MovementCommand.Direction.LEFT;
            }
        } else {
            if (diffY > 25 || diffY < -25) {
                return MovementCommand.Direction.DOWN;
            } else {
                return MovementCommand.Direction.UP;
            }
        }
    }
    public MovementCommand.Direction dirToClosestEnemy() {
        if (isEmpty()) return null;

        int cntUp = cellsToEnemy(MovementCommand.Direction.UP);
        int cntDown = cellsToEnemy(MovementCommand.Direction.DOWN);
        int cntLeft = cellsToEnemy(MovementCommand.Direction.LEFT);
        int cntRight = cellsToEnemy(MovementCommand.Direction.RIGHT);

        if (smallestOf(Integer.MAX_VALUE - 1, cntUp, cntDown, cntLeft, cntRight)) {
            return null;
        }
        if (smallestOf(cntUp, cntDown, cntLeft, cntRight)){
            return MovementCommand.Direction.UP;
        } else if (smallestOf(cntDown, cntUp, cntLeft, cntRight)){
            return MovementCommand.Direction.DOWN;
        } else if (smallestOf(cntLeft, cntUp, cntDown, cntRight)){
            return MovementCommand.Direction.LEFT;
        } else if (smallestOf(cntRight, cntUp, cntLeft, cntDown)){
            return MovementCommand.Direction.RIGHT;
        }

        return null;
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

        if (belongsToEnemy() || count <= 0) {
            return;
        }

        ctx.addCommand(this, new MovementCommand(coords, dir, new Long(count)));
    }

    public void executeCommand(CellCommand cmd) {
        cmd.execute(this);
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
    public Cell cellAt(MovementCommand.Direction dir) {
        return ctx.cellAt(coords.getRelative(1, dir));
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

    public Owner owner() {
        if (isEmpty()) return Owner.EMPTY;
        if (belongsToMe()) return Owner.MINE;
        else return Owner.ENEMY;
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
