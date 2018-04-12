package aim4.vehicle.mixedcpm;

import aim4.driver.AutoDriver;
import aim4.driver.Driver;
import aim4.driver.mixedcpm.coordinator.MixedCPMManualCoordinator;
import aim4.driver.mixedcpm.MixedCPMManualDriver;
import aim4.map.connections.BasicConnection;
import aim4.map.lane.Lane;
import aim4.map.mixedcpm.parking.AutomatedParkingRoad;
import aim4.map.mixedcpm.parking.ManualStall;
import aim4.map.mixedcpm.parking.StallSpec;
import aim4.map.mixedcpm.parking.StallType;
import aim4.vehicle.BasicAutoVehicle;
import aim4.vehicle.VehicleSpec;
import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class MixedCPMBasicAutoVehicle extends MixedCPMBasicVehicle {

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /**
     * The Driver controlling this vehicle.
     */
    protected MixedCPMManualDriver driver;

    /**
     * The target stall for this vehicle, assigned
     * on entry and on re-entry of the car park.
     */
    protected AutomatedParkingRoad targetLane;

    // messaging

    /**
     * The inbox for messages from the car park IStatusMonitor.
     * There will only ever be one message, which will be a
     * ManualStall (or null, if no room for vehicle to park).
     */
    protected AutomatedParkingRoad I2Vinbox;



    /**
     * Construct a vehicle
     *
     * @param spec            the vehicle's specification
     * @param pos             the initial position of the Vehicle
     * @param heading         the initial heading of the Vehicle
     * @param steeringAngle   the initial steering angle of the Vehicle
     * @param velocity        the initial velocity of the Vehicle
     * @param targetVelocity  the initial target velocity
     * @param acceleration    the initial acceleration of the Vehicle
     * @param currentTime     the current time
     * @param parkingTime     how long the vehicle will park for
     */
    public MixedCPMBasicAutoVehicle(VehicleSpec spec,
                                      Point2D pos,
                                      double heading,
                                      double steeringAngle,
                                      double velocity,
                                      double targetVelocity,
                                      double acceleration,
                                      double currentTime,
                                      double parkingTime) {
        super(spec, pos, heading, velocity, steeringAngle, acceleration,
                targetVelocity, currentTime, parkingTime);
        this.targetLane = null;

    }

    @Override
    public AutoDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(Driver driver) {
        assert driver instanceof MixedCPMManualDriver;
        this.driver = (MixedCPMManualDriver) driver;
    }


    public AutomatedParkingRoad getTargetLane() {
        return targetLane;
    }

    public boolean inInTargetStall(){
        return this.driver.isInStall();
    }

    public void setTargetLane(AutomatedParkingRoad targetLane) {
        if (this.targetLane == null) {
            this.targetLane = targetLane;
        }

    }


    public double distanceToParkingEndPoint(){
        Point2D endPoint = driver.getVehicle().getTargetStall().getRoad().getOnlyLane().getEndPoint();
        Point2D vehiclePosition = gaugePointAtMiddleFront(0);
        return vehiclePosition.distance(endPoint);
    }

    public double horizontalDistanceToStallEndPoint(){
        Point2D endPoint = driver.getVehicle().getTargetStall().getRoad().getOnlyLane().getEndPoint();
        Point2D vehiclePosition = gaugePosition();
        double distance = abs(vehiclePosition.getX() - endPoint.getX());
        return distance;
    }

    public void clearTargetLane(){
        targetLane = null;
    }

    public void sendMessageToI2VInbox(AutomatedParkingRoad message) {
        I2Vinbox = message;
    }

    public void clearI2Vinbox() {
        //System.out.println("I2V inbox cleared");
        I2Vinbox = null;
    }

    public AutomatedParkingRoad getMessagesFromI2VInbox() {
        return I2Vinbox;
    }

}
