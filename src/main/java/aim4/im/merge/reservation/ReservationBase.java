package aim4.im.merge.reservation;

import java.util.*;

/**
 * Created by Callum on 13/04/2017.
 */
public class ReservationBase {
    //PUBLIC NESTED CLASS//
    public static class TimeReservation {
        /**
         * The discrete time.
         */
        private int dt;

        /**
         * Create a time-reservation.
         *
         * @param dt the discrete time
         */
        public TimeReservation(int dt) {
            this.dt = dt;
        }

        /**
         * Get the discrete time.
         *
         * @return the discrete time
         */
        public int getDiscreteTime() {
            return dt;
        }
    }

    //PRIVATE FIELDS//
    /**
     * A mapping from discrete times to reservationIds
     */
    private NavigableMap<Integer, Integer> timeToRID;
    /**
     * A mapping from reservationIds to times
     */
    private NavigableMap<Integer, List<Integer>> ridToTime;

    //CONSTRUCTOR//

    /**
     * Creates a new reservation system
     */
    public ReservationBase() {
        timeToRID = new TreeMap<Integer, Integer>();
        ridToTime = new TreeMap<Integer, List<Integer>>();
    }

    //PUBLIC METHODS//

    /**
     * Whether the time has been reserved
     * @param dt
     */
    public boolean isReserved(int dt) {
        if(timeToRID.containsKey(dt))
            return true;
        else
            return false;
    }

    /**
     * Gets the reservation ID that reserved the time
     * @param dt
     * @return
     */
    public int getReservationID(int dt) {
        if(timeToRID.containsKey(dt))
            return timeToRID.get(dt);
        else
            return -1;
    }

    /**
     * Check whether a given reservation ID exists
     *
     * @param rid  the reservation ID
     * @return whether the reservation ID exists
     */
    public boolean hasReservation(int rid) {
        return ridToTime.containsKey(rid);
    }

    /**
     * Get the last time at which any time-tile has been reserved.
     *
     * @return the last time at which any time-tile has been reserved;
     *         -1 if there is currently no reservation.
     */
    public int getLastReservedDiscreteTime() {
        try {
            return timeToRID.lastKey();
        } catch(NoSuchElementException e) {
            return -1;
        }
    }

    /**
     * Make the reservation of a set of times with a given reservation id.
     * If the reservation is not successful, no times will be reserved.
     *
     * @param rid          the reservation ID
     * @param workingList  a collection of times to be reserved
     *
     * @return whether the reservation is successful
     */
    public boolean reserve(int rid, Collection<? extends TimeReservation> workingList) {
        for(TimeReservation tr : workingList) {
            int dt = tr.getDiscreteTime();
            if(timeToRID.containsKey(dt)){
                return false; //the time has been reserved.
            }
        }

        int timeBegin = 0;
        try {
            timeBegin = timeToRID.firstKey();
        } catch (NoSuchElementException e) {
            //No times reserved. All times acceptable.
        }

        for(TimeReservation tr : workingList) {
            int dt = tr.getDiscreteTime();

            if(dt >= timeBegin) {
                //Update timeToRID
                timeToRID.put(dt, rid);
                //Update ridToTime
                if(!ridToTime.containsKey(rid))
                    ridToTime.put(rid, new ArrayList<Integer>());
            }
            ridToTime.get(rid).add(dt);
        }
        return true;
    }

    /**
     * Cancel a reservation
     *
     * @param rid  the reservation ID
     * @return whether the cancellation is successful
     */
    public boolean cancel(int rid) {
        if(ridToTime.containsKey(rid)) {
            List<Integer> times = ridToTime.remove(rid);
            for(Integer time : times)
                timeToRID.remove(time);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove all reservations before a given discrete time.
     * @param dt the discrete time before which the reservations will be removed
     */
    public void cleanUp(int dt) {
        List<Integer> timesToRemove = new ArrayList<Integer>();
        //Clean up timeToRID
        try {
            while (timeToRID.firstKey() < dt) {
                timesToRemove.add(timeToRID.firstKey());
                timeToRID.remove(timeToRID.firstKey());
            }
        } catch (NoSuchElementException e) {
            //do nothing
        }
        //Clean to ridToTime
        List<Integer> ridsToRemove = new ArrayList<Integer>();
        for(int rid : ridToTime.keySet()) {
            ridToTime.get(rid).removeAll(timesToRemove);
            if(ridToTime.get(rid).isEmpty())
                ridsToRemove.add(rid);
        }
        for(int rid : ridsToRemove)
            ridToTime.remove(rid);
    }
}