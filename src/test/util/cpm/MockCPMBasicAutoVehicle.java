package util.cpm;

import aim4.driver.AutoDriver;
import aim4.driver.Driver;
import aim4.driver.cpm.CPMCoordinator;
import aim4.driver.cpm.CPMV2VDriver;
import aim4.map.connections.BasicConnection;
import aim4.map.cpm.parking.ParkingLane;
import aim4.vehicle.BasicAutoVehicle;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.awt.geom.Point2D;

/**
 * Spawn a vehicle will a specific target Parking Lane.
 */
public class MockCPMBasicAutoVehicle extends CPMBasicAutoVehicle {


    /**
     * Construct a vehicle
     *
     * @param spec           the vehicle's specification
     * @param pos            the initial position of the Vehicle
     * @param heading        the initial heading of the Vehicle
     * @param steeringAngle  the initial steering angle of the Vehicle
     * @param velocity       the initial velocity of the Vehicle
     * @param targetVelocity the initial target velocity
     * @param acceleration   the initial acceleration of the Vehicle
     * @param currentTime    the current time
     * @param parkingTime    how long the vehicle will park for
     */
    public MockCPMBasicAutoVehicle(VehicleSpec spec, Point2D pos, double heading, double steeringAngle,
                                   double velocity, double targetVelocity, double acceleration,
                                   double currentTime, double parkingTime, ParkingLane targetParkingLane,
                                   boolean hasEntered) {
        super(spec, pos, heading, steeringAngle, velocity, targetVelocity, acceleration, currentTime, parkingTime);
        this.targetParkingLane = targetParkingLane;
        this.hasEntered = hasEntered;
    }
}
