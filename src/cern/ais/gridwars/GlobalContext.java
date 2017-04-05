package cern.ais.gridwars;

import cern.ais.gridwars.command.MovementCommand;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bruno on 04/04/17.
 */

public class GlobalContext {

    public GlobalContext() {
        this.cells = new Cell[50][50];
        for (int x = 0; x < 50; ++x) {
            for(int y = 0; y < 50; ++y) {
                cells[x][y] = new Cell(this);
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

    void dumpTurnCommands(List<MovementCommand> cmd) {
        for (ArrayList<MovementCommand> e : this.nextTurnCommands.values()) {
            cmd.add(e.get(0));
        }

        nextTurnCommands = new HashMap<>();
    }

    public boolean cellCoordsNeedInit = true;

    public void updateUniverseView(UniverseView u) {
        this.uv = u;

        if (cellCoordsNeedInit) {
            for (int x = 0; x < 50; ++x) {
                for (int y = 0; y < 50; ++y) {
                    cells[x][y].updateCoordinates(u.getCoordinates(x, y));
                }
            }

            cellCoordsNeedInit = false;
        }
    }

    public void recordCellStates() {
        prevCellStates.add(new GridData(cells));
    }

    class GridData {
        public GridData(Cell[][] snapshot) {
            for (int x = 0; x < 50; ++x) {
                for (int y = 0; y < 50; ++y) {
                    cells[x][y] = new CellData(snapshot[x][y]);
                }
            }
        }
        public CellData[][] cells = new CellData[50][50];
    }

    public Cell cellAt(Coordinates coords){
        return cells[coords.getX()][coords.getY()];
    }
    public Cell[][] cells;

    public CircularArrayList<GridData> prevCellStates = new CircularArrayList<>(10);

    public List<String> policyHistory = new ArrayList<>();
    public UniverseView uv = null;

    public Map<Cell, ArrayList<MovementCommand>> nextTurnCommands = new HashMap<>();
}
