package aim4.gui.setuppanel;

import aim4.sim.setup.SimSetup;
import aim4.sim.setup.cpm.BasicCPMSimSetup;

/**
 * The SetupPanel for CPM.
 */
public class CPMSimSetupPanel extends SimSetupPanel {

    @Override
    public SimSetup getSimSetup() {
        return new BasicCPMSimSetup();
    }
}
