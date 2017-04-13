package aim4.im.merge.reservation;

import aim4.map.connections.MergeConnection;

/**
 * Created by Callum on 13/04/2017.
 */
public class ReservationMergeManager {
    private double currentTime;
    private Config config;
    private ReservationMergeArea reservationMergeArea;
    private MergeConnection merge;

    //NESTED CLASSES//
    /**
     * The configuration of the ReservationMergeManager
     */
    public static class Config {
        /**
         * The simulation time step;
         */
        private double timeStep;

        public Config(double timeStep) {
            this.timeStep = timeStep;
        }

        //ACCESSORS//
        public double getTimeStep() {
            return this.timeStep;
        }
    }

    //CONSTRUCTOR
    public ReservationMergeManager(double currentTime,
                                   Config config,
                                   ReservationMergeArea reservationMergeArea,
                                   MergeConnection merge) {
        this.currentTime = currentTime;
        this.config = config;
        this.merge = merge;
        this.reservationMergeArea = reservationMergeArea;
    }

    //PUBLIC METHODS
    //ACTION
    public void act(double timeStep) {
        currentTime += timeStep;
    }

    public Config getConfig() {
        return config;
    }
}
