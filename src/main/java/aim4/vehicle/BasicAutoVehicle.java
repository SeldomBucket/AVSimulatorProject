package aim4.vehicle;

import aim4.driver.aim.AIMAutoDriver;
import aim4.map.lane.Lane;
import aim4.noise.DoubleGauge;

import java.awt.geom.Point2D;

/**
 * Created by Callum on 17/11/2016.
 */
public class BasicAutoVehicle extends BasicVehicle implements AutoVehicleSimModel {
    /////////////////////////////////
    // CONSTANTS
    /////////////////////////////////

    // These control how the sensor range (side to side) changes during turning

    /**
     * Sensor angle multiplier (based on steering angle) for the side of the
     * Vehicle toward which the Vehicle is turning. Currently a factor of
     * {@value}.
     */
    private static final double SENSOR_RANGE_MULT_B = 1.5;

    /**
     * Sensor angle multiplier (based on steering angle) for the side of the
     * Vehicle away from which the Vehicle is turning. Currently a factor of
     * {@value}.
     */
    private static final double SENSOR_RANGE_MULT_S = 0.25;

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /**
     * The Driver controlling this vehicle.
     */
    private AIMAutoDriver driver;

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////
    // intervalometer

    /**
     * A gauge indicating the distance to the vehicle in front of this one. If
     * no vehicle is detected in front of this one, the gauge reads its maximum
     * value.
     */
    private DoubleGauge intervalometer = new DoubleGauge();

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    // LRF

    /**
     * The current operating mode of the vehicle's laser range finder.
     */
    private AutoVehicleDriverModel.LRFMode lrfMode = AutoVehicleDriverModel.LRFMode.DISABLED;

    /**
     * A gauge indicating whether or not the Laser Range finder is currently
     * sensing anything.
     */
    private boolean lrfSensing = false;
    /**
     * A gauge indicating the angle from the vehicle to the object currently
     * sensed by the Laser Range Finder, in radians.  An angle of 0 means
     * straight ahead. As with the steerometer, positive angles are to the left,
     * negative are to the right.
     */
    private DoubleGauge lrfAngle = new DoubleGauge();
    /**
     * A gauge indicating the distance from the vehicle to the object currently
     * sensed by the Laser Range Finder, in meters.  This distance is measured
     * from the point between the front wheels of the vehicle.
     */
    private DoubleGauge lrfDistance = new DoubleGauge();


    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    // vehicle tracking

    /**
     * A gauge indicating whether or not the vehicle tracking devices
     * are currently sensing anything.
     */
    private boolean vehicleTracking = false;
    /**
     * The target lane for the vehicle tracking devices.
     */
    private Lane vehicleTrackingTargetLane = null;
    /**
     * A gauge holding the distance, in meters, between p1 and p2, both of them
     * are points on the target lane, where p1 is the point projected from the
     * center of the front of the vehicle, and p2 is the nearest point of
     * another vehicle in the front of the vehicle to p1 on the target lane.
     * If there is no vehicle in the front on the target lane, the value
     * should be Double.MAX_VALUE.
     */
    private DoubleGauge frontVehicleDistanceSensor = new DoubleGauge();
    /**
     * A gauge holding the distance, in meters, between p1 and p2, both of them
     * are points on the target lane, where p1 is the point projected from the
     * center of the front of the vehicle, and p2 is the nearest point of
     * another vehicle behind of the vehicle to p1 on the target lane.
     * If there is no vehicle behind on the target lane, the value
     * should be Double.MAX_VALUE.
     */
    private DoubleGauge rearVehicleDistanceSensor = new DoubleGauge();
    /**
     * A gauge holding the speed, in meters per second, of the vehicle in front
     * of the vehicle on the target lane.  If there is no vehicle in the front on
     * the target lane, the value should be Double.MAX_VALUE.
     */
    private DoubleGauge frontVehicleSpeedSensor = new DoubleGauge();
    /**
     * A gauge holding the speed, in meters per second, of the vehicle behind
     * the vehicle on the target lane.  If there is no vehicle behind on the
     * target lane, the balue should be Double.MAX_VALUE.
     */
    private DoubleGauge rearVehicleSpeedSensor = new DoubleGauge();


    /**
     * Construct a vehicle
     *
     * @param spec           the vehicle's specification
     * @param pos            the initial position of the Vehicle
     * @param heading        the initial heading of the Vehicle
     * @param velocity       the initial velocity of the Vehicle
     * @param steeringAngle  the initial steering angle of the Vehicle
     * @param acceleration   the initial acceleration of the Vehicle
     * @param targetVelocity the initial target velocity
     * @param currentTime    the current time
     */
    public BasicAutoVehicle(VehicleSpec spec, Point2D pos, double heading, double velocity, double steeringAngle, double acceleration, double targetVelocity, double currentTime) {
        super(spec, pos, heading, velocity, steeringAngle, acceleration, targetVelocity, currentTime);
    }

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public AIMAutoDriver getDriver() {
        return driver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDriver(AIMAutoDriver driver) {
        this.driver = driver;
    }

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    // LRFS sensor (not implemented)

    /**
     * Set the Vehicle's laser range finder operating mode.
     *
     * @param mode the new laser range finder mode
     * @see        AutoVehicleDriverModel.LRFMode
     */
    public void setLRFMode(AutoVehicleDriverModel.LRFMode mode) {
        this.lrfMode = mode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AutoVehicleDriverModel.LRFMode getLRFMode() {
        return lrfMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLRFSensing() {
        return lrfSensing;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLRFSensing(boolean sensing) {
        lrfSensing = sensing;
    }

    /**
     * Get this Vehicle's laser range finder angle gauge.
     * This should <b>only</b> be followed by a call to <code>read</code>,
     * <b>except</b> in the actual physical simulator which is allowed to set
     * these values.
     *
     * @return the Vehicle's laser range finder angle gauge
     */
    public DoubleGauge getLRFAngle() {
        return lrfAngle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleGauge getLRFDistance() {
        return lrfDistance;
    }


    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    // vehicle tracking (for lane changing)

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVehicleTracking() {
        return vehicleTracking;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVehicleTracking(boolean sensing) {
        vehicleTracking = sensing;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTargetLaneForVehicleTracking(Lane lane) {
        vehicleTrackingTargetLane = lane;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Lane getTargetLaneForVehicleTracking() {
        return vehicleTrackingTargetLane;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleGauge getFrontVehicleDistanceSensor() {
        return frontVehicleDistanceSensor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleGauge getRearVehicleDistanceSensor() {
        return rearVehicleDistanceSensor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleGauge getFrontVehicleSpeedSensor() {
        return frontVehicleSpeedSensor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleGauge getRearVehicleSpeedSensor() {
        return rearVehicleSpeedSensor;
    }

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    // intervalometer

    /**
     * {@inheritDoc}
     */
    @Override
    public DoubleGauge getIntervalometer() {
        return intervalometer;
    }


}
