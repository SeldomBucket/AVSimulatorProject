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
 * The Coordinator is responsible for handling the messages
 * between vehicles (V2VCommunication), and with the
 * StatusMonitor (I2VCommunication.
 * The two agents (Coordinator and Pilot) communicate by setting
 * the DrivingState and ParkingStatus in this class.
 */
public class CPMCoordinator implements Coordinator {

    /**
     * The different parking statuses that an agent can have.
     * RELOCATING is also used as a message between vehicles.
     */
    public enum ParkingStatus {
        /** The vehicle has been spawned and is waiting to enter the car park.
         * If there is enough room they will be granted access by receiving
         * the parking lane they should park in (the one with the most room).
         * If there is not enough space, an exception is thrown, as vehicles
         * are only spawned if there is enough room to cater for them.
         * */
        WAITING,

        /** The vehicle has entered, or re-entered, the car park. They are
         * now making their way to a parking lane to park.*/
        PARKING,

        /** The vehicle is relocating - they leave the parking lane and reenter
         * the car park, allowing a vehicle it was blocking to exit. If a vehicle
         * is blocking it, then it will send it a message telling it to RELOCATE.
         * On reentry,the vehicle is given a new parking lane and go back to PARKING.
         * */
        RELOCATING,

        /**
         * The vehicle is being retrieved and should exit the car park.
         * If it is blocked by the vehicle in front, it will send it a
         * message to RELOCATE.
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
         * The agent simply follows the current lanes, ensuring not to hit
         * the vehicle in front of it.
         */
        DEFAULT_DRIVING_BEHAVIOUR,
        /**
         * The agent is traversing around a corner.
         */
        TRAVERSING_CORNER,
        /**
         * The agent is traversing through a junction.
         */
        TRAVERSING_JUNCTION,
        /**
         * The agent is traversing through an intersection.
         */
        TRAVERSING_INTERSECTION,
        /**
         * The agent is traversing a parking lane. More or less the same
         * behaviour as DEFAULT_DRIVING_BEHAVIOUR, but it ensures that
         * it doesn't pass the parking end point.
         */
        TRAVERSING_PARKING_LANE,
        /**
         * The agent has completed and Coordinator has finished its job.
         */
        TERMINAL_STATE
    }

    /** The Vehicle being coordinated by this coordinator. */
    private CPMBasicAutoVehicle vehicle;

    /** The driver that this coordinator is a part of. */
    private CPMV2VDriver driver;

    /** The sub-agent that controls physical manipulation of the vehicle */
    private CPMPilot pilot;

    /**
     * The sub-agent that decides where to go when there is a choice.
     * i.e. in junctions and intersections.
     */
    private CPMNavigator navigator;

    /**
     * The corner that the agent is currently in, if any.
     * Used to determine if it has entered a new corner.
     * */
    private Corner currentCorner;

    /**
     * The junction that the agent is currently in, if any.
     * Used to determine if it has entered a new junction.
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
     * The most recent time at which the driving state was changed.
     */
    private double lastDrivingStateChangeTime = 0.0;

    /**
     * The driving state handlers
     */
    private EnumMap<DrivingState,StateHandler> stateHandlers;

    /////////////////////////////////
    // CONSTRUCTORS
    /////////////////////////////////

    /**
     * Create a basic V2V Coordinator to coordinate a Vehicle in CPM.
     *
     * @param vehicle  the Vehicle it will coordinate.
     * @param driver   the driver agent it is a part of.
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
        checkTimeToExit(); // check if the parking time has elapsed
        processI2Vinbox(); // process any messages from the Status Monitor
        processV2Vinbox(); // process any messages from other vehicles
        callStateHandlers(); // act according to the driving state
    }

    /**
     * Check the time left until it should exit, and change parking status
     * to exiting if it is time to do so.
     */
    private void checkTimeToExit() {
        if (vehicle.getTimeToExit() <= 0
                && parkingStatus != ParkingStatus.EXIT) {
            System.out.println("Parking time has elapsed, " +
                               "setting parking status to EXIT.");
            parkingStatus = ParkingStatus.EXIT;
            drivingState = DrivingState.DEFAULT_DRIVING_BEHAVIOUR;
            // Tell the vehicle in front to relocate, if there is one
            passMessageToVehicleInFront(ParkingStatus.RELOCATING);
        }
    }

    /**
     * Find the vehicle parked in front and send it a message to relocate
     * so this agent can leave the parking lane.
     * @param status The driving status that the vehicle in front should change to.
     */
    private void passMessageToVehicleInFront(ParkingStatus status){
        // We only want to send the message if the vehicle is actually parked
        // I.e. is stationary, no velocity, and if we haven't already sent it
        // a message
        System.out.println("Finding vehicle in front");
        CPMBasicAutoVehicle vehicleInFront = vehicle.getVehicleInFront();
        if(vehicleInFront != null
            && vehicleInFront.getVelocity() == 0.0
                && vehicleInFront.getMessagesFromV2VInbox() == null) {
            System.out.println("Sending message " +  status.toString() + " to vehicle in front");
            vehicleInFront.sendMessageToV2VInbox(status);
        }
    }

    /**
     * Process any messages in the I2V inbox, from the StatusMonitor.
     */
    private void processI2Vinbox() {
        ParkingLane I2Vinbox = vehicle.getMessagesFromI2VInbox();
        if ((I2Vinbox != null && parkingStatus == ParkingStatus.WAITING) ||
                (I2Vinbox != null && parkingStatus == ParkingStatus.RELOCATING) ) {
            // We have been granted access to the car park and know where to park
            System.out.println("Changing status to PARKING.");
            setParkingStatus(ParkingStatus.PARKING);
            vehicle.setTargetParkingLane(I2Vinbox);
            vehicle.clearI2Vinbox();
            System.out.println("Finding space on " + I2Vinbox.getRoadName());
            if (!vehicle.hasEnteredCarPark()) {
                vehicle.setHasEntered();
            } else {
                vehicle.increaseNumberOfReEntries();
            }
        }
    }

    /**
     * Process any messages in the V2V inbox, from the vehicle behind us.
     */
    private void processV2Vinbox() {
        ParkingStatus V2Vinbox = vehicle.getMessagesFromV2VInbox();
        if ((V2Vinbox == ParkingStatus.RELOCATING && parkingStatus == ParkingStatus.PARKING)) {
            // The vehicle behind us needs to exit, so change our parking status
            System.out.println("Changing status to " + V2Vinbox.toString());
            setParkingStatus(V2Vinbox);
            setDrivingState(DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
            // If there is a vehicle in front, we need to send them the same message
            passMessageToVehicleInFront(V2Vinbox);
            vehicle.clearV2Vinbox();
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
     * Get the current driving state of this driver agent.
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
                System.out.println("Entering corner.");
                currentCorner = driver.inCorner();
                vehicle.updateEstimatedDistanceTravelled(currentCorner);
                setDrivingState(DrivingState.TRAVERSING_CORNER);
            }
            if (driver.inJunction() != null){
                System.out.println("Entering junction.");
                currentJunction = driver.inJunction();
                vehicle.updateEstimatedDistanceTravelled(currentJunction);
                setDrivingState(DrivingState.TRAVERSING_JUNCTION);
            }
            if (driver.inIntersection() != null){
                System.out.println("Entering intersection.");
                SimpleIntersection currentIntersection = driver.inIntersection();
                vehicle.updateEstimatedDistanceTravelled(currentIntersection);
                setDrivingState(DrivingState.TRAVERSING_INTERSECTION);
            }
            // If on EXIT or RELOCATING, we want default driving behaviour
            // so vehicle will drive past the parking end point
            if (driver.getCurrentLane() instanceof ParkingLane
                    && parkingStatus == ParkingStatus.PARKING) {
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
                // and estimate the distance travelled.
                if (corner != currentCorner) {
                    currentCorner = corner;
                    vehicle.updateEstimatedDistanceTravelled(currentCorner);
                    pilot.clearDepartureLane();
                }
                // do nothing, keep going around the corner
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
                // and estimate the distance travelled.
                if (junction != currentJunction) {
                    currentJunction = junction;
                    vehicle.updateEstimatedDistanceTravelled(currentJunction);
                    pilot.clearDepartureLane();
                }
                // do nothing, keep going through the junction
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
                // The vehicle is out of the intersection.
                // Go back to default driving behaviour
                pilot.clearDepartureLane();
                setDrivingState(DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
            } else {
                // do nothing keep going through the intersection
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
            // keep driving on the parking lane
            pilot.followCurrentLane();
            pilot.simpleThrottleAction();
            pilot.dontPassParkingEndPoint(parkingStatus);
            return false;
        }
    }

    /**
     * Set the driving state of this agent.
     * @param drivingState the new driving state for this agent.
     */
    private void setDrivingState(DrivingState drivingState) {
        if (Debug.isPrintDriverStateOfVIN(vehicle.getVIN())) {
            System.err.printf("At time %.2f, vin %d changes state to %s\n",
                    vehicle.gaugeTime(), vehicle.getVIN(), drivingState);
        }
        this.drivingState = drivingState;
        lastDrivingStateChangeTime = vehicle.gaugeTime();
    }

    /**
     * Get the current parking status of this agent.
     * @return the current parking status of this agent.
     */
    public ParkingStatus getParkingStatus() {
        return parkingStatus;
    }

    private void setParkingStatus(ParkingStatus parkingStatus) {
        this.parkingStatus = parkingStatus;
    }

    /**
     * Get the amount of time, in seconds, since the driving state of this
     * agent last changed.
     *
     * @return the amount of time, in seconds, since the state of this
     *         agent last changed
     */
    private double timeSinceStateChange() {
        return vehicle.gaugeTime() - lastDrivingStateChangeTime;
    }

    /**
     * Whether of not the coordinator has finished its job.
     */
    @Override
    public boolean isTerminated() {
        return drivingState == DrivingState.TERMINAL_STATE;
    }
}
