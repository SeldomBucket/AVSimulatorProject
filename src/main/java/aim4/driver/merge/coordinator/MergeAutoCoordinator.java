package aim4.driver.merge.coordinator;

import aim4.config.SimConfig;
import aim4.driver.merge.MergeAutoDriver;
import aim4.driver.merge.pilot.MergeAutoPilot;
import aim4.im.merge.MergeManager;
import aim4.map.Road;
import aim4.map.connections.MergeConnection;
import aim4.map.lane.Lane;
import aim4.map.merge.MergeMap;
import aim4.map.merge.RoadNames;
import aim4.vehicle.AccelSchedule;
import aim4.vehicle.VehicleUtil;
import aim4.vehicle.merge.MergeAutoVehicleDriverModel;

import java.util.EnumMap;

/**
 * Created by Callum on 25/03/2017.
 */
public class MergeAutoCoordinator extends MergeCoordinator {
    // STATES //
    public enum State {
        /**
         * Agent is planning what to do next
         */
        PLANNING,
        /**
         * The agent follows the current lane and does not enter the intersection
         */
        DEFAULT_DRIVING_BEHAVIOUR,
        /**
         * The agent continues driving towards the exit
         */
        END_OF_MERGE,
        /**
         * The agent is crossing the merge.
         */
        TRAVERSING_MERGE,
        /**
         * Signals the end of the simulation for the vehicle.
         */
        TERMINAL_STATE
    }

    //PRIVATE FIELDS//
    //VEHICLE AND DRIVER
    //STATE
    /** The current state of the agent. */
    private State state;
    /** The most recent time at which the state was changed */
    private double lastStateChangeTime = 0.0;
    /** The state handlers */
    private EnumMap<State, StateHandler> stateHandlers;

    //CONSTRUCTOR//
    public MergeAutoCoordinator(MergeAutoVehicleDriverModel vehicle, MergeAutoDriver driver, MergeMap map) {
        this.vehicle = vehicle;
        this.driver = driver;
        this.map = map;
        this.pilot = new MergeAutoPilot(vehicle, driver);

        initStateHandlers();

        setState(State.PLANNING);
    }

    //STATE CONTROLLERS//
    public State getState() {
        return state;
    }

    public String getStateString() {
        return getState().toString();
    }

    private void setState(State state) {
        this.state = state;
        lastStateChangeTime = vehicle.gaugeTime();
    }

    private void initStateHandlers() {
        stateHandlers = new EnumMap<State, StateHandler>(State.class);

        stateHandlers.put(State.PLANNING, new PlanningStateHandler());
        stateHandlers.put(State.DEFAULT_DRIVING_BEHAVIOUR, new DefaultDrivingBehaviourHandler());
        stateHandlers.put(State.TRAVERSING_MERGE, new TraversingMergeStateHandler());
        stateHandlers.put(State.END_OF_MERGE, new EndOfMergeStateHandler());
        stateHandlers.put(State.TERMINAL_STATE, terminalStateHandler);
    }

    private void callStateHandlers() {
        boolean shouldContinue = true;
        while (shouldContinue) {
            if (stateHandlers.containsKey(state)) {
                shouldContinue = stateHandlers.get(state).perform();
            } else {
                throw new RuntimeException("Unknown state.");
            }
        }
    }

    //STATE HANDLERS//
    private class PlanningStateHandler implements StateHandler {
        @Override
        public boolean perform() {
            if(vehicle.gaugeVelocity() < VehicleUtil.MIN_MAX_TURN_VELOCITY)
                if(vehicle.getAccelSchedule() != null)
                    vehicle.removeAccelSchedule();
            else
                if(vehicle.getAccelSchedule() == null)
                    vehicle.setAccelSchedule(calculateAccelProfileToArriveAtMergeAtSafeSpeed());
            if(driver.inMerge() != null) {
                setState(State.TRAVERSING_MERGE);
            } else {
                setState(State.DEFAULT_DRIVING_BEHAVIOUR);
            }
            return true;
        }
    }

    private class DefaultDrivingBehaviourHandler implements StateHandler {
        @Override
        public boolean perform() {
            pilot.followCurrentLane();
            if(vehicle.getAccelSchedule() == null) //Motion handled by accel schedule if it exists.
                pilot.simpleThrottleAction();
            setState(State.PLANNING);
            return false;
        }
    }

    private class EndOfMergeStateHandler implements StateHandler {
        @Override
        public boolean perform() {
            pilot.followCurrentLane();
            pilot.simpleThrottleAction();
            return false;
        }
    }

    private class TraversingMergeStateHandler implements StateHandler {
        @Override
        public boolean perform() {
            MergeConnection mergeConnection = driver.inMerge();
            if(mergeConnection == null){
                //Vehicle cleared connection. Return to normal driving
                setState(State.END_OF_MERGE);
                Lane target = null;
                for(Road r : map.getRoads())
                    if(r.getName().equals(RoadNames.TARGET_ROAD.toString()))
                        target = r.getOnlyLane();
                assert target != null;
                driver.setCurrentLane(target);
                pilot.followCurrentLane();
            } else {
                pilot.steerThroughMergeConnection(mergeConnection);
            }
            pilot.simpleThrottleAction();
            return false;
        }
    }

    //ACTION//
    @Override
    public void act() {
        callStateHandlers();
    }

    //GETTERS
    @Override
    public boolean isTerminated() {
        return state == State.TERMINAL_STATE;
    }

    // ACCEL //
    private AccelSchedule calculateAccelProfileToArriveAtMergeAtSafeSpeed() {
        MergeManager mergeManager = driver.getCurrentLane().getLaneMM().firstMergeManager();
        Lane entryLane = driver.getCurrentLane();
        Lane exitLane = mergeManager.getMergeConnection().getExitLanes().get(0);

        double safeTurnSpeed = VehicleUtil.maxTurnVelocity(vehicle.getSpec(), entryLane, exitLane, mergeManager, map);
        double distanceToMerge = driver.distanceToNextMerge();
        double maxDeceleration = vehicle.getSpec().getMaxDeceleration();
        double maxAcceleration = vehicle.getSpec().getMaxAcceleration();
        double maxVelocity = Math.min(vehicle.getSpec().getMaxVelocity(), driver.getCurrentLane().getSpeedLimit());
        double startTime = vehicle.gaugeTime();

        AccelSchedule accelSchedule = new AccelSchedule();
        //Accelerate until no longer safe
        double timeToAccelUntil = startTime;
        double currentVelocity = vehicle.gaugeVelocity();
        double currentDistanceToMerge = distanceToMerge;
        if(currentVelocity < maxVelocity) {
            accelSchedule.add(vehicle.gaugeTime(), maxAcceleration);
            while (canSlowDownSafely(currentVelocity, safeTurnSpeed, maxDeceleration, currentDistanceToMerge) &&
                    currentVelocity < maxVelocity) {
                timeToAccelUntil += SimConfig.TIME_STEP;
                currentVelocity = (SimConfig.TIME_STEP * maxAcceleration) + currentVelocity;
                currentDistanceToMerge = currentDistanceToMerge - SimConfig.TIME_STEP * currentVelocity;
            }
            accelSchedule.add(Math.min(timeToAccelUntil, startTime), 0);
        } else {
            accelSchedule.add(vehicle.gaugeTime(), 0);
        }

        //Decelerate if required
        if(currentVelocity > safeTurnSpeed) {
            double distanceForDecel = VehicleUtil.distanceToChangeBetween(currentVelocity, safeTurnSpeed, maxDeceleration);
            double distanceFromDecelPoint = currentDistanceToMerge - distanceForDecel;
            double timeUntilDecel = distanceFromDecelPoint / currentVelocity;
            double timeToHitMerge = VehicleUtil.timeToChangeBetween(currentVelocity, safeTurnSpeed, maxDeceleration);
            if(timeUntilDecel != 0) {
                accelSchedule.add(timeToAccelUntil + timeUntilDecel, maxDeceleration);
            }
            accelSchedule.add(timeToAccelUntil + timeUntilDecel + timeToHitMerge, 0);
        }
        return accelSchedule;
    }

    private boolean canSlowDownSafely(double currentVelocity, double targetVelocity, double maxDeceleration, double distance) {
        double distanceTravelledWhenSlowing = VehicleUtil.distanceToChangeBetween(currentVelocity, targetVelocity, maxDeceleration);
        if(distance > distanceTravelledWhenSlowing)
            return true;
        else
            return false;
    }
}
