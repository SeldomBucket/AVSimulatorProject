package aim4.vehicle.merge;

import aim4.driver.Driver;
import aim4.driver.merge.MergeV2IAutoDriver;
import aim4.msg.merge.i2v.I2VMergeMessage;
import aim4.msg.merge.v2i.V2IMergeMessage;
import aim4.vehicle.VehicleSpec;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Callum on 13/04/2017.
 */
public class MergeV2IAutoVehicle extends MergeBasicAutoVehicle implements MergeV2IAutoVehicleSimModel {

    // CONSTANTS //
    /**
     * The default distance the Vehicle can transmit messages.
     * {@value} meters.
     */
    public static final double DEFAULT_TRANSMISSION_POWER = Double.MAX_VALUE; // meters

    // PRIVATE FIELDS //
    /**
     * The maximum distance this vehicle can transmit a message, in meters.
      */
    private double transmissionPower = DEFAULT_TRANSMISSION_POWER;
    /**
     * The messages waiting to be sent from the Vehicle to a
     * MergeManager.
     */
    private Queue<V2IMergeMessage> v2iOutbox = new LinkedList<V2IMergeMessage>();

    /**
     * The messages waiting to be received from a MergeManager and
     * processed by the Vehicle.
     */
    private Queue<I2VMergeMessage> i2vInbox = new LinkedList<I2VMergeMessage>();

    // CONSTRUCTOR //
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
    public MergeV2IAutoVehicle(VehicleSpec spec, Point2D pos, double heading, double velocity, double steeringAngle, double acceleration, double targetVelocity, double currentTime) {
        super(spec, pos, heading, velocity, steeringAngle, acceleration, targetVelocity, currentTime);
    }

    // PUBLIC METHODS //
    // DRIVER
    /**
     * {@inheritDoc}
     */
    @Override
    public MergeV2IAutoDriver getDriver() {
        assert driver instanceof MergeV2IAutoDriver;
        return (MergeV2IAutoDriver) driver;
    }

    /**
     * {@inheritDoc}
     *
     * In this instance, forces Driver to be a MergeV2IAutoDriver
     */
    @Override
    public void setDriver(Driver driver) {
        assert driver instanceof MergeV2IAutoDriver;
        super.setDriver(driver);
    }

    // MESSAGING
    /**
     * Set the Vehicle's transmission power.
     *
     * @param transmissionPower the new transmission power, in meters
     */
    public void setTransmissionPower(double transmissionPower) {
        this.transmissionPower = transmissionPower;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTransmissionPower() {
        return transmissionPower;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public Queue<V2IMergeMessage> getV2IOutbox() {
        return v2iOutbox;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<I2VMergeMessage> pollAllMessagesFromI2VInbox() {
        List<I2VMergeMessage> msgs = new ArrayList<I2VMergeMessage>(i2vInbox);
        i2vInbox.clear();
        return msgs;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void send(V2IMergeMessage msg) {
        v2iOutbox.add(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receive(I2VMergeMessage msg) {
        i2vInbox.add(msg);
    }

}
