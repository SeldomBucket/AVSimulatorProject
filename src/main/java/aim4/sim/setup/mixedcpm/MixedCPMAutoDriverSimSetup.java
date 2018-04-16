package aim4.sim.setup.mixedcpm;

import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.MixedCPMMapUtil;
import aim4.map.mixedcpm.maps.AdjustableManualCarPark;
import aim4.map.mixedcpm.maps.AdjustableMixedCarPark;
import aim4.map.mixedcpm.maps.StaticMap;
import aim4.sim.Simulator;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;
import aim4.util.Logging;

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
        double disabledProbability = 0.048;
        MixedCPMBasicMap layout = null;

        // Set up map
        switch (mapType){
            case STATIC:
                layout = new StaticMap(carParkHeight,
                                       carParkWidth,
                                       6.0, // From essex2009parking
                                       speedLimit,
                                       currentTime);
                this.automatedVehiclesRate = 0;
                break;
            case ADJUSTABLE_MANUAL:
                layout = new AdjustableManualCarPark(carParkHeight,
                                                    carParkWidth,
                                                    laneWidth,
                                                    speedLimit,
                                                    currentTime);
                break;
            case ADJUSTABLE_MIXED:
                layout = new AdjustableMixedCarPark(carParkHeight,
                                                    carParkWidth,
                                                    laneWidth,
                                                    speedLimit,
                                                    currentTime);
                break;
        }

        MixedCPMAutoDriverSimulator.setMapType(mapType);

        // Set up the correct spawn point (Don't log if reading from CSV)
        switch(spawnSpecType) {
            case FINITE_SINGLE:
                MixedCPMMapUtil.setUpFiniteSingleSpecSpawnPoint(layout, noOfVehiclesToSpawn, trafficLevel, automatedVehiclesRate);
                if (logToFile) {
                    Logging.initialiseLogWriter();
                    Logging.initialiseSpawnLogWriter();
                }
                break;
            case FINITE_RANDOM:
                MixedCPMMapUtil.setUpFiniteRandomSpecSpawnPoint(layout, noOfVehiclesToSpawn, trafficLevel, disabledProbability, automatedVehiclesRate);
                if (logToFile) {
                    Logging.initialiseLogWriter();
                    Logging.initialiseSpawnLogWriter();
                }
                break;
            case SINGLE:
                MixedCPMMapUtil.setUpInfiniteSingleSpecVehicleSpawnPoint(layout, trafficLevel, automatedVehiclesRate);
                if (logToFile) {
                    Logging.initialiseLogWriter();
                    Logging.initialiseSpawnLogWriter();
                }
                break;
            case RANDOM:
                MixedCPMMapUtil.setUpInfiniteRandomSpecVehicleSpawnPoint(layout, trafficLevel, disabledProbability, automatedVehiclesRate);
                if (logToFile) {
                    Logging.initialiseLogWriter();
                    Logging.initialiseSpawnLogWriter();
                }
                break;
            case CSV:
                MixedCPMMapUtil.setUpSpecificSpecVehicleSpawnPoint(layout, useCSVFile);
                if (logToFile) {
                    Logging.initialiseLogWriter();
                }
                break;
        }

        return new MixedCPMAutoDriverSimulator(layout);
    }
}
