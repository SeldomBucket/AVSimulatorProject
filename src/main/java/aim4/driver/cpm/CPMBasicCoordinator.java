package aim4.driver.cpm;

import aim4.config.Debug;
import aim4.driver.AutoDriver;
import aim4.driver.aim.coordinator.Coordinator;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.util.EnumMap;

/**
 * This class has a similar role to the V2ICoodinator for AIM.
 * It should handle the messages between vehicles, and with
 * the paypoint (if we have one). This includes processing messages
 * and sending messages.
 */
public class CPMBasicCoordinator implements Coordinator{

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
    public enum State {
        /**
         * The agent simply follows the current lanes until it exits the simulator.
         * the intersection
         */
        DEFAULT_DRIVING_BEHAVIOUR,
        TRAVERSING_CORNER,
        TRAVERSING_JUNCTION,
        TRAVERSING_INTERSECTION,
        // Find out what this is
        TERMINAL_STATE
    }

    /** The Vehicle being coordinated by this coordinator. */
    private CPMBasicAutoVehicle vehicle;

    /** The driver of which this coordinator is a part. */
    private AutoDriver driver;

    // Does it make sense to have this here? Should it be in Driver or Vehicle?
    /** The sub-agent that controls physical manipulation of the vehicle */
    private CPMV2VPilot pilot;

    // state

    /**
     * The current state of the agent. This is part of how the two sub-agents
     * communicate.
     */
    private State state;

    /**
     * The most recent time at which the state was changed.
     */
    private double lastStateChangeTime = 0.0;

    /**
     * The state handlers
     */
    private EnumMap<State,StateHandler> stateHandlers;

    /////////////////////////////////
    // CONSTRUCTORS
    /////////////////////////////////

    /**
     * Create an basic V2V Coordinator to coordinate a Vehicle in CPM.
     *
     * @param vehicle  the Vehicle to coordinate
     * @param driver   the driver
     */
    public CPMBasicCoordinator (CPMBasicAutoVehicle vehicle,
                                AutoDriver driver){
        this.vehicle = vehicle;
        this.driver = driver;
        this.pilot = new CPMV2VPilot(vehicle, driver);

        initStateHandlers();

        // Set the intial state of the coordinator
        setState(State.DEFAULT_DRIVING_BEHAVIOUR);
    }

    @Override
    public void act() {
        // messageHandler()
        callStateHandlers();
    }

    /**
     * The main loop for calling the state handlers
     */
    private void callStateHandlers() {
        boolean shouldContinue = true;
        while(shouldContinue) {
            if (stateHandlers.containsKey(state)) {
                shouldContinue = stateHandlers.get(state).perform();
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
    public State getState() {
        return state;
    }

    /////////////////////////////////
    // STATE HANDLERS
    /////////////////////////////////

    /**
     * Initialize the state handlers.
     */
    private void initStateHandlers() {
        stateHandlers = new EnumMap<State,StateHandler>(State.class);

        stateHandlers.put(State.DEFAULT_DRIVING_BEHAVIOUR,
                new DefaultDrivingBehaviourStateHandler());

        stateHandlers.put(State.TRAVERSING_CORNER,
                new TraversingCornerStateHandler());

        stateHandlers.put(State.TRAVERSING_JUNCTION,
                new TraversingJunctionStateHandler());

        stateHandlers.put(State.TRAVERSING_INTERSECTION,
                new TraversingIntersectionStateHandler());

        stateHandlers.put(State.TERMINAL_STATE,
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
            // First check if we are in a RoadCorner.
            // If so, then switch to traversing corner mode.
            assert driver instanceof CPMBasicV2VDriver;
            if (((CPMBasicV2VDriver) driver).inCorner() != null){
                setState(State.TRAVERSING_CORNER);
            }
            if (((CPMBasicV2VDriver) driver).inJunction() != null){
                setState(State.TRAVERSING_JUNCTION);
            }
            if (((CPMBasicV2VDriver) driver).inIntersection() != null){
                setState(State.TRAVERSING_INTERSECTION);
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
            // Check to see if we are still in the corner
            assert driver instanceof CPMBasicV2VDriver;
            Corner corner = ((CPMBasicV2VDriver) driver).inCorner();
            if (corner == null) {
                System.out.println("Driver is now out of the corner.");
                // The vehicle is out of the corner.
                // Go back to default driving behaviour
                pilot.clearDepartureLane();
                setState(State.DEFAULT_DRIVING_BEHAVIOUR);
            } else {
                // do nothing keep going
                pilot.takeSteeringActionForTraversing(corner);
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
            // Check to see if we are still in the junction
            assert driver instanceof CPMBasicV2VDriver;
            Junction junction = ((CPMBasicV2VDriver) driver).inJunction();
            if (junction == null) {
                System.out.println("Driver is now out of the junction.");
                // The vehicle is out of the junction.
                // Go back to default driving behaviour
                pilot.clearDepartureLane();
                setState(State.DEFAULT_DRIVING_BEHAVIOUR);
            } else {
                // do nothing keep going
                pilot.takeSteeringActionForTraversing(junction);
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
            assert driver instanceof CPMBasicV2VDriver;
            SimpleIntersection intersection = ((CPMBasicV2VDriver) driver).inIntersection();
            if (intersection == null) {
                System.out.println("Driver is now out of the intersection.");
                // The vehicle is out of the corner.
                // Go back to default driving behaviour
                pilot.clearDepartureLane();
                setState(State.DEFAULT_DRIVING_BEHAVIOUR);
            } else {
                // do nothing keep going
                pilot.takeSteeringActionForTraversing(intersection);
                // TODO: CPM Have we considered AccelerationProfiles yet? Should we
                // pilot.followAccelerationProfile(rparameter);
            }
            return false;
        }
    }

    private void setState(State state) {
        // log("Changing state to " + state.toString());
        if (Debug.isPrintDriverStateOfVIN(vehicle.getVIN())) {
            System.err.printf("At time %.2f, vin %d changes state to %s\n",
                    vehicle.gaugeTime(), vehicle.getVIN(), state);
        }
        this.state = state;
        lastStateChangeTime = vehicle.gaugeTime();
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
        return state == State.TERMINAL_STATE;
    }
}
