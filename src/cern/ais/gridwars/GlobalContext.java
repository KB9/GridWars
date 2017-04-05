package cern.ais.gridwars;

import cern.ais.gridwars.command.MovementCommand;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bruno on 04/04/17.
 */

public class GlobalContext {

    GlobalContext(UniverseView uv) {
        this.uv =uv;
        this.cells = new Cell[50][50];
        for (int x = 0; x < 50; ++x) {
            for(int y = 0; y < 50; ++y) {
                cells[x][y] = new Cell(this, this.uv.getCoordinates(x, y));
            }
        }
    }

    private List<MovementCommand> getCommandList(Cell c) {
        if (!nextTurnCommands.containsKey(c)) {
            nextTurnCommands.put(c, new ArrayList<MovementCommand>());
        }

        return nextTurnCommands.get(c);
    }

    public void addCommand(Cell cell, MovementCommand mc) {
        if (mc == null) return;

        getCommandList(cell).add(mc);
    }

    public ArrayList<Cell> myCells() {
        List<Coordinates> cellCoords = uv.getMyCells();
        ArrayList<Cell> cells = new ArrayList<>();
        for (Coordinates c : cellCoords) {
            cells.add(cellAt(c));
        }
        return cells;
    }

    void fillTurnCommands(List<MovementCommand> cmd) {
        for (ArrayList<MovementCommand> e : this.nextTurnCommands.values()) {
            cmd.add(e.get(0));
        }
    }


    public Cell cellAt(Coordinates coords){
        return cells[coords.getX()][coords.getY()];
    }
    public Cell[][] cells;

    public List<String> policyHistory = new ArrayList<>();
    public final UniverseView uv;

    public Map<Cell, ArrayList<MovementCommand>> nextTurnCommands = new HashMap<>();
}
