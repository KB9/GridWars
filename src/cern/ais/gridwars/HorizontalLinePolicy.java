package cern.ais.gridwars;

import java.util.List;

/**
 * Created by kavan on 06/04/17.
 */
public class HorizontalLinePolicy extends Policy {

    @Override
    public void execute(GlobalContext cxt) {
        List<Cell> myCells = cxt.myCells();
        CircularGrowCommand cmd = new CircularGrowCommand();
        for (Cell cell : myCells) {
            cell.executeCommand(cmd);
        }
    }

    @Override
    String name() {
        return "HorizontalLinePolicy";
    }
}
