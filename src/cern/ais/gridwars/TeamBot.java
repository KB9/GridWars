package cern.ais.gridwars;

import cern.ais.gridwars.bot.PlayerBot;
import cern.ais.gridwars.command.MovementCommand;

import java.util.List;

public class TeamBot implements PlayerBot {
	Policy policy;
	GlobalContext ctx = new GlobalContext();

	public TeamBot(){
	    policy = new ExpandPolicy();
    }
	@Override public void getNextCommands(UniverseView universeView, List<MovementCommand> movementCommands) {
        ctx.updateUniverseView(universeView);
	    policy.execute(ctx);
	    ctx.recordCellStates();
	    ctx.dumpTurnCommands(movementCommands);
	}
}
