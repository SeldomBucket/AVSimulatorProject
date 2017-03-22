package aim4.sim.setup.cpm;

import aim4.map.cpm.CPMMap;
import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.testmaps.CPMCarParkWithStatus;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
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

        CPMMap layout = new CPMCarParkWithStatus(laneWidth, // laneWidth
                speedLimit,
                currentTime,
                numberOfParkingLanes,
                parkingLength,
                accessLength);

        // set up the spawn points: create a new method for this.
        // CPMMapUtil.setUpOneVehicleSpawnPoint(layout);
        // CPMMapUtil.setUpInfiniteVehicleSpawnPoint(layout);
        CPMMapUtil.setUpFiniteVehicleSpawnPoint(layout, 2);
        // TODO CPM make use of traffic level
        return new CPMAutoDriverSimulator(layout);
    }
}
