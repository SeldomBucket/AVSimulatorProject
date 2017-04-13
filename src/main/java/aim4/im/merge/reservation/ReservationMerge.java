package aim4.im.merge.reservation;

import aim4.map.connections.MergeConnection;

import java.awt.geom.Area;

/**
 * Created by Callum on 13/04/2017.
 */
public class ReservationMerge extends ReservationBase {
    //CONSTANTS//
    private static final int RESERVATION_CLEAN_UP_PERIOD = 30;

    //NESTED CLASSES//
    public class TimeReservation extends ReservationBase.TimeReservation{
        public TimeReservation(int dt) {
            super(dt);
        }

        public double getTime() {
            return getDiscreteTime() * mergeTimeStep;
        }
    }

    //PRIVATE FIELDS//
    /**
     * The merge connection being reserved
     */
    private MergeConnection merge;
    /**
     * The time step
     */
    private double mergeTimeStep;

    //CONSTRUCTOR//
    public ReservationMerge(MergeConnection merge, double mergeTimeStep) {
        super();
        this.merge = merge;
        this.mergeTimeStep = mergeTimeStep;
    }

    //PUBLIC METHODS//
    //ACTIONS
    /**
     * Cleans the reservations
     */
    public void cleanUp(double currentTime) {
        int currentDiscreteTime = calcDiscreteTime(currentTime);
        if (currentDiscreteTime % RESERVATION_CLEAN_UP_PERIOD == 0) {
            cleanUp(currentDiscreteTime);
        }
    }

    //ACCESSORS
    public double getMergeTimeStep() {
        return mergeTimeStep;
    }

    //CALCULATIONS
    /**
     * Get the discrete time of a given time.  If the given time is not
     * exactly equal to the discrete time, the largest discrete time that
     * is smaller or equal to the given time will be returned.
     *
     * @param time  the time
     * @return the discrete time
     */
    public int calcDiscreteTime(double time) {
        return (int)(time/mergeTimeStep);
    }

    /**
     * Get the remaining time in the merge time step of the given time
     *
     * @param time  the time
     * @return the remaining time in the grid time step
     */
    public double calcRemainingTime(double time) {
        return time - mergeTimeStep * calcDiscreteTime(time);
    }

    /**
     * Get the time of a given discrete time.
     *
     * @param discreteTime  the time
     * @return the time
     */
    public double calcTime(int discreteTime) {
        return discreteTime * mergeTimeStep;
    }

    /**
     * Get the last time at which any time was reserved.
     *
     * @return the last time at which any time was reserved;
     *         -1 if there is currently no reservation.
     */
    public double getLastReservedTime() {
        return super.getLastReservedDiscreteTime() * mergeTimeStep;
    }
}
