package cern.ais.gridwars;

import cern.ais.gridwars.command.MovementCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruno on 04/04/17.
 */
public class ExpandPolicy extends Policy {
    private boolean aggressive;
    public ExpandPolicy(boolean aggressive){
        this.aggressive = aggressive;
    }
    void execute(GlobalContext ctx){
        this.logPolicy(ctx);

        ArrayList<Cell> myCells = ctx.myCells();

        CellExpandCommand cmd = new CellExpandCommand(aggressive);

        for (Cell c : myCells){
            c.executeCommand(cmd);
        }
    }

    String name(){
        return "ExpandPolicy";
    }
}
