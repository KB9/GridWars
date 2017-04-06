package cern.ais.gridwars;

import cern.ais.gridwars.bot.PlayerBot;
import cern.ais.gridwars.command.MovementCommand;

import java.util.List;

public class TeamBot implements PlayerBot {
	Policy policy;
	GlobalContext ctx = new GlobalContext();
	Policy pol2= new ExpandPolicy(true);

	int counter = 20;

	public TeamBot(){
	    policy = new ChasePolicy();
    }
	@Override public void getNextCommands(UniverseView universeView, List<MovementCommand> movementCommands) {
        ctx.updateUniverseView(universeView);
        if (counter-- >0){
        	pol2.execute(ctx);
		} else
	    policy.execute(ctx);
	    //ctx.recordCellStates();
	    ctx.dumpTurnCommands(movementCommands);
	}
}
