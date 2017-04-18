package aim4.im.merge.reservation.grid;

import aim4.config.Constants;
import aim4.driver.Driver;
import aim4.driver.merge.MergeAutoDriver;
import aim4.im.ReservationManager;
import aim4.map.connections.MergeConnection;
import aim4.map.lane.Lane;
import aim4.map.merge.MergeMap;
import aim4.msg.merge.v2i.Request;
import aim4.util.TiledArea;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VehicleUtil;
import aim4.vehicle.merge.MergeBasicAutoVehicle;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Callum on 17/04/2017.
 */

/**
 * The reservation grid manager.
 */
public class ReservationMergeGridManager implements
        ReservationManager<ReservationMergeGridManager.Query,
                ReservationMergeGridManager.Plan,
                Integer> {
    /////////////////////////////////
    // NESTED CLASSES
    /////////////////////////////////

    /**
     * The configuration of the reservation grid manager.
     */
    public static class Config {
        /**
         * The simulation time step.
         */
        private double timeStep;
        /**
         * The length of a discrete time step in the grid
         */
        private double gridTimeStep;
        /**
         * The size of the static buffer, in meters, used by this policy.
         */
        private double staticBufferSize;
        /**
         * The size of the time buffer, in seconds, used for internal tiles.
         */
        private double internalTileTimeBufferSize;
        /**
         * The size of the time buffer, in seconds, used for edge tiles.
         */
        private double edgeTileTimeBufferSize;
        /**
         * Whether or not the edge tile time buffer is enabled.
         */
        private boolean isEdgeTileTimeBufferEnabled;
        /**
         * The granularity.
         */
        private double granularity;

        /**
         * Create a configuration object.
         *
         * @param timeStep
         * @param gridTimeStep
         * @param staticBufferSize
         * @param internalTileTimeBufferSize
         * @param edgeTileTimeBufferSize
         * @param isEdgeTileTimeBufferEnabled
         * @param granularity
         */
        public Config(double timeStep,
                      double gridTimeStep,
                      double staticBufferSize,
                      double internalTileTimeBufferSize,
                      double edgeTileTimeBufferSize,
                      boolean isEdgeTileTimeBufferEnabled,
                      double granularity) {
            this.timeStep = timeStep;
            this.gridTimeStep = gridTimeStep;
            this.staticBufferSize = staticBufferSize;
            this.internalTileTimeBufferSize = internalTileTimeBufferSize;
            this.edgeTileTimeBufferSize = edgeTileTimeBufferSize;
            this.isEdgeTileTimeBufferEnabled = isEdgeTileTimeBufferEnabled;
            this.granularity = granularity;
        }

        /**
         * Get the time step.
         *
         * @return the time step
         */
        public double getTimeStep() {
            return timeStep;
        }

        /**
         * Get the grid time step.
         *
         * @return the grid time step
         */
        public double getGridTimeStep() {
            return gridTimeStep;
        }

        /**
         * Get the static buffer size.
         *
         * @return the static buffer size
         */
        public double getStaticBufferSize() {
            return staticBufferSize;
        }

        /**
         * Get the internal time buffer size.
         *
         * @return the internal tile time buffer size
         */
        public double getInternalTileTimeBufferSize() {
            return internalTileTimeBufferSize;
        }

        /**
         * Get the edge buffer size.
         *
         * @return the edge tile time buffer size
         */
        public double getEdgeTileTimeBufferSize() {
            return edgeTileTimeBufferSize;
        }

        /**
         * Get whether the edge buffer is enabled.
         *
         * @return whether the edge buffer is enabled
         */
        public boolean getIsEdgeTileTimeBufferEnabled() {
            return isEdgeTileTimeBufferEnabled;
        }

        /**
         * Get the granularity of the tile.
         *
         * @return the granularity of the tile.
         */
        public double getGranularity() {
            return granularity;
        }
    }

    /**
     * The reservation grid manager.
     */
    public static class Query {

        /** The VIN of the vehicle */
        private int vin;
        /** The arrival time */
        private double arrivalTime;
        /** The arrival velocity */
        private double arrivalVelocity;
        /** The ID of the arrival lane */
        private int arrivalLineId;
        /** The ID of the departure lane */
        private int departureLaneId;
        /** The vehicle specification for request message */
        private Request.VehicleSpecForRequestMsg spec;
        /** The maximum turn velocity */
        private double maxTurnVelocity;
        /** Whether the acceleration is allowed */
        private boolean accelerating;

        /**
         * Create a query.
         *
         * @param vin              the VIN of the vehicle
         * @param arrivalTime      the arrival time
         * @param arrivalVelocity  the arrival velocity
         * @param arrivalLineId    the arrival lane ID
         * @param departureLaneId  the departure lane ID
         * @param spec             the vehicle specification
         * @param maxTurnVelocity  the maximum turn velocity
         * @param accelerating     Whether the acceleration is allowed
         */
        public Query(int vin, double arrivalTime, double arrivalVelocity,
                     int arrivalLineId, int departureLaneId, Request.VehicleSpecForRequestMsg spec,
                     double maxTurnVelocity, boolean accelerating) {
            this.vin = vin;
            this.arrivalTime = arrivalTime;
            this.arrivalVelocity = arrivalVelocity;
            this.arrivalLineId = arrivalLineId;
            this.departureLaneId = departureLaneId;
            this.spec = spec;
            this.maxTurnVelocity = maxTurnVelocity;
            this.accelerating = accelerating;
        }

        /**
         * Get the VIN of a vehicle.
         *
         * @return the VIN of a vehicle
         */
        public int getVin() {
            return vin;
        }

        /**
         * Get the arrival time.
         *
         * @return the arrival time
         */
        public double getArrivalTime() {
            return arrivalTime;
        }

        /**
         * Get the arrival velocity.
         *
         * @return the arrival velocity
         */
        public double getArrivalVelocity() {
            return arrivalVelocity;
        }

        /**
         * Get the arrival lane ID.
         *
         * @return the arrival lane ID
         */
        public int getArrivalLaneId() {
            return arrivalLineId;
        }

        /**
         * Get the departure lane ID.
         *
         * @return the departure lane ID
         */
        public int getDepartureLaneId() {
            return departureLaneId;
        }

        /**
         * Get the specification of the vehicle for the request message.
         *
         * @return the specification of the vehicle
         */
        public Request.VehicleSpecForRequestMsg getSpec() {
            return spec;
        }

        /**
         * Get the maximum turn velocity.
         *
         * @return the maximum turn velocity
         */
        public double getMaxTurnVelocity() {
            return maxTurnVelocity;
        }

        /**
         * Whether the vehicle is allowed to accelerate.
         *
         * @return whether the vehicle is allowed to accelerate
         */
        public boolean isAccelerating() {
            return accelerating;
        }
    }

    /**
     * The plan for the reservation.
     */
    public static class Plan {
        /** The VIN of the vehicle */
        private int vin;
        /** The exit time */
        private double exitTime;
        /** The exit velocity */
        private double exitVelocity;
        /** The list of time tiles reserved */
        private List<ReservationMergeGrid.TimeTile> workingList;
        /** The acceleration profile */
        private Queue<double[]> accelerationProfile;

        /**
         * Create the plan for the reservation.
         *
         * @param vin                  the VIN of the vehicle
         * @param exitTime             the exit time
         * @param exitVelocity         the exit velocity
         * @param workingList          the list of time tiles reserved
         * @param accelerationProfile  the acceleration profile
         */
        public Plan(int vin,
                    double exitTime,
                    double exitVelocity,
                    List<ReservationMergeGrid.TimeTile> workingList,
                    Queue<double[]> accelerationProfile) {
            this.vin = vin;
            this.exitTime = exitTime;
            this.exitVelocity = exitVelocity;
            this.workingList = workingList;
            this.accelerationProfile = accelerationProfile;
        }

        /**
         * Get the VIN of the vehicle.
         *
         * @return the VIN of the vehicle
         */
        public int getVin() {
            return vin;
        }

        /**
         * Get the exit time.
         *
         * @return the exit time
         */
        public double getExitTime() {
            return exitTime;
        }

        /**
         * Get the exit velocity.
         *
         * @return the exit velocity
         */
        public double getExitVelocity() {
            return exitVelocity;
        }

        /**
         * Get the list of time tiles reserved.
         *
         * @return the list of time tiles reserved
         */
        public List<ReservationMergeGrid.TimeTile> getWorkingList() {
            return workingList;
        }

        /**
         * Get the acceleration profile.
         *
         * @return the acceleration profile
         */
        public Queue<double[]> getAccelerationProfile() {
            return accelerationProfile;
        }
    }

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    // TODO: replace config with other things

    /**
     * The configuration of this reservation grid manager.
     */
    public Config config;

    /**
     * The size of the static buffer, in meters, used by this policy.
     */
    public double staticBufferSize;
    /**
     * Whether or not the edge tile time buffer is enabled.
     */
    private boolean isEdgeTileTimeBufferEnabled;
    /**
     * The size of the time buffer, in time steps, used for internal tiles.
     */
    private int internalTileTimeBufferSteps;
    /**
     * The size of the time buffer, in time steps, used for edge tiles.
     */
    private int edgeTileTimeBufferSteps;
    /**
     * The current time.
     */
    private double currentTime;
    /**
     * The merge
     */
    private MergeConnection merge;
    /**
     * The tiled area of the merge
     */
    private TiledArea tiledArea;
    /**
     * The reservation System
     */
    private ReservationMergeGrid reservationGrid;

    /**
     * The current layout of the map.
     */
    private final MergeMap layout;


    /////////////////////////////////
    // CONSTRUCTORS
    /////////////////////////////////

    /**
     * Create a reservation grid manager.
     *
     * @param config           the configuration of the grid manager
     * @param merge     the merge
     * @param tiledArea        the tiled area
     * @param reservationGrid  the reservation grid
     */
    public ReservationMergeGridManager(Config config,
                                       MergeConnection merge,
                                       TiledArea tiledArea,
                                       ReservationMergeGrid reservationGrid,
                                       MergeMap layout){
        this(0.0, config, merge, tiledArea, reservationGrid, layout);
    }

    /**
     * Create a reservation grid manager.
     *
     * @param currentTime      the current time
     * @param config           the configuration of the grid manager
     * @param merge     the merge
     * @param tiledArea        the tiled area
     * @param reservationGrid  the reservation grid
     */
    public ReservationMergeGridManager(double currentTime,
                                       Config config,
                                       MergeConnection merge,
                                       TiledArea tiledArea,
                                       ReservationMergeGrid reservationGrid,
                                       MergeMap layout) {
        this.currentTime = currentTime;
        this.config = config;
        this.staticBufferSize = config.getStaticBufferSize();
        this.isEdgeTileTimeBufferEnabled = config.getIsEdgeTileTimeBufferEnabled();
        this.internalTileTimeBufferSteps =
                (int) (config.getInternalTileTimeBufferSize() / config.getGridTimeStep());
        this.edgeTileTimeBufferSteps =
                (int) (config.getEdgeTileTimeBufferSize() / config.getGridTimeStep());

        this.merge = merge;
        this.tiledArea = tiledArea;
        this.reservationGrid = reservationGrid;
        this.layout = layout;
    }


    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    /**
     * Advance the time step.
     *
     * @param timeStep  the time step
     */
    public void act(double timeStep) {
        reservationGrid.cleanUp(currentTime);
        currentTime += timeStep;
    }

    /**
     * Get the configuration.
     *
     * @return the configuration
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Get the tiled area.
     *
     * @return the tiled area
     */
    public TiledArea getTiledArea() {
        return tiledArea;
    }

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    /**
     * Find a set of space-time tile for a particular traversal proposal in
     * a request message.  This attempt can be either with attempting to
     * setMaxAccelWithMaxTargetVelocity to maximum velocity or with a constant velocity.
     * @param q  the query object
     *
     * @return a set of space-time tiles on the trajectory and
     *         the exit velocity of the vehicle if the reservation is
     *         successful; otherwise return null.
     */
    @Override
    public Plan query(Query q) {
        // Position the Vehicle to be ready to start the simulation
        Lane arrivalLane =
                layout.getLaneRegistry().get(q.getArrivalLaneId());
        Lane departureLane =
                layout.getLaneRegistry().get(q.getDepartureLaneId());

        // Create a test vehicle to use in the internal simulation.
        MergeBasicAutoVehicle testVehicle =
                createTestVehicle(q.getSpec(),
                        q.getArrivalVelocity(),
                        q.getMaxTurnVelocity(),
                        arrivalLane);

        // Create a dummy driver to steer it
        Driver dummy = new MergeAutoDriver(testVehicle, layout);
        dummy.setCurrentLane(arrivalLane);
        // Assign driver to vehicle
        testVehicle.setDriver(dummy);
        testVehicle.getIntervalometer().record(Double.MAX_VALUE); //No next vehicle

        // Keep track of the TileTimes that will make up this reservation
        TileTimesBySimulationResult tileSimResult = findTileTimesBySimulation(testVehicle,
                                                                              dummy,
                                                                              q.getArrivalTime(),
                                                                              q.isAccelerating());

        if (tileSimResult != null) {
            List<ReservationMergeGrid.TimeTile> workingList = tileSimResult.getWorkingList();

            double exitTime = workingList.get(workingList.size()-1).getTime();

            Queue<double[]> accelerationProfile =
                    calcAccelerationProfile(q.getArrivalTime(),
                            q.getArrivalVelocity(),
                            q.getMaxTurnVelocity(),
                            q.getSpec().getMaxAcceleration(),
                            tileSimResult.getExitTime(),
                            q.isAccelerating());

            return new Plan(q.getVin(),
                    exitTime,
                    testVehicle.gaugeVelocity(),
                    workingList,
                    accelerationProfile);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer accept(Plan plan) {
        boolean b = reservationGrid.reserve(plan.getVin(), plan.getWorkingList());
        assert b;
        return plan.getVin();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel(Integer reservationId) {
        reservationGrid.cancel(reservationId);  // reservationId == vin
    }


    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /**
     * Create a test vehicle to use in the internal simulation.
     *
     * @param spec             the specification of the test vehicle
     * @param arrivalVelocity  the arrival velocity of the vehicle
     * @param maxVelocity      the Vehicle's maximum velocity, in meters per
     *                         second
     * @param arrivalLane      the arrival lane of the vehicle in this proposal
     *
     * @return             whether or not a reservation could be made
     */
    private MergeBasicAutoVehicle createTestVehicle(
            Request.VehicleSpecForRequestMsg spec,
            double arrivalVelocity,
            double maxVelocity,
            Lane arrivalLane) {

        VehicleSpec newSpec = new VehicleSpec(
                "TestVehicle",
                spec.getMaxAcceleration(),
                spec.getMaxDeceleration(),
                maxVelocity,  // TODO: why not one in msg.getSpec().getMaxVelocity()
                spec.getMinVelocity(),
                spec.getLength(),
                spec.getWidth(),
                spec.getFrontAxleDisplacement(),
                spec.getRearAxleDisplacement(),
                0.0, // wheelSpan
                0.0, // wheelRadius
                0.0, // wheelWidth
                spec.getMaxSteeringAngle(),
                spec.getMaxTurnPerSecond());

        MergeBasicAutoVehicle testVehicle = new MergeBasicAutoVehicle(
                newSpec,
                merge.getEntryPoint(arrivalLane), //Position
                arrivalLane.getInitialHeading(), //Heading
                0.0, //Steering angle
                arrivalVelocity, //Velocity
                0.0, // Target velocity
                0.0, // Acceleration
                0.0 // The current time
        );
        return testVehicle;
    }

    /**
     * The record for holding the result of the time tiles found
     * by the internal simulation.
     */
    private static class TileTimesBySimulationResult {
        /** The time tiles */
        List<ReservationMergeGrid.TimeTile> workingList;
        /** The exit time */
        double exitTime;

        /**
         * Create a record for holding the result of the time tiles found
         * by the internal simulation.
         *
         * @param workingList  the time tiles
         * @param exitTime     the exit time
         */
        public TileTimesBySimulationResult(List<ReservationMergeGrid.TimeTile> workingList,
                                           double exitTime) {
            this.workingList = workingList;
            this.exitTime = exitTime;
        }

        /**
         * Get the time tiles.
         *
         * @return the time tiles
         */
        public List<ReservationMergeGrid.TimeTile> getWorkingList() {
            return workingList;
        }

        /**
         * Get the exit time.
         *
         * @return the exit time
         */
        public double getExitTime() {
            return exitTime;
        }

    }
    /**
     * Find a list of unreserved tiletimes by simulation
     *
     * @param dummy         the dummy driver
     * @param arrivalTime   the arrival time of the vehicle
     * @param accelerating  whether or not to setMaxAccelWithMaxTargetVelocity to maximum velocity
     *                      during the traversal
     *
     * @return A list of tiles that can be reserved by the vehicle. If returns
     *         null, the trajectory hits some reserved tiles and the reservation
     *         fails.
     */
    private TileTimesBySimulationResult
    findTileTimesBySimulation(MergeBasicAutoVehicle testVehicle,
                              Driver dummy,
                              double arrivalTime,
                              boolean accelerating) {
        // The area of the merge
        Area areaPlus = merge.getArea();
        // The following must be true because the test vehicle
        // starts at the entry point of the merge.
        assert areaPlus.contains(testVehicle.getPointAtMiddleFront(Constants.DOUBLE_EQUAL_PRECISION));

        // The list of tile-times that will make up this reservation
        List<ReservationMergeGrid.TimeTile> workingList = new ArrayList<ReservationMergeGrid.TimeTile>();

        // A discrete representation of the time throughout the internal simulation
        // Notice that currentIntTime != arrivalTime
        int currentIntTime = reservationGrid.calcDiscreteTime(arrivalTime);
        // The duration in the current time interval
        double currentDuration = reservationGrid.calcRemainingTime(arrivalTime);

        // drive the test vehicle until it leaves the merge
        while(VehicleUtil.intersectsHighPrecision(testVehicle, areaPlus)) {
            moveTestVehicle(testVehicle, dummy, currentDuration, accelerating);
            // Find out which tiles are occupied by the vehicle
            currentIntTime++;  // Record that we've moved forward one time step
            List<TiledArea.Tile> occupied =
                    tiledArea.findOccupiedTiles(testVehicle.getShape(staticBufferSize));

            // Make sure none of these tiles are reserved by someone else already
            for(TiledArea.Tile tile : occupied) {

                // Figure out how large of a time buffer to use, based on whether or
                // not this is an edge tile
                int buffer;
                if (isEdgeTileTimeBufferEnabled && tile.isEdgeTile()) {
                    buffer = edgeTileTimeBufferSteps;
                } else {
                    buffer = internalTileTimeBufferSteps;
                }
                int tileId = tile.getId();
                for(int t = currentIntTime - buffer; t <= currentIntTime + buffer; t++){
                    // If the tile is already reserved and it isn't by us, we've failed
                    if (!reservationGrid.isReserved(t, tileId)) {
                        workingList.add(reservationGrid.new TimeTile(t, tile.getId()));
                    } else {
                        return null; // Failure! Just bail!
                    }
                }
            }
            currentDuration = reservationGrid.getGridTimeStep();
        }

        return new TileTimesBySimulationResult(workingList,
                reservationGrid
                        .calcTime(currentIntTime));
    }

    /**
     * Advance the test vehicle by one time step
     *
     * @param testVehicle   the test vehicle
     * @param dummy         the dummy driver
     * @param accelerating  whether or not to setMaxAccelWithMaxTargetVelocity to maximum velocity
     *                      during the traversal
     */
    private void moveTestVehicle(MergeBasicAutoVehicle testVehicle,
                                 Driver dummy,
                                 double duration,
                                 boolean accelerating) {
        // Give the dummy a chance to steer
        dummy.act();
        // Now control the vehicle's acceleration
        if(accelerating) {
            // Accelerate at maximum rate, topping out at maximum velocity
            testVehicle.setMaxAccelWithMaxTargetVelocity();   // TODO: use other function instead of
            // setMaxAccelWithMaxTargetVelocity()
        } else {
            // Maintain a constant speed
            testVehicle.coast();
        }
        // Now move the vehicle
        testVehicle.move(duration);
    }

    /**
     * Compute the acceleration profile.
     *
     * @param arrivalTime      the arrival time of the vehicle
     * @param arrivalVelocity  the arrival velocity of the vehicle
     * @param maxVelocity      the maximum velocity of the vehicle
     * @param maxAcceleration  the maximum acceleration of the vehicle
     * @param exitTime         the time at which the vehicle exits the
     *                         merge
     * @param accelerating  whether or not to setMaxAccelWithMaxTargetVelocity to maximum velocity
     *                      during the traversal
     *
     * @return  a sequence of acceleration pair (acceleration, duration)
     */
    private Queue<double[]> calcAccelerationProfile(double arrivalTime,
                                                    double arrivalVelocity,
                                                    double maxVelocity,
                                                    double maxAcceleration,
                                                    double exitTime,
                                                    boolean accelerating) {
        // Calculate the accelerations
        Queue<double[]> accelerationProfile = new LinkedList<double[]>();
        // Figure out how long we took to traverse the merge
        double traversalTime = exitTime - arrivalTime;
        if (traversalTime <= 0.0) {
            System.err.printf("traversalTime = %.10f\n", traversalTime);
        }
        assert traversalTime > 0.0;
        if (accelerating && (maxVelocity > arrivalVelocity)) {
            // How much of the time did we spend accelerating
            double accelerationDuration =
                    Math.min(traversalTime, (maxVelocity-arrivalVelocity)/maxAcceleration);
            // Add in the time spent accelerating, if any
            assert accelerationDuration > 0.0;
            accelerationProfile.add(
                    new double[] { maxAcceleration, accelerationDuration });
            // Fill the remaining time with constant speed, if any remains
            if(accelerationDuration < traversalTime) {
                accelerationProfile.add(
                        new double[] { 0.0, traversalTime - accelerationDuration });
            }
        } else {  // Fixed speed reservation
            // Just add in the time we crossed, all at constant speed
            accelerationProfile.add(new double[] { 0.0, traversalTime });
        }
        return accelerationProfile;
    }
}
