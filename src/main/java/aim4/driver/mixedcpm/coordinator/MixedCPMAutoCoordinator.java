package aim4.driver.mixedcpm.coordinator;

import aim4.config.Debug;
import aim4.driver.Coordinator;
import aim4.driver.mixedcpm.MixedCPMAutoDriver;
import aim4.driver.mixedcpm.navigator.MixedCPMAutoNavigator;
import aim4.driver.mixedcpm.pilot.MixedCPMAutoPilot;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.mixedcpm.parking.AutomatedParkingRoad;
import aim4.vehicle.mixedcpm.MixedCPMBasicAutoVehicle;

import java.util.EnumMap;
import java.util.HashSet;

public class MixedCPMAutoCoordinator implements Coordinator {

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

        /** The vehicle has entered, the car park. They are
         * now making their way to a manual stall to park.*/
        PARKING,

        /** The vehicle has now parked. */
        PARKED,

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
         * The agent is parking in a parking stall. Makes sure it's in line
         * with the parking stall, and doesn't move past the end of it.
         */
        PARKING_IN_LANE,
        /**
         * The agent is parked in a stall. It doesn't move until it needs
         * to exit.
         */
        PARKED_IN_LANE,
        /**
         * The agent is exiting a parking stall (reversing out of it).
         */
        EXITING_LANE,
        /**
         * The agent has completed and Coordinator has finished its job.
         */
        TERMINAL_STATE
    }

    /** The Vehicle being coordinated by this coordinator. */
    private MixedCPMBasicAutoVehicle vehicle;

    /** The driver that this coordinator is a part of. */
    private MixedCPMAutoDriver driver;

    /** The sub-agent that controls physical manipulation of the vehicle */
    private MixedCPMAutoPilot pilot;

    /**
     * The sub-agent that decides where to go when there is a choice.
     * i.e. in junctions and intersections.
     */
    private MixedCPMAutoNavigator navigator;

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
    protected MixedCPMAutoCoordinator.DrivingState drivingState;

    /**
     * The current parking status of the agent.
     * This is part of how the two sub-agents communicate.
     */
    protected MixedCPMAutoCoordinator.ParkingStatus parkingStatus;

    /**
     * The most recent time at which the driving state was changed.
     */
    private double lastDrivingStateChangeTime = 0.0;

    /**
     * The driving state handlers
     */
    private EnumMap<MixedCPMAutoCoordinator.DrivingState, MixedCPMAutoCoordinator.StateHandler> stateHandlers;

    private HashSet<Junction> junctionsAlreadyTraversed;

    /**
     * Whether we've printed the junction to the console already
     */
    private boolean debugPrintedThisJunctionAlready = false;

    /////////////////////////////////
    // CONSTRUCTORS
    /////////////////////////////////

    /**
     * Create a basic V2V Coordinator to coordinate a Vehicle in CPM.
     *
     * @param vehicle  the Vehicle it will coordinate.
     * @param driver   the driver agent it is a part of.
     */
    public MixedCPMAutoCoordinator(MixedCPMBasicAutoVehicle vehicle,
                                     MixedCPMAutoDriver driver){
        this.vehicle = vehicle;
        this.driver = driver;
        this.navigator = new MixedCPMAutoNavigator(vehicle, driver);
        this.pilot = new MixedCPMAutoPilot(vehicle, driver, navigator);


        initStateHandlers();

        // Set the intial driving state of the coordinator
        setDrivingState(MixedCPMAutoCoordinator.DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
        // Set the initial parking status of the coordinator
        setParkingStatus(ParkingStatus.WAITING);
        junctionsAlreadyTraversed = new HashSet<>();
    }

    @Override
    public void act() {
        checkTimeToExit(); // check if the parking time has elapsed
        processI2Vinbox(); // process any messages from the Status Monitor
        callStateHandlers(); // act according to the driving state
    }

    /**
     * Check the time left until it should exit, and change parking status
     * to exiting if it is time to do so.
     */
    private void checkTimeToExit() {
        if (vehicle.getTimeUntilExit() <= 0
                && parkingStatus != MixedCPMAutoCoordinator.ParkingStatus.EXIT) {
            System.out.println("Vehicle " + vehicle.getVIN() +" parking time has elapsed: setting parking status to EXIT.");
            parkingStatus = MixedCPMAutoCoordinator.ParkingStatus.EXIT;
            drivingState = MixedCPMAutoCoordinator.DrivingState.EXITING_LANE;
        }
    }

    /**
     * Process any messages in the I2V inbox, from the IStatusMonitor.
     */
    private void processI2Vinbox() {
        AutomatedParkingRoad message = vehicle.getMessagesFromI2VInbox();
        // TODO ED MixedCPMAutoCoordinator
        if (message!= null) {
            if (this.vehicle.getTargetLane() == null&&parkingStatus == MixedCPMAutoCoordinator.ParkingStatus.WAITING) {
                // We have been granted access to the car park and know where to park
                //System.out.println("Changing status to PARKING.");
                setParkingStatus(MixedCPMAutoCoordinator.ParkingStatus.PARKING);
                vehicle.setTargetLane(message);
                vehicle.clearI2Vinbox();
                System.out.println("Vehicle " + vehicle.getVIN() + " finding " + message.getName());
                vehicle.setHasEntered();
            }
        }
    }

    /**
     * The main loop for calling the state handlers
     */
    private void callStateHandlers() {
        //here
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
    public MixedCPMAutoCoordinator.DrivingState getDrivingState() {
        return drivingState;
    }

    /////////////////////////////////
    // STATE HANDLERS
    /////////////////////////////////

    /**
     * Initialize the state handlers.
     */
    private void initStateHandlers() {
        stateHandlers = new EnumMap<MixedCPMAutoCoordinator.DrivingState,MixedCPMAutoCoordinator.StateHandler>(MixedCPMAutoCoordinator.DrivingState.class);

        stateHandlers.put(MixedCPMAutoCoordinator.DrivingState.DEFAULT_DRIVING_BEHAVIOUR,
                new MixedCPMAutoCoordinator.DefaultDrivingBehaviourStateHandler());

        stateHandlers.put(MixedCPMAutoCoordinator.DrivingState.TRAVERSING_CORNER,
                new MixedCPMAutoCoordinator.TraversingCornerStateHandler());

        stateHandlers.put(MixedCPMAutoCoordinator.DrivingState.TRAVERSING_JUNCTION,
                new MixedCPMAutoCoordinator.TraversingJunctionStateHandler());

        stateHandlers.put(MixedCPMAutoCoordinator.DrivingState.TRAVERSING_INTERSECTION,
                new MixedCPMAutoCoordinator.TraversingIntersectionStateHandler());

        stateHandlers.put(MixedCPMAutoCoordinator.DrivingState.PARKING_IN_LANE,
                new MixedCPMAutoCoordinator.ParkingInLaneStateHandler());

        stateHandlers.put(DrivingState.PARKED_IN_LANE,
                new MixedCPMAutoCoordinator.ParkedInLaneStateHandler());

        stateHandlers.put(DrivingState.EXITING_LANE,
                new MixedCPMAutoCoordinator.ExitingLaneStateHandler());

        stateHandlers.put(MixedCPMAutoCoordinator.DrivingState.TERMINAL_STATE,
                terminalStateHandler);
    }

    /**
     * The terminal state handler.
     */
    private static MixedCPMAutoCoordinator.StateHandler terminalStateHandler =
            new MixedCPMAutoCoordinator.StateHandler() {
                @Override
                public boolean perform() {
                    return false;  // do nothing, not even the pilot
                }
            };

    /**
     * The state handler for the default driving behavior state.
     */
    private class DefaultDrivingBehaviourStateHandler implements MixedCPMAutoCoordinator.StateHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            // First check if we are in a Connection or ParkingLane.
            // If so, then switch to the relevant traversing mode.
            assert driver != null;

            junctionsAlreadyTraversed.clear();
            junctionsAlreadyTraversed.add((Junction)vehicle.getLastConnection());

            if (driver.isInTargetLane()
                    && parkingStatus == MixedCPMAutoCoordinator.ParkingStatus.PARKING) {
                System.out.println("Vehicle " + vehicle.getVIN() + " parking in " + vehicle.getTargetLane().getName());
                setDrivingState(MixedCPMAutoCoordinator.DrivingState.PARKING_IN_LANE);
            } else {
                if (driver.inCorner() != null){
                    System.out.println("Vehicle " + vehicle.getVIN() + " Entering corner.");
                    currentCorner = driver.inCorner();
                    vehicle.updateEstimatedDistanceTravelled(currentCorner);
                    setDrivingState(MixedCPMAutoCoordinator.DrivingState.TRAVERSING_CORNER);
                }
                if (driver.inJunction() != null){
                    currentJunction = driver.inJunction();
                    System.out.println("Vehicle " + vehicle.getVIN() + " Entering junction with roads " + currentJunction.getRoads().toString());
                    junctionsAlreadyTraversed.add(currentJunction);
                    vehicle.updateEstimatedDistanceTravelled(currentJunction);
                    setDrivingState(MixedCPMAutoCoordinator.DrivingState.TRAVERSING_JUNCTION);
                }
                if (driver.inIntersection() != null){
                    System.out.println("Vehicle " + vehicle.getVIN() + " Entering intersection.");
                    SimpleIntersection currentIntersection = driver.inIntersection();
                    vehicle.updateEstimatedDistanceTravelled(currentIntersection);
                    setDrivingState(MixedCPMAutoCoordinator.DrivingState.TRAVERSING_INTERSECTION);
                }
                pilot.followCurrentLane();
                pilot.simpleThrottleAction();
            }
            return false;
        }
    }

    /**
     * The state handler for the traversing corner state.
     */
    private class TraversingCornerStateHandler implements MixedCPMAutoCoordinator.StateHandler {
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
                setDrivingState(MixedCPMAutoCoordinator.DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
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
    private class TraversingJunctionStateHandler implements MixedCPMAutoCoordinator.StateHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            // Check to see if we are still in the same junction
            assert driver != null;
            Junction junction = driver.inJunction();
            if (junction == null) {
                debugPrintedThisJunctionAlready = false;
                // The vehicle is out of the junction.
                // Go back to default driving behaviour if not in a manual stall
                junctionsAlreadyTraversed.clear();
                if (vehicle.inInTargetStall())
                {
                    System.out.println("Vehicle " + vehicle.getVIN() + " has exited junction and is parking in " + vehicle.getTargetLane().getName());
                    setDrivingState(DrivingState.PARKING_IN_LANE);
                } else {
                    System.out.println("Vehicle " + vehicle.getVIN() + " is now out of the junction on road " + driver.getCurrentLane().getId());
                    //System.out.println("Vehicle " + vehicle.getVIN() + " Junction (x,y) " + junction.getCentroid().toString());
                    //System.out.println("Vehicle " + vehicle.getVIN() + " Vehicle  (x,y) " + vehicle.gaugePosition().toString());
                    setDrivingState(DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
                }
                pilot.clearDepartureLane();
                currentJunction = null;
            } else {
                // if in a different junction and we're not parking, need to get
                // a new departure lane and estimate the distance travelled.
                if (!junctionsAlreadyTraversed.contains(junction)) {

                    // if we're not already in the junction with the final road
                    if (!currentJunction.getExitRoads().get(0).getName().equals("bottomRoad")) {
                        if (currentJunction != junction && parkingStatus == ParkingStatus.EXIT
                                || (parkingStatus == ParkingStatus.PARKING
                                && vehicle.getTargetLane() != null
                                && pilot.getConnectionDepartureLane() != vehicle.getTargetLane().getOnlyLane())) {
                            System.out.println("Vehicle " + vehicle.getVIN() + " in new junction with roads " + junction.getRoads().toString());
                            currentJunction = junction;
                            junctionsAlreadyTraversed.add(currentJunction);
                            vehicle.updateEstimatedDistanceTravelled(currentJunction);
                            pilot.clearDepartureLane();
                        }
                        if (!debugPrintedThisJunctionAlready) {
                            System.out.println("Vehicle " + vehicle.getVIN() + " in junction with roads " + junction.getRoads().toString());
                            debugPrintedThisJunctionAlready = true;
                        }
                        // do nothing, keep going through the junction
                        // TODO: CPM Have we considered AccelerationProfiles yet? Should we
                        // pilot.followAccelerationProfile(rparameter);
                    }
                }
                //System.out.println("Vehicle " + vehicle.getVIN() + " actual junction is " + currentJunction.getRoads().toString());
                /*if (pilot.getConnectionDepartureLane() != null) {
                    System.out.println("Vehicle " + vehicle.getVIN() + " existing departure lane " + pilot.getConnectionDepartureLane().getStartPoint().toString());
                }else{
                    System.out.println("Vehicle " + vehicle.getVIN() + " existing departure lane null");

                }*/

                pilot.takeSteeringActionForTraversing(currentJunction, parkingStatus);
                pilot.simpleThrottleAction();
            }
            return false;
        }
    }

    /**
     * The state handler for the traversing intersection state.
     */
    private class TraversingIntersectionStateHandler implements MixedCPMAutoCoordinator.StateHandler {
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
                setDrivingState(MixedCPMAutoCoordinator.DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
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
     * The state handler for the traversing manual stall state.
     */
    private class ParkingInLaneStateHandler implements MixedCPMAutoCoordinator.StateHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            // First check that we are still meant to be in a manual stall
            assert(driver != null);
            if (vehicle.getTargetLane().getOnlyLane() ==
                    driver.getCurrentLane()){
                parkingStatus = ParkingStatus.PARKING;
                //vehicle.clearTargetStall();
            }
            // park
            pilot.parkInLane(parkingStatus);
            /*if(pilot.linedUpWithStall()){
                junctionsAlreadyTraversed.clear();
                junctionsAlreadyTraversed.add(vehicle.getTargetLane().getJunction());
                System.out.println(String.format("Vehicle VIN %d parked", vehicle.getVIN()));
                parkingStatus = ParkingStatus.PARKED;
                setDrivingState(DrivingState.PARKED_IN_LANE);
            }*/
            return false;
        }
    }

    /**
     * The state handler for the parked in manual stall state.
     */
    private class ParkedInLaneStateHandler implements MixedCPMAutoCoordinator.StateHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            // First check that we are still meant to be in a manual stall
            assert(driver != null);
            // Do nothing as we're still parked.
            pilot.dontPassParkingEndPoint(parkingStatus);
            return false;
        }
    }

    /**
     * The state handler for the parked in manual stall state.
     */
    private class ExitingLaneStateHandler implements MixedCPMAutoCoordinator.StateHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            // First check that we are still meant to be in a manual stall
            assert(driver != null);
            // Do nothing as we're still parked.
            pilot.dontPassParkingEndPoint(parkingStatus);
            return false;
        }
    }



    /**
     * Set the driving state of this agent.
     * @param drivingState the new driving state for this agent.
     */
    private void setDrivingState(MixedCPMAutoCoordinator.DrivingState drivingState) {
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
    public MixedCPMAutoCoordinator.ParkingStatus getParkingStatus() {
        return parkingStatus;
    }

    public boolean hasBeenInJunctionAlready(Junction junction) {
        return junctionsAlreadyTraversed.contains(junction);
    }

    private void setParkingStatus(MixedCPMAutoCoordinator.ParkingStatus parkingStatus) {
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
        return drivingState == MixedCPMAutoCoordinator.DrivingState.TERMINAL_STATE;
    }
}
