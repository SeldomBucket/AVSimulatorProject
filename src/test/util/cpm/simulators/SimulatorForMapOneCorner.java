package util.cpm.simulators;

import aim4.driver.cpm.CPMCoordinator.*;
import aim4.map.DataCollectionLine;
import aim4.map.cpm.CPMBasicMap;
import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.CPMSpawnPoint;
import aim4.map.lane.Lane;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VinRegistry;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;
import util.cpm.MockCPMBasicAutoVehicle;
import util.cpm.MockCPMDriver;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * Created by Becci on 12-Apr-17.
 */
public class SimulatorForMapOneCorner extends CPMAutoDriverSimulator {

    ParkingStatus initialParkingStatusForSpawnedVehicles;
    DrivingState initialDrivingStateForSpawnedVehicles;

    public SimulatorForMapOneCorner(CPMBasicMap map, ParkingStatus parkingStatus,
                                    DrivingState drivingState) {
        super(map);
        this.initialDrivingStateForSpawnedVehicles = drivingState;
        this.initialParkingStatusForSpawnedVehicles = parkingStatus;
    }

    @Override
    public SimStepResult step(double timeStep) {
        spawnVehicles(timeStep);
        provideSensorInput();
        findNextVehicles();
        letDriversAct();
        moveVehicles(timeStep);
        observeParkedVehicles();
        observeNumberOfVehiclesInCarPark();
        List<Integer> completedVINs = cleanUpCompletedVehicles();
        currentTime += timeStep;
        return new CPMAutoDriverSimStepResult(completedVINs);
    }

    /**
     * Spawn vehicles.
     *
     * @param timeStep  the time step
     */
    @Override
    protected void spawnVehicles(double timeStep) {
        for(CPMSpawnPoint spawnPoint : map.getSpawnPoints()) {
            if (canSpawnVehicle(spawnPoint)) {
                List<CPMSpawnPoint.CPMSpawnSpec> spawnSpecs = spawnPoint.act(timeStep);
                for(CPMSpawnPoint.CPMSpawnSpec spawnSpec : spawnSpecs) {
                        CPMBasicAutoVehicle vehicle = makeVehicle(spawnPoint, spawnSpec);
                        VinRegistry.registerVehicle(vehicle); // Get vehicle a VIN number
                        vinToVehicles.put(vehicle.getVIN(), vehicle);
                        map.addVehicleToMap(vehicle);
                        break; // only handle the first spawn vehicle
                }
            } // else ignore the spawnSpecs and do nothingSystem.out.println("No vehicle spawned: canSpawn = False.");
        }
    }

    /**
     * Create a vehicle at a spawn point.
     *
     * @param spawnPoint  the spawn point
     * @param spawnSpec   the spawn specification
     * @return the vehicle
     */
    @Override
    protected CPMBasicAutoVehicle makeVehicle(CPMSpawnPoint spawnPoint,
                                              CPMSpawnPoint.CPMSpawnSpec spawnSpec) {
        VehicleSpec spec = spawnSpec.getVehicleSpec();
        Lane lane = spawnPoint.getLane();
        // Now just take the minimum of the max velocity of the vehicle, and
        // the speed limit in the lane
        double initVelocity = Math.min(spec.getMaxVelocity(), lane.getSpeedLimit());
        // Generate a length of time that this car should park for
        // This is from entering to when the EXITING state is set.


        // Obtain a Vehicle
        MockCPMBasicAutoVehicle vehicle =
                new MockCPMBasicAutoVehicle(spec,
                        spawnPoint.getPosition(),
                        spawnPoint.getHeading(),
                        spawnPoint.getSteeringAngle(),
                        initVelocity, // velocity
                        initVelocity,  // target velocity
                        spawnPoint.getAcceleration(),
                        spawnSpec.getSpawnTime(),
                        spawnSpec.getParkingTime(),
                        null,
                        true);
        // Set the driver
        MockCPMDriver driver = new MockCPMDriver(vehicle, map,
                initialParkingStatusForSpawnedVehicles,
                initialDrivingStateForSpawnedVehicles);
        driver.setCurrentLane(lane);
        driver.setSpawnPoint(spawnPoint);
        vehicle.setDriver(driver);

        return vehicle;
    }

    /**
     * Move all the vehicles.
     *
     * @param timeStep  the time step
     */
    @Override
    protected void moveVehicles(double timeStep) {
        for(CPMBasicAutoVehicle vehicle : vinToVehicles.values()) {
            Point2D p1 = vehicle.getPosition();
            vehicle.move(timeStep);
            Point2D p2 = vehicle.getPosition();

            CPMMapUtil.checkVehicleStillOnMap(map, p2, vehicle.getDriver().getCurrentLane());

            // Check if we've gone through a data collection line
            for(DataCollectionLine line : map.getDataCollectionLines()) {
                line.intersect(vehicle, currentTime, p1, p2);
            }

            // Update the time left for the vehicle to be parked.
            if (vehicle.hasEnteredCarPark()) {
                vehicle.updateTimeToExit(timeStep);
            }
        }
    }
}
