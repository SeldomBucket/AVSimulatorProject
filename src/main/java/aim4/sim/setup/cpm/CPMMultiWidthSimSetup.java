package aim4.sim.setup.cpm;

import aim4.map.cpm.CPMBasicMap;
import aim4.map.cpm.CPMCarParkMultiLaneWidth;
import aim4.map.cpm.CPMMapUtil;
import aim4.sim.Simulator;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import javafx.util.Pair;

import java.util.List;

/**
 * Created by Becci on 23-Apr-17.
 */
public class CPMMultiWidthSimSetup extends BasicCPMSimSetup {

    private List<Pair<Integer, Double>> parkingLaneSets;

    public CPMMultiWidthSimSetup(BasicCPMSimSetup basicSimSetup) {
        super(basicSimSetup);
    }

    public CPMMultiWidthSimSetup(double speedLimit,
                                 double trafficLevel,
                                 double parkingLength,
                                 double accessLength,
                                 CPMMapUtil.SpawnSpecType spawnSpecType,
                                 Pair<Boolean, String> useCSVFile,
                                 Pair<Boolean, Double> useSpecificSimTime,
                                 String singleSpawnSpecName,
                                 List<Double> mixedSpawnDistribution,
                                 List<Pair<Integer, Double>> parkingLaneSets,
                                 Integer numberOfSimulations,
                                 String fileLocation,
                                 List<String> specsToIncludeForRandomSpawn) {
        super(speedLimit, trafficLevel, parkingLength, accessLength,
                spawnSpecType, useCSVFile, useSpecificSimTime, singleSpawnSpecName,
                mixedSpawnDistribution, numberOfSimulations, fileLocation, specsToIncludeForRandomSpawn);
        this.parkingLaneSets = parkingLaneSets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Simulator getSimulator() {
        double currentTime = 0.0;

        CPMBasicMap layout = new CPMCarParkMultiLaneWidth(speedLimit,
                currentTime,
                parkingLength,
                accessLength,
                parkingLaneSets);

        // Set up the correct spawn point
        switch (spawnSpecType) {
            case SINGLE:
                if (!useCSVFile.getKey()) {
                    CPMMapUtil.setUpInfiniteSingleSpecVehicleSpawnPoint(layout, trafficLevel, singleSpawnSpecName);
                } else {
                    CPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(layout, useCSVFile, singleSpawnSpecName);
                }
                break;
            case RANDOM:
                if (!useCSVFile.getKey()) {
                    CPMMapUtil.setUpInfiniteRandomSpecVehicleSpawnPoint(layout, trafficLevel, specsToIncludeForRandomSpawn);
                } else {
                    CPMMapUtil.setUpSpecificRandomSpecVehicleSpawnPoint(layout, useCSVFile, specsToIncludeForRandomSpawn);
                }
                break;
            case MIXED:
                if (mixedSpawnDistribution == null) {
                    throw new RuntimeException("No distribution has been given!");
                }
                if (!useCSVFile.getKey()) {
                    CPMMapUtil.setUpInfiniteMixedSpecVehicleSpawnPoint(layout, trafficLevel, mixedSpawnDistribution);
                } else {
                    CPMMapUtil.setUpSpecificMixedSpecVehicleSpawnPoint(layout, useCSVFile, mixedSpawnDistribution);
                }
                break;
        }

        return new CPMAutoDriverSimulator(layout, useSpecificSimTime.getValue());
    }

    public int getNumberOfParkingLanes() {
        int numberOfParkingLanes = 0;
        for (Pair<Integer,Double> pair : parkingLaneSets) {
            numberOfParkingLanes += pair.getKey();
        }
        return numberOfParkingLanes;
    }

    public List<Pair<Integer, Double>> getParkingLaneSets() {
        return parkingLaneSets;
    }
}
