package aim4.gui.setuppanel;

import aim4.sim.setup.SimSetup;
import aim4.sim.setup.merge.BasicMergeSimSetup;

/**
 * Created by Callum on 08/02/2017.
 */
public class MergeSimSetupPanel extends SimSetupPanel {

    @Override
    public SimSetup getSimSetup() {
        return new BasicMergeSimSetup();
    }
}
