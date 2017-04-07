package cern.ais.gridwars;

/**
 * Created by bruno on 06/04/17.
 */
public class ChasePolicy extends Policy {
    @Override
    String name() {
        return "ChasePolicy";
    }

    @Override
    public void execute(GlobalContext ctx) {
        CellChaseCommand cmd = new CellChaseCommand();

        for (Cell c : ctx.myCells()){
            c.executeCommand(cmd);
        }
    }
}
