package util.cpm;

import aim4.driver.cpm.CPMCoordinator;
import aim4.driver.cpm.CPMV2VDriver;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

/**
 * Created by Becci on 12-Apr-17.
 */
public class MockCPMCoordinator extends CPMCoordinator {

    /**
     * Create a basic V2V Coordinator to coordinate a Vehicle in CPM.
     *
     * @param vehicle the Vehicle it will coordinate.
     * @param driver  the driver agent it is a part of.
     */
    public MockCPMCoordinator(CPMBasicAutoVehicle vehicle, CPMV2VDriver driver) {
        super(vehicle, driver);
    }

    /**
     * Create a basic V2V Coordinator to coordinate a Vehicle in CPM.
     *
     * @param vehicle the Vehicle it will coordinate.
     * @param driver  the driver agent it is a part of.
     */
    public MockCPMCoordinator(CPMBasicAutoVehicle vehicle, CPMV2VDriver driver,
                              DrivingState drivingState, ParkingStatus parkingStatus) {
        super(vehicle, driver);
        this.parkingStatus = parkingStatus;
        this.drivingState = drivingState;
    }
}
