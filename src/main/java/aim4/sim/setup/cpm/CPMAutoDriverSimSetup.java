package aim4.sim.setup.cpm;

import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.VerySimpleMap;
import aim4.sim.CPMAutoDriverSimulator;
import aim4.sim.Simulator;

/**
 * Setup for simulation of AVs in an AV specific car park which are self-organising.
 */
public class CPMAutoDriverSimSetup extends BasicCPMSimSetup {

    /**
     * Create a setup for the simulator in which all vehicles are autonomous.
     *
     * @param basicSimSetup  the basic simulator setup
     */
    public CPMAutoDriverSimSetup(BasicCPMSimSetup basicSimSetup) {
        super(basicSimSetup);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Simulator getSimulator() {
        double currentTime = 0.0;
        VerySimpleMap layout = new VerySimpleMap();
        // set up the spawn points: create a new method for this.
        CPMMapUtil.setUpSimpleSpawnPoints(layout);
        return new CPMAutoDriverSimulator(layout);
    }
}
