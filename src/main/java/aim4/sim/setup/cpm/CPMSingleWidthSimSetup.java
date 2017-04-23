package aim4.sim.setup.cpm;

import aim4.map.cpm.CPMBasicMap;
import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.CPMCarParkSingleLaneWidth;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.sim.Simulator;
import javafx.util.Pair;

import java.util.List;

/**
 * Setup for simulation of AVs in an AV specific car park which are self-organising.
 */
public class CPMSingleWidthSimSetup extends BasicCPMSimSetup {

    /** The width of all lanes */
    protected double laneWidth;
    /** The number of parking rows in the car park. */
    protected int numberOfParkingLanes;
    /**
     * Create a setup for the simulator in which all vehicles are autonomous.
     *
     * @param simSetup  the basic simulator setup
     */
    public CPMSingleWidthSimSetup(CPMSingleWidthSimSetup simSetup) {
        super(simSetup);
        this.laneWidth = simSetup.laneWidth;
        this.numberOfParkingLanes = simSetup.numberOfParkingLanes;
    }

    public CPMSingleWidthSimSetup(double speedLimit, double trafficLevel,
                            double parkingLength, double accessLength,
                            CPMMapUtil.SpawnSpecType spawnSpecType,
                            Pair<Boolean, String> useCSVFile,
                            Pair<Boolean, Double> useSpecificSimTime,
                            String singleSpawnSpecName,
                            List<Double> mixedSpawnDistribution,
                            double laneWidth,
                            int numberOfParkingLanes) {
        super(speedLimit, trafficLevel, parkingLength, accessLength, spawnSpecType,
         useCSVFile, useSpecificSimTime, singleSpawnSpecName, mixedSpawnDistribution);
        this.laneWidth = laneWidth;
        this.numberOfParkingLanes = numberOfParkingLanes;
    }

    public double getLaneWidth() {
        return laneWidth;
    }

    public void setLaneWidth(double laneWidth) {
        this.laneWidth = laneWidth;
    }

    public int getNumberOfParkingLanes() {
        return numberOfParkingLanes;
    }

    public void setNumberOfParkingLanes(int numberOfParkingLanes) {
        this.numberOfParkingLanes = numberOfParkingLanes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Simulator getSimulator() {
        double currentTime = 0.0;

        CPMBasicMap layout = new CPMCarParkSingleLaneWidth(laneWidth, // laneWidth
                speedLimit,
                currentTime,
                numberOfParkingLanes,
                parkingLength,
                accessLength);

        // Set up the correct spawn point
        switch(spawnSpecType) {
            case SINGLE:
                if (!useCSVFile.getKey()){
                    CPMMapUtil.setUpInfiniteSingleSpecVehicleSpawnPoint(layout, trafficLevel, singleSpawnSpecName);
                } else {
                    CPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(layout, useCSVFile, singleSpawnSpecName);
                }
                break;
            case RANDOM:
                if (!useCSVFile.getKey()){
                    CPMMapUtil.setUpInfiniteRandomSpecVehicleSpawnPoint(layout, trafficLevel);
                } else {
                    CPMMapUtil.setUpSpecificRandomSpecVehicleSpawnPoint(layout, useCSVFile);
                }
                break;
            case MIXED:
                if (mixedSpawnDistribution == null) {
                    throw new RuntimeException("No distribution has been given!");
                }
                if (!useCSVFile.getKey()){
                    CPMMapUtil.setUpInfiniteMixedSpecVehicleSpawnPoint(layout, trafficLevel, mixedSpawnDistribution);
                } else {
                    CPMMapUtil.setUpSpecificMixedSpecVehicleSpawnPoint(layout, useCSVFile, mixedSpawnDistribution);
                }
                break;
        }

        return new CPMAutoDriverSimulator(layout, useSpecificSimTime.getValue());
    }
}
