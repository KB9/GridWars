package cern.ais.gridwars;

/**
 * Created by bruno on 05/04/17.
 */
public class CellData {
    public CellData(Cell c) {
        owner = c.owner();
        troops = c.troopCount();
    }

    public int troops;
    public Cell.Owner owner;
}
