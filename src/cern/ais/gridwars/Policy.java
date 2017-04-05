package cern.ais.gridwars;


import cern.ais.gridwars.command.MovementCommand;
import java.util.List;

/**
 * Created by bruno on 04/04/17.
 */
public abstract class Policy {
    abstract void execute(GlobalContext cxt);
    void logPolicy(GlobalContext ctx) {
        ctx.policyHistory.add(this.name());
    }
    abstract String name();
}
