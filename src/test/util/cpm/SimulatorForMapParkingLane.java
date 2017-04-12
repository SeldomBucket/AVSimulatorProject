package util.cpm;

import aim4.config.Debug;
import aim4.config.DebugPoint;
import aim4.driver.cpm.CPMCoordinator;
import aim4.driver.cpm.CPMV2VDriver;
import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.cpm.*;
import aim4.map.cpm.parking.ParkingLane;
import aim4.map.cpm.parking.SensoredLine;
import aim4.map.cpm.parking.StatusMonitor;
import aim4.map.cpm.testmaps.CPMMapParkingLane;
import aim4.map.lane.Lane;
import aim4.sim.Simulator;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.vehicle.VehicleSimModel;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VinRegistry;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

/**
 * Create a simulator for the CPMMapParkingLane.
 * It ensures to spawn vehicles with the correct target parking lane,
 * driving state and parking status. Does not need to check if vehicles
 * cross any sensored lines.
 */
public class SimulatorForMapParkingLane extends CPMAutoDriverSimulator {

    public SimulatorForMapParkingLane(CPMMapParkingLane map){
        super(map);
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

    /////////////////////////////////
    // STEP 1
    /////////////////////////////////

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
                    // Check that the car park caters for vehicles this wide
                    double vehicleWidth = spawnSpec.getVehicleSpec().getWidth();
                    double parkingLaneWidth = map.getRoadByName("Parking Road 0").getOnlyLane().getWidth();
                    if (parkingLaneWidth < (vehicleWidth + MIN_DISTANCE_BETWEEN_PARKED_VEHICLES)) {
                        System.out.println("Spawned vehicle discarded: map doesn't cater for vehicles this wide.");
                    } else {
                        // Only create the vehicle if there is room in the car park
                        double vehicleLength = spawnSpec.getVehicleSpec().getLength();
                        ParkingLane parkingLane = (ParkingLane)map.getRoadByName("Parking Road 0").getOnlyLane();
                        if (parkingLane.getTotalParkingLength() > vehicleLength) { // This does not take into account any cars already parked there
                            CPMBasicAutoVehicle vehicle = makeVehicle(spawnPoint, spawnSpec);
                            VinRegistry.registerVehicle(vehicle); // Get vehicle a VIN number
                            vinToVehicles.put(vehicle.getVIN(), vehicle);
                            map.addVehicleToMap(vehicle);
                            break; // only handle the first spawn vehicle
                        } else {
                            System.out.println("Spawned vehicle discarded: not enough room.");
                        }
                    }
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
                        ((CPMMapParkingLane)map).getOnlyParkingLane());
        // Set the driver
        MockCPMDriver driver = new MockCPMDriver(vehicle, map, CPMCoordinator.ParkingStatus.PARKING,
                                                 CPMCoordinator.DrivingState.TRAVERSING_PARKING_LANE);
        driver.setCurrentLane(lane);
        driver.setSpawnPoint(spawnPoint);
        vehicle.setDriver(driver);

        return vehicle;
    }



    /////////////////////////////////
    // STEP 5
    /////////////////////////////////

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

            if (Debug.isPrintVehicleStateOfVIN(vehicle.getVIN())) {
                vehicle.printState();
            }
        }
    }
}
