package util.cpm.simulators;

import aim4.driver.cpm.CPMCoordinator.*;
import aim4.map.cpm.CPMBasicMap;
import aim4.map.cpm.CPMSpawnPoint;
import aim4.map.lane.Lane;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;
import util.cpm.MockCPMBasicAutoVehicle;
import util.cpm.MockCPMDriver;

/**
 * Created by Becci on 12-Apr-17.
 */
public class SimulatorForCarParkWithStatus extends CPMAutoDriverSimulator {

    public SimulatorForCarParkWithStatus(CPMBasicMap map) {
        super(map);
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
                        false);
        // Set the driver
        MockCPMDriver driver = new MockCPMDriver(vehicle, map,
                ParkingStatus.WAITING, DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
        driver.setCurrentLane(lane);
        driver.setSpawnPoint(spawnPoint);
        vehicle.setDriver(driver);

        return vehicle;
    }
}
