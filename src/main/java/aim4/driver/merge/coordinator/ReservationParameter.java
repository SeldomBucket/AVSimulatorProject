package aim4.driver.merge.coordinator;

/**
 * Created by Callum on 17/04/2017.
 */

import aim4.map.lane.Lane;
import aim4.map.merge.MergeMap;
import aim4.msg.merge.i2v.Confirm;

import java.util.Queue;

/**
 * Postprocessing the reservation parameters
 */
public class ReservationParameter {

    /**
     * The Lane in which the Vehicle should arrive at the merge.
     */
    private Lane arrivalLane;

    /**
     * The Lane in which the Vehicle will depart the merge.
     */
    private Lane departureLane;

    /**
     * The time at which the Vehicle should arrive at the merge.
     */
    private double arrivalTime;

    /**
     * The allowed amount of time, in seconds before the exact planned arrival
     * time for which the Vehicle is allowed to arrive at the merge.
     */
    private double earlyError;

    /**
     * The allowed amount of time, in seconds after the exact planned arrival
     * time for which the Vehicle is allowed to arrive at the merge.
     */
    private double lateError;

    /**
     * The velocity, in meters per second, at which the Vehicle should arrive
     * at the merge.
     */
    private double arrivalVelocity;

    /**
     * The distance after the merge that is protected by an Admission
     * Control Zone.
     */
    private double aczDistance;

    /**
     * The list of acceleration/duration pairs the vehicle should use to
     * cross the merge safely.  If empty or null, the vehicle should
     * accelerate to top speed or the speed limit, whichever is lower.
     */
    private Queue<double[]> accelerationProfile;

    /////////////////////////////////
    // CONSTRUCTORS
    /////////////////////////////////

    /**
     * Create a reservation parameter object
     */
    public ReservationParameter(Confirm msg, MergeMap map) {
        this.arrivalLane =
                map.getLaneRegistry().get(msg.getArrivalLaneID());
        this.departureLane =
                map.getLaneRegistry().get(msg.getDepartureLaneID());
        this.arrivalTime = msg.getArrivalTime();
        this.earlyError = msg.getEarlyError();
        this.lateError = msg.getLateError();
        this.arrivalVelocity = msg.getArrivalVelocity();
        this.aczDistance = msg.getACZDistance();
        this.accelerationProfile = msg.getAccelerationProfile();
    }

    /**
     * Get the Lane in which this driver agent's Vehicle should
     * arrive to comply with the reservation this driver agent is holding. If
     * the driver agent is not holding a reservation, the return value is not
     *  defined.
     *
     * @return the arrival lane for the reservation this driver agent is holding
     */
    public Lane getArrivalLane() {
        return arrivalLane;
    }

    /**
     * Get the Lane in which this driver agent's Vehicle should
     * arrive to comply with the reservation this driver agent is holding. If
     * the driver agent is not holding a reservation, the return value is not
     * defined.
     *
     * @return the departure Lane for the reservation this driver agent is
     *         holding
     */
    public Lane getDepartureLane() {
        return departureLane;
    }

    /**
     * Get the arrival time of the reservation this driver agent is holding. If
     * the driver agent is not holding a reservation, the return value is not
     * defined.
     *
     * @return the arrival time of the reservation this driver agent is holding
     */
    public double getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Get the maximum amount of time, in seconds, before the official arrival
     * time that the driver agent's vehicle can arrive at the merge, for
     * the current reservation the driver agent is holding.  If the driver agent
     * is not holding a reservation, the return value is undefined.
     *
     * @return the maximum early error for the driver agent's current
     *         reservation
     */
    public double getEarlyError() {
        return earlyError;
    }

    /**
     * Get the maximum amount of time, in seconds, after the official arrival
     * time that the driver agent's vehicle can arrive at the merge, for
     * the current reservation the driver agent is holding.  If the driver agent
     * is not holding a reservation, the return value is undefined.
     *
     * @return the maximum late error for the driver agent's current
     *         reservation
     */
    public double getLateError() {
        return lateError;
    }

    /**
     * Get the arrival velocity, in meters per second, of the reservation this
     * driver agent is holding. If the driver agent is not holding a
     * reservation, the return value is not defined.
     *
     * @return the arrival velocity of the reservation this driver agent is
     *         holding
     */
    public double getArrivalVelocity() {
        return arrivalVelocity;
    }

    /**
     * Get the distance past the merge which is controlled by the
     * Admission Control Zone after the merge for the reservation this
     * driver agent is holding.
     *
     * @return the distance of the Admission Control Zone after the merge
     *         for the reservation this driver agent is holding
     */
    public double getACZDistance() {
        return aczDistance;
    }

    /**
     * Get the list of acceleration/duration pairs that describe the required
     * velocity profile of the driver agent's Vehicle as it crosses the
     * merge in accordance with its current reservation.  If the driver
     * agent is not holding a reservation, the return value is not defined.
     *
     * @return the acceleration profile of the reservation this driver agent
     *         is currently holding
     */
    public Queue<double[]> getAccelerationProfile() {
        return accelerationProfile;
    }

}