package cern.ais.gridwars;

import cern.ais.gridwars.bot.PlayerBot;
import cern.ais.gridwars.command.MovementCommand;

import java.util.List;

public class TeamBot implements PlayerBot {
	Policy policy;
	GlobalContext ctx;

	TeamBot(){
	    policy = new ExpandPolicy();
    }
	@Override public void getNextCommands(UniverseView universeView, List<MovementCommand> movementCommands) {
        ctx = new GlobalContext(universeView);
	    policy.execute(ctx);
	    ctx.fillTurnCommands(movementCommands);
	}
}
