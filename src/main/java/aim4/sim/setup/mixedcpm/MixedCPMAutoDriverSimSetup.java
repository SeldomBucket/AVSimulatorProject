package aim4.sim.setup.mixedcpm;

import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.MixedCPMMapUtil;
import aim4.map.mixedcpm.testmaps.ManualCPMMapTest;
import aim4.sim.Simulator;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;

/**
 * Setup for simulation of AVs in an AV specific car park which are self-organising.
 */
public class MixedCPMAutoDriverSimSetup extends BasicMixedCPMSimSetup {

    /**
     * Create a setup for the simulator in which all vehicles are autonomous.
     *
     * @param basicSimSetup  the basic simulator setup
     */
    public MixedCPMAutoDriverSimSetup(BasicMixedCPMSimSetup basicSimSetup) {
        super(basicSimSetup);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Simulator getSimulator() {
        double currentTime = 0.0;

        MixedCPMBasicMap layout = new ManualCPMMapTest(carParkHeight, 
                carParkWidth, 
                laneWidth,
                speedLimit,
                currentTime);

        if (spawnSpecType == MixedCPMMapUtil.SpawnSpecType.SINGLE)
            // Set up the correct spawn point
            switch(spawnSpecType) {
                case SINGLE:
                    if (!useCSVFile.getKey()){
                        MixedCPMMapUtil.setUpInfiniteSingleSpecVehicleSpawnPoint(layout, trafficLevel);
                    } else {
                        MixedCPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(layout, useCSVFile);
                    }
                    break;
                case RANDOM:
                    if (!useCSVFile.getKey()){
                        MixedCPMMapUtil.setUpInfiniteRandomSpecVehicleSpawnPoint(layout, trafficLevel);
                    } else {
                        MixedCPMMapUtil.setUpSpecificRandomSpecVehicleSpawnPoint(layout, useCSVFile);
                    }
                    break;
            }

        return new MixedCPMAutoDriverSimulator(layout);
    }
}
