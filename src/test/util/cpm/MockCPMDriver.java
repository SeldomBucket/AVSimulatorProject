package util.cpm;

import aim4.driver.cpm.CPMCoordinator;
import aim4.driver.cpm.CPMV2VDriver;
import aim4.map.cpm.CPMMap;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

/**
 * Spawn a driver with a specific initial parking status and driving state.
 */
public class MockCPMDriver extends CPMV2VDriver {

    CPMCoordinator.ParkingStatus initialParkingStatus;
    CPMCoordinator.DrivingState initialDrivingState;


    public MockCPMDriver(CPMBasicAutoVehicle vehicle, CPMMap map,
                         CPMCoordinator.ParkingStatus parkingStatus,
                         CPMCoordinator.DrivingState drivingState) {
        super(vehicle, map);
        this.initialDrivingState = drivingState;
        this.initialParkingStatus = parkingStatus;
    }

    /**
     * Take control actions for driving the agent's Vehicle.  This allows
     * both the Coordinator and the Pilot to act (in that order).
     */
    @Override
    public void act() {
        if (coordinator == null){
            // Create a new coordinator if the vehicle doesn't already have one.
            coordinator = new MockCPMCoordinator(vehicle, (CPMV2VDriver)vehicle.getDriver(),
                                                 initialDrivingState, initialParkingStatus);
        }

        // the newly created coordinator can be called immediately.
        if (!coordinator.isTerminated()) {
            coordinator.act();
        }
    }

    public CPMCoordinator.ParkingStatus getParkingStatus() { return coordinator.getParkingStatus(); }

    public CPMCoordinator.DrivingState getDrivingStatus() { return coordinator.getDrivingState(); }
}
