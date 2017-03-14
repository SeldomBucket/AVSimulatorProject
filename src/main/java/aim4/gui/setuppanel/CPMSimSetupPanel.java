package aim4.gui.setuppanel;

import aim4.sim.setup.SimSetup;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import aim4.sim.setup.cpm.CPMAutoDriverSimSetup;

/**
 * The SetupPanel for CPM.
 */
public class CPMSimSetupPanel extends SimSetupPanel {
    private static final long serialVersionUID = 1L;
    /** The simulation setup panel */
    private BasicCPMSimSetup simSetup;

    /**
     * Create a simulation setup panel
     *
     * @param initSimSetup  the initial simulation setup
     */
    public CPMSimSetupPanel(BasicCPMSimSetup initSimSetup) {
        this.simSetup = initSimSetup;
    }

    @Override
    public SimSetup getSimSetup() {
        return new CPMAutoDriverSimSetup(simSetup);
    }
}
