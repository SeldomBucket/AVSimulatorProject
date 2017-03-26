package aim4.sim.setup.merge;

import aim4.sim.setup.SimSetup;
import aim4.sim.simulator.merge.MergeSimulator;

/**
 * Created by Callum on 08/02/2017.
 */
public interface MergeSimSetup extends SimSetup {
    @Override
    public MergeSimulator getSimulator();
}
