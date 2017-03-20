package aim4.vehicle.cpm;

import aim4.driver.AutoDriver;
import aim4.driver.Driver;
import aim4.driver.cpm.CPMV2VDriver;
import aim4.map.cpm.parking.ParkingLane;
import aim4.vehicle.BasicAutoVehicle;
import aim4.vehicle.VehicleSpec;

import java.awt.geom.Point2D;

/**
 * The basic autonomous vehicle for CPM simulations.
 */
public class CPMBasicAutoVehicle extends BasicAutoVehicle {

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /**
     * The Driver controlling this vehicle.
     */
    protected CPMV2VDriver driver;

    /**
     * The target parking lane for this vehicle, assigned
     * on entry and on re-entry of the car park.
     */
    protected ParkingLane targetParkingLane;

    /**
     * The length of time the vehicle should park for,
     * from entering the car park (crossing the entry
     * sensored line) to when it should change it's
     * parking status to EXITING.
     */
    protected double parkingTime;

    /**
     * The time left until the vehicle needs to exit the car park.
     * Starts with parkingTime and is decreased with every timestep
     * by the coordinator (who updates the parking status when it's
     * tme to exit.)
     */
    protected double timeToExit;

    protected boolean hasEntered;

    // messaging

    /**
     * The inbox for messages from the car park StatusMonitor.
     * There will only ever be one message, which will be a
     * ParkingLane (or null, if no room for vehicle to park).
     */
    private ParkingLane I2Vinbox;

    /**
     * The outbox for messages to the StatusMonitor.
     * There will only ever be one message, which
     * will be to enter, re-enter or exit the car
     * park.
     // TODO CPM somehow need to send VIN so StatusMonitor knows who msg if from
     private ParkingStatus V2Ioutbox;*/

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
    public CPMBasicAutoVehicle(VehicleSpec spec,
                               Point2D pos,
                               double heading,
                               double steeringAngle,
                               double velocity,
                               double targetVelocity,
                               double acceleration,
                               double currentTime,
                               double parkingTime) {
        super(spec, pos, heading, velocity, steeringAngle, acceleration,
                targetVelocity, currentTime);
        this.targetParkingLane = null;
        this.parkingTime = parkingTime;
        this.timeToExit = parkingTime;
        this.hasEntered = false;
    }

    @Override
    public AutoDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(Driver driver) {
        assert driver instanceof CPMV2VDriver;
        this.driver = (CPMV2VDriver) driver;
    }

    public double getParkingTime() { return parkingTime; }

    public double getTimeToExit() { return timeToExit; }

    public boolean hasEnteredCarPark() {
        return hasEntered;
    }

    public void setHasEntered() {
        assert !hasEntered;
        hasEntered = true;
    }

    public ParkingLane getTargetParkingLane() {
        return targetParkingLane;
    }

    public void setTargetParkingLane(ParkingLane targetParkingLane) {
        this.targetParkingLane = targetParkingLane;
    }

    public void clearTargetParkingLane(){
        targetParkingLane = null;
    }

    /**
     * Find out the distance between the front of the vehicle and
     * the ParkingLane's parking end point.
     * @return the distance between the front of the vehicle and
     * the parking end point of the lane that the driver is currently on.
     */
    public double distanceToParkingEndPoint(){
        assert(driver.getCurrentLane() instanceof ParkingLane);
        Point2D endPoint = ((ParkingLane) driver.getCurrentLane()).getParkingEndPoint();
        Point2D vehiclePosition = gaugePosition();
        return vehiclePosition.distance(endPoint);
    }

    public void sendMessageToI2VInbox(ParkingLane parkingLane) {
        I2Vinbox = parkingLane;

    }

    public ParkingLane getMessagesFromInbox() {
        return I2Vinbox;
    }

    public void clearV2Iinbox() {
        System.out.println("vehicle inbox cleared");
        I2Vinbox = null;
    }

    public void updateTimeToExit(double timeStep) {
        if (timeToExit > 0) {
            timeToExit -= timeStep;
        }
    }
}
