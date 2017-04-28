package aim4.driver.merge.coordinator;

import aim4.driver.merge.MergeV2IAutoDriver;
import aim4.driver.merge.pilot.MergeAutoPilot;
import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.merge.MergeMap;
import aim4.map.merge.RoadNames;
import aim4.msg.merge.i2v.I2VMergeMessage;
import aim4.msg.merge.i2v.QConfirm;
import aim4.msg.merge.i2v.QGo;
import aim4.msg.merge.i2v.QReject;
import aim4.msg.merge.v2i.QDone;
import aim4.msg.merge.v2i.QRequest;
import aim4.vehicle.merge.MergeV2IAutoVehicleDriverModel;

import java.util.EnumMap;
import java.util.List;

/**
 * Created by Callum on 19/04/2017.
 */
public class MergeQueueCoordinator extends MergeCoordinator {
    // CONSTANT
    private static final double REQUEST_TIME_GAP = 0.1;

    // NESTED CLASSES //
    public enum State {
        PLANNING,
        DEFAULT_DRIVING_BEHAVIOUR,
        PREPARING_REQUEST,
        AWAITING_CONFIRM,
        AWAITING_GO,
        MOVING_TO_MERGE,
        TRAVERSING,
        CLEARING,
        TERMINAL_STATE
    }

    // PRIVATE FIELDS //
    // STATE
    /** The current state of the agent */
    private State state;

    // COMMUNICATION
    /**
     * The next time at which the vehicle is allowed to send out request messages
     */
    private double nextAllowedSendingRequestTime;
    /** The state handlers */
    private EnumMap<State, StateHandler> stateHandlers;

    // CONSTRUCTORS //
    public MergeQueueCoordinator(MergeV2IAutoVehicleDriverModel vehicle,
                                 MergeV2IAutoDriver driver,
                                 MergeMap map) {
        this.vehicle = vehicle;
        this.driver = driver;
        this.map = map;
        this.pilot = new MergeAutoPilot(vehicle, driver);

        initStateHandlers();
        //This coordinator shoudl be used when there is a merge manager available.
        assert driver.nextMergeManager() != null;
        //We should be allowed to transmit now
        nextAllowedSendingRequestTime = vehicle.gaugeTime();
        //Set the initial state
        setState(State.PLANNING);
    }

    // ACTION //
    @Override
    public void act() {
        messageHandler();
        callStateHandlers();
    }

    /**
     * Process the message in the inbox.
     */
    private void messageHandler() {
        // the incoming message queue one by one
        List<I2VMergeMessage> msgs = getVehicle().pollAllMessagesFromI2VInbox();
        for(I2VMergeMessage msg : msgs) {
            // interpret the message (and potentially change the state)
            processMessages(msg);
        }
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
                throw new RuntimeException(String.format(
                        "Unknown state %s.",
                        state.toString())
                );
            }
        }
    }

    // STATE CONTROLS //
    private void setState(State state){
        this.state = state;
    }

    @Override
    public String getStateString() {
        return state.toString();
    }

    @Override
    public boolean isTerminated() {
        return state == State.TERMINAL_STATE;
    }

    // STATE HANDLERS //
    private void initStateHandlers() {
        stateHandlers = new EnumMap<State, StateHandler>(State.class);

        stateHandlers.put(State.PLANNING,
                new PlanningStateHandler());

        stateHandlers.put(State.DEFAULT_DRIVING_BEHAVIOUR,
                new DefaultDrivingBehaviourStateHandler());

        stateHandlers.put(State.PREPARING_REQUEST,
                new PreparingRequestStateHandler());

        stateHandlers.put(State.AWAITING_CONFIRM,
                new AwaitingConfirmStateHandler());

        stateHandlers.put(State.AWAITING_GO,
                new AwaitingGoStateHandler());

        stateHandlers.put(State.MOVING_TO_MERGE,
                new MovingToMerge());

        stateHandlers.put(State.TRAVERSING,
                new TraversingStateHandler());

        stateHandlers.put(State.CLEARING,
                new ClearingStateHandler());

        stateHandlers.put(State.TERMINAL_STATE,
                terminalStateHandler);
    }

    private class PlanningStateHandler implements StateHandler {
        /** {@inheritDoc} */
        @Override
        public boolean perform() {
            if(vehicle.gaugeTime() >= nextAllowedSendingRequestTime) {
                setState(State.PREPARING_REQUEST);
                return true;
            } else {
                setState(State.DEFAULT_DRIVING_BEHAVIOUR);
                return true;
            }
        }
    }

    private class DefaultDrivingBehaviourStateHandler implements StateHandler {
        /** {@inheritDoc} */
        @Override
        public boolean perform() {
            pilot.followCurrentLane();
            pilot.simpleThrottleActionDontEnterMerge();
            setState(State.PLANNING);
            return false;
        }
    }

    private class PreparingRequestStateHandler implements StateHandler {
        /** {@inheritDoc} */
        @Override
        public boolean perform() {
            //Update Next allowed request time
            nextAllowedSendingRequestTime = nextAllowedSendingRequestTime + REQUEST_TIME_GAP;
            //Get distance to merge
            double distanceToMerge = getDriver().distanceToNextMerge();
            //Create request
            QRequest request = new QRequest(
                    vehicle.getVIN(),
                    getVehicle().getPrecedingVehicleVIN(),
                    getDriver().getCurrentMM().getId(),
                    distanceToMerge
            );
            //Send request
            sendRequestMessage(request);
            //Send state
            setState(State.AWAITING_CONFIRM);
            return true;
        }
    }

    private class AwaitingConfirmStateHandler implements StateHandler {
        /** {@inheritDoc} */
        @Override
        public boolean perform() {
            pilot.followCurrentLane();
            pilot.simpleThrottleActionDontEnterMerge();
            return false;
        }
    }

    private class AwaitingGoStateHandler implements StateHandler {
        /** {@inheritDoc} */
        @Override
        public boolean perform() {
            pilot.followCurrentLane();
            pilot.simpleThrottleActionDontEnterMerge();
            return false;
        }
    }

    private class MovingToMerge implements StateHandler {
        /** {@inheritDoc} */
        @Override
        public boolean perform() {
            if(getDriver().inCurrentMerge()) {
                setState(State.TRAVERSING);
                return true;
            } else {
                pilot.followCurrentLane();
                pilot.simpleThrottleAction();
                return false;
            }
        }
    }

    private class TraversingStateHandler implements StateHandler {
        /** {@inheritDoc} */
        @Override
        public boolean perform() {
            if(getDriver().inCurrentMerge()) {
                pilot.steerThroughMergeConnection(getDriver().inMerge());
                pilot.simpleThrottleAction();
                return false;
            } else {
                // The vehicle is out of the merge.
                Lane target = null;
                for (Road r : map.getRoads())
                    if (r.getName().equals(RoadNames.TARGET_ROAD.toString()))
                        target = r.getOnlyLane();
                assert target != null;
                driver.setCurrentLane(target);
                pilot.followCurrentLane();
                System.err.printf("Sent done message at time %.2f\n",
                        vehicle.gaugeTime());
                // And now get ready to clear
                setState(State.CLEARING);
                return true;  // can check clearance immediately
            }
        }
    }

    private class ClearingStateHandler implements StateHandler {
        /** {@inheritDoc} */
        @Override
        public boolean perform() {
            sendDoneMessage(new QDone(
                    getVehicle().getVIN(),
                    getDriver().getCurrentMM().getId())
            );
            setState(State.TERMINAL_STATE);
            pilot.followCurrentLane();
            pilot.simpleThrottleActionDontEnterMerge();
            return false;
        }
    }

    // COMMUNICATIONS //
    /**
     * Called every time {@link #act()} is called, to process any waiting
     * messages.
     */
    private void processMessages(I2VMergeMessage msg) {
        switch(msg.getMessageType()) {
            case Q_CONFIRM:
                processConfirmMessage((QConfirm)msg);
                break;
            case Q_REJECT:
                processRejectMessage((QReject)msg);
                break;
            case Q_GO:
                processGoMessage((QGo)msg);
                break;
            default:
                throw new UnsupportedOperationException(String.format(
                        "Unsupported message type %s",
                        msg.getMessageType().toString())
                );
        }
    }

    private void processConfirmMessage(QConfirm msg) {
        switch(state) {
            case AWAITING_CONFIRM:
                setState(State.AWAITING_GO);
                break;
            default:
                System.err.printf("vin %d receives a confirm message when it is not " +
                                "at the AWAITING_CONFIRM state\n",
                        vehicle.getVIN());
        }
    }

    private void processRejectMessage(QReject msg) {
        switch(state) {
            case AWAITING_CONFIRM:
                processRejectMessageByReason(msg.getReason());
                break;
            default:
                System.err.printf("vin %d receives a reject message when it is not " +
                                "at the AWAITING_CONFIRM state\n",
                        vehicle.getVIN());

        }
    }

    private void processRejectMessageByReason(QReject.Reason reason) {
        switch (reason) {
            case TOO_FAR:
                setState(State.PLANNING);
                break;
            case ALREADY_IN_QUEUE:
                setState(State.AWAITING_GO);
                System.err.printf("vin %d received an ALREADY_IN_QUEUE reject message\n",
                        vehicle.getVIN());
                break;
            case VEHICLE_IN_FRONT_NOT_IN_QUEUE:
                setState(State.PLANNING);
                System.err.printf("vin %d received a VEHICLE_IN_FRONT_NOT_IN_QUEUE reject message\n",
                        vehicle.getVIN());
                break;
            default:
                throw new UnsupportedOperationException(String.format(
                        "Reject reason %s is unsupported",
                        reason.toString())
                );
        }
    }

    private void processGoMessage(QGo msg) {
        switch(state) {
            case AWAITING_GO:
                setState(State.MOVING_TO_MERGE);
                break;
            default:
                System.err.printf("vin %d receives a go message when it is not " +
                                "at the AWAITING_GO state\n",
                        vehicle.getVIN());
        }
    }

    private void sendRequestMessage(QRequest request) {
        getVehicle().send(request);
    }

    private void sendDoneMessage(QDone done) {
        getVehicle().send(done);
    }

    // ACCESSORS //
    private MergeV2IAutoVehicleDriverModel getVehicle() {
        assert vehicle instanceof MergeV2IAutoVehicleDriverModel;
        return (MergeV2IAutoVehicleDriverModel) vehicle;
    }

    private MergeV2IAutoDriver getDriver() {
        assert driver instanceof MergeV2IAutoDriver;
        return (MergeV2IAutoDriver) driver;
    }


}
