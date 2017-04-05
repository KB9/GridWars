package cern.ais.gridwars;

import cern.ais.gridwars.command.MovementCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruno on 04/04/17.
 */
public class ExpandPolicy extends Policy {
    void execute(GlobalContext ctx){
        this.logPolicy(ctx);

        ArrayList<Cell> myCells = ctx.myCells();

        for (Cell c : myCells){
            c.moveTroops(MovementCommand.Direction.DOWN, 100);
        }
    }

    String name(){
        return "ExpandPolicy";
    }
}
