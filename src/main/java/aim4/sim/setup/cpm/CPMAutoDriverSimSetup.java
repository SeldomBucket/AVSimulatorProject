package aim4.sim.setup.cpm;

import aim4.map.cpm.CPMBasicMap;
import aim4.map.cpm.CPMMap;
import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.CPMCarParkWithStatus;
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

        CPMBasicMap layout = new CPMCarParkWithStatus(laneWidth, // laneWidth
                speedLimit,
                currentTime,
                numberOfParkingLanes,
                parkingLength,
                accessLength);

        if (spawnSpecType == CPMMapUtil.SpawnSpecType.SINGLE)
        // Set up the correct spawn point
        switch(spawnSpecType) {
            case SINGLE:
                if (!useCSVFile.getKey()){
                    CPMMapUtil.setUpInfiniteSingleSpecVehicleSpawnPoint(layout, trafficLevel);
                } else {
                    CPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(layout, useCSVFile);
                }
                break;
            case RANDOM:
                if (!useCSVFile.getKey()){
                    CPMMapUtil.setUpInfiniteRandomSpecVehicleSpawnPoint(layout, trafficLevel);
                } else {
                    CPMMapUtil.setUpSpecificRandomSpecVehicleSpawnPoint(layout, useCSVFile);
                }
                break;
        }

        return new CPMAutoDriverSimulator(layout);
    }
}
