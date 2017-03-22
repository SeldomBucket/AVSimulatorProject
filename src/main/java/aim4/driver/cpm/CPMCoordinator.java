package aim4.driver.cpm;

import aim4.config.Debug;
import aim4.driver.aim.coordinator.Coordinator;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.cpm.parking.ParkingLane;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.util.EnumMap;

/**
 * This class has a similar role to the V2ICoodinator for AIM.
 * It should handle the messages between vehicles, and with
 * the paypoint (if we have one). This includes processing messages
 * and sending messages.
 */
public class CPMCoordinator implements Coordinator{

    /**
     * The different parking statuses that an agent can have.
     */
    public enum ParkingStatus {
        /** The vehicle has been spawned and is waiting to enter the car park.
         * If there is enough room they will be granted access by recieving
         * the parking lane they should park in (the one with the most room).
         * If there is not enough space, the vehicle will not recieve a parking
         * lane and they should continue to wait.
         * */
        WAITING,
        /** The vehicle has entered, or re-entered, the car park. They are
         * now making their way to a parking lane to park.*/
        PARKING,
        /** The vehicle is relocating - they leave the parking lane and reenter
         * the car park, allowing a vehicle it was blocking to exit. On reentry,
         * they are given a new parking lane and go back to PARKING.
         * */
        RELOCATING,
        /**
         * The vehicle is being retrieved and should exit the car park.
         * */
        EXIT
    }

    /////////////////////////////////
    // NESTED INTERFACES
    /////////////////////////////////

    /**
     * An interface of the state handler.
     */
    private static interface StateHandler {
        /**
         * Perform the action defined by the state handler at the driver state.
         *
         * @return true if the driver agent should proceed to the next action
         * immediately.
         */
        boolean perform();
    }

    /**
     * Potential states that a CoordinatingDriverAgent can be in.  This is one
     * aspect of how the two subagents, the Pilot and the Coordinator,
     * communicate.
     */
    public enum DrivingState {
        /**
         * The agent simply follows the current lanes until it exits the simulator.
         * the intersection
         */
        DEFAULT_DRIVING_BEHAVIOUR,
        TRAVERSING_CORNER,
        TRAVERSING_JUNCTION,
        TRAVERSING_INTERSECTION,
        TRAVERSING_PARKING_LANE,
        // TODO CPM Find out what this is
        TERMINAL_STATE
    }

    /** The Vehicle being coordinated by this coordinator. */
    private CPMBasicAutoVehicle vehicle;

    /** The driver of which this coordinator is a part. */
    private CPMV2VDriver driver;

    /** The sub-agent that controls physical manipulation of the vehicle */
    private CPMPilot pilot;

    /** The sub-agent that decides where to go when there is a choice. */
    private CPMNavigator navigator;

    /**
     * The corner that the agent is currently in, if any.
     * Used to determine if it has entered a new corner.
     * */
    private Corner currentCorner;

    /**
     * The junction that the agent is currently in, if any.
     * Used to determine if it has enetered a new junction.
     * */
    private Junction currentJunction;

    // state

    /**
     * The current driving state of the agent.
     * This is part of how the two sub-agents communicate.
     */
    private DrivingState drivingState;

    /**
     * The current parking status of the agent.
     * This is part of how the two sub-agents communicate.
     */
    private ParkingStatus parkingStatus;

    /**
     * The most recent time at which the state was changed.
     */
    private double lastStateChangeTime = 0.0;

    /**
     * The state handlers
     */
    private EnumMap<DrivingState,StateHandler> stateHandlers;

    /////////////////////////////////
    // CONSTRUCTORS
    /////////////////////////////////

    /**
     * Create an basic V2V Coordinator to coordinate a Vehicle in CPM.
     *
     * @param vehicle  the Vehicle to coordinate
     * @param driver   the driver
     */
    public CPMCoordinator(CPMBasicAutoVehicle vehicle,
                          CPMV2VDriver driver){
        this.vehicle = vehicle;
        this.driver = driver;
        this.navigator = new CPMNavigator(vehicle, driver);
        this.pilot = new CPMPilot(vehicle, driver, navigator);


        initStateHandlers();

        // Set the intial driving state of the coordinator
        setDrivingState(DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
        // Set the initial parking status of the coordinator
        setParkingStatus(ParkingStatus.WAITING);
    }

    @Override
    public void act() {
        checkTimeToExit();
        processI2Vinbox();
        callStateHandlers();
    }

    /**
     * Check the time left until exit, and change parking status
     * to exiting if it is time to do so.
     */
    private void checkTimeToExit() {
        if (vehicle.getTimeToExit() <= 0
                && parkingStatus != ParkingStatus.EXIT) {
            System.out.println("Parking time has elapsed, " +
                               "setting parking status to EXIT.");
            parkingStatus = ParkingStatus.EXIT;
        }
    }

    /**
     * Process any messages in the I2V inbox, from the StatusMonitor.
     */
    private void processI2Vinbox() {
        ParkingLane I2Vinbox = vehicle.getMessagesFromInbox();
        if ((I2Vinbox != null && parkingStatus == ParkingStatus.WAITING) ||
                (I2Vinbox != null && parkingStatus == ParkingStatus.RELOCATING) ) {
            // We have been granted access to the car park and know where to park
            System.out.println("Changing status to PARKING.");
            setParkingStatus(ParkingStatus.PARKING);
            vehicle.setTargetParkingLane(I2Vinbox);
            System.out.println("Parking on " + I2Vinbox.getRoadName());
            vehicle.setHasEntered();
        }
    }

    /**
     * The main loop for calling the state handlers
     */
    private void callStateHandlers() {
        boolean shouldContinue = true;
        while(shouldContinue) {
            if (stateHandlers.containsKey(drivingState)) {
                shouldContinue = stateHandlers.get(drivingState).perform();
            } else {
                throw new RuntimeException("Unknown state.");
            }
        }
    }

    // State

    /**
     * Get the current state of the CoordinatingDriverAgent.
     *
     * @return the current state of the driver agent
     */
    public DrivingState getDrivingState() {
        return drivingState;
    }

    /////////////////////////////////
    // STATE HANDLERS
    /////////////////////////////////

    /**
     * Initialize the state handlers.
     */
    private void initStateHandlers() {
        stateHandlers = new EnumMap<DrivingState,StateHandler>(DrivingState.class);

        stateHandlers.put(DrivingState.DEFAULT_DRIVING_BEHAVIOUR,
                new DefaultDrivingBehaviourStateHandler());

        stateHandlers.put(DrivingState.TRAVERSING_CORNER,
                new TraversingCornerStateHandler());

        stateHandlers.put(DrivingState.TRAVERSING_JUNCTION,
                new TraversingJunctionStateHandler());

        stateHandlers.put(DrivingState.TRAVERSING_INTERSECTION,
                new TraversingIntersectionStateHandler());

        stateHandlers.put(DrivingState.TRAVERSING_PARKING_LANE,
                new TraversingParkingLaneStateHandler());

        stateHandlers.put(DrivingState.TERMINAL_STATE,
                terminalStateHandler);
    }

    /**
     * The terminal state handler.
     */
    private static StateHandler terminalStateHandler =
            new StateHandler() {
                @Override
                public boolean perform() {
                    return false;  // do nothing, not even the pilot
                }
            };

    /**
     * The state handler for the default driving behavior state.
     */
    private class DefaultDrivingBehaviourStateHandler implements StateHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            // First check if we are in a Connection or ParkingLane.
            // If so, then switch to the relevant traversing mode.
            assert driver != null;
            if (driver.inCorner() != null){
                currentCorner = driver.inCorner();
                setDrivingState(DrivingState.TRAVERSING_CORNER);
            }
            if (driver.inJunction() != null){
                currentJunction = driver.inJunction();
                setDrivingState(DrivingState.TRAVERSING_JUNCTION);
            }
            if (driver.inIntersection() != null){
                setDrivingState(DrivingState.TRAVERSING_INTERSECTION);
            }
            if (driver.getCurrentLane() instanceof ParkingLane) {
                System.out.println("Traversing Parking Lane" + driver.getCurrentLane());
                setDrivingState(DrivingState.TRAVERSING_PARKING_LANE);
            }
            pilot.followCurrentLane();
            pilot.simpleThrottleAction();
            return false;
        }
    }

    /**
     * The state handler for the traversing corner state.
     */
    private class TraversingCornerStateHandler implements StateHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            // Check to see if we are still in the same corner
            assert driver != null;
            Corner corner = driver.inCorner();
            if (corner == null) {
                System.out.println("Driver is now out of the corner.");
                // The vehicle is out of the corner.
                // Go back to default driving behaviour
                currentCorner = null;
                pilot.clearDepartureLane();
                setDrivingState(DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
            } else {
                // if in a different corner, need to get a new departure lane
                if (corner != currentCorner) {
                    currentCorner = corner;
                    pilot.clearDepartureLane();
                }
                // do nothing keep going
                pilot.takeSteeringActionForTraversing(corner, parkingStatus);
                // TODO: CPM Have we considered AccelerationProfiles yet? Should we
                // pilot.followAccelerationProfile(rparameter);
            }
            return false;
        }
    }

    /**
     * The state handler for the traversing junction state.
     */
    private class TraversingJunctionStateHandler implements StateHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            // Check to see if we are still in the same junction
            assert driver != null;
            Junction junction = driver.inJunction();
            if (junction == null) {
                System.out.println("Driver is now out of the junction.");
                // The vehicle is out of the junction.
                // Go back to default driving behaviour
                currentJunction = null;
                pilot.clearDepartureLane();
                setDrivingState(DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
            } else {
                // if in a different junction, need to get a new departure lane
                if (junction != currentJunction) {
                    currentJunction = junction;
                    pilot.clearDepartureLane();
                }
                // do nothing keep going
                pilot.takeSteeringActionForTraversing(junction, parkingStatus);
                // TODO: CPM Have we considered AccelerationProfiles yet? Should we
                // pilot.followAccelerationProfile(rparameter);
            }
            return false;
        }
    }

    /**
     * The state handler for the traversing intersection state.
     */
    private class TraversingIntersectionStateHandler implements StateHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            // Check to see if we are still in the intersection
            assert driver != null;
            SimpleIntersection intersection = driver.inIntersection();
            if (intersection == null) {
                System.out.println("Driver is now out of the intersection.");
                // The vehicle is out of the corner.
                // Go back to default driving behaviour
                pilot.clearDepartureLane();
                setDrivingState(DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
            } else {
                // do nothing keep going
                pilot.takeSteeringActionForTraversing(intersection, parkingStatus);
                // TODO: CPM Have we considered AccelerationProfiles yet? Should we
                // pilot.followAccelerationProfile(rparameter);
            }
            return false;
        }
    }

    /**
     * The state handler for the traversing parking lane state.
     */
    private class TraversingParkingLaneStateHandler implements StateHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            // First check that we are still on a parking lane
            assert(driver != null);
            if (!driver.inParkingLane() || parkingStatus == ParkingStatus.EXIT){
                System.out.println("Driver is now leaving the parking lane.");
                // Find out which state to be in next
                // Find out if we need to change state
                if (driver.inCorner() != null){
                    setDrivingState(DrivingState.TRAVERSING_CORNER);
                } else if (driver.inJunction() != null){
                    setDrivingState(DrivingState.TRAVERSING_JUNCTION);
                } else if (driver.inIntersection() != null){
                    setDrivingState(DrivingState.TRAVERSING_INTERSECTION);
                } else {
                    setDrivingState(DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
                }
            }
            if (vehicle.getTargetParkingLane() ==
                    driver.getParkingLaneCurrentlyIn()){
                System.out.println("Reached target parking lane");
                vehicle.clearTargetParkingLane();
            }
            pilot.followCurrentLane();
            pilot.simpleThrottleAction();
            pilot.dontPassParkingEndPoint(parkingStatus);
            return false;
        }
    }

    private void setDrivingState(DrivingState drivingState) {
        // log("Changing state to " + state.toString());
        if (Debug.isPrintDriverStateOfVIN(vehicle.getVIN())) {
            System.err.printf("At time %.2f, vin %d changes state to %s\n",
                    vehicle.gaugeTime(), vehicle.getVIN(), drivingState);
        }
        this.drivingState = drivingState;
        lastStateChangeTime = vehicle.gaugeTime();
    }

    public ParkingStatus getParkingStatus() {
        return parkingStatus;
    }

    private void setParkingStatus(ParkingStatus parkingStatus) {
        this.parkingStatus = parkingStatus;
    }

    /**
     * Get the amount of time, in seconds, since the state of this
     * CoordinatingDriverAgent last changed.
     *
     * @return the amount of time, in seconds, since the state of this
     *         CoordinatingDriverAgent last changed
     */
    private double timeSinceStateChange() {
        return vehicle.gaugeTime() - lastStateChangeTime;
    }

    /**
     * Whether of not the coordinator has finished its job.
     */
    @Override
    public boolean isTerminated() {
        return drivingState == DrivingState.TERMINAL_STATE;
    }
}
