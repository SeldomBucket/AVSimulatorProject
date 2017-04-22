package aim4.sim.simulator.merge;

import aim4.im.merge.MergeManager;
import aim4.im.merge.V2IEnabledMergeManager;
import aim4.map.merge.MergeMap;
import aim4.msg.merge.i2v.I2VMergeMessage;
import aim4.msg.merge.v2i.V2IMergeMessage;
import aim4.sim.setup.merge.enums.ProtocolType;
import aim4.vehicle.VinRegistry;
import aim4.vehicle.merge.MergeV2IAutoVehicleSimModel;
import aim4.vehicle.merge.MergeVehicleSimModel;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

/**
 * Created by Callum on 13/04/2017.
 */
public class V2IMergeSimulator extends CoreMergeSimulator {


    public V2IMergeSimulator(MergeMap map, ProtocolType protocolType) {
        super(map, protocolType);
    }

    public V2IMergeSimulator(MergeMap map,
                             ProtocolType protocolType,
                             Map<String, Double> specToExpectedTimeMergeLane,
                             Map<String, Double> specToExpectedTimeTargetLane) {
        super(map, protocolType, specToExpectedTimeMergeLane, specToExpectedTimeTargetLane);
    }

    // ACTION //
    @Override
    public synchronized CoreMergeSimStepResult step(double timeStep) {
        spawnHelper.spawnVehicles(timeStep, protocolType);
        sensorInputHelper.provideSensorInput();
        letDriversAct();
        letMergeManagersAct(timeStep);
        communication();
        moveVehicles(timeStep);
        //checkForCollisions(); TODO: Fix collision prevention so that this can be run.

        Map<Integer, MergeVehicleSimModel> completedVehicles = cleanUpCompletedVehicles();
        provideCompletedVehiclesWithResultsInfo(completedVehicles);
        recordCompletedVehicles(completedVehicles);
        incrementCurrentTime(timeStep);

        return new CoreMergeSimStepResult(completedVehicles);
    }

    private void letMergeManagersAct(double timeStep) {
        for(MergeManager mm : getMap().getMergeManagers())
            mm.act(timeStep);
    }

    // COMMUNICATION //
    private void communication() {
        deliverV2IMessages();
        deliverI2VMessages();
    }

    private void deliverV2IMessages() {
        //Loop each vehicle and deliver messages
        for(MergeVehicleSimModel vehicle : getVinToVehicles().values()){
            if(vehicle instanceof MergeV2IAutoVehicleSimModel) {
                MergeV2IAutoVehicleSimModel sender = (MergeV2IAutoVehicleSimModel) vehicle;
                Queue<V2IMergeMessage> v2iOutbox = sender.getV2IOutbox();
                while(!v2iOutbox.isEmpty()) {
                    V2IMergeMessage msg = v2iOutbox.poll();
                    V2IEnabledMergeManager receiver =
                            (V2IEnabledMergeManager) getMap().getMMRegistry().get(msg.getMMID());
                    //Calculate distance message must travel
                    double txDistance =
                            sender.getPosition().distance(
                                    receiver.getMergeConnection().getCentroid());
                    //Find out if message can make it there.
                    if(transmit(txDistance, sender.getTransmissionPower())) {
                        receiver.receive(msg);
                    }
                }
            }
        }
    }

    /**
     * Deliver the I2V messages.
     */
    private void deliverI2VMessages() {
        // Now deliver all the I2V messages
        for(MergeManager im : getMap().getMergeManagers()) {
            V2IEnabledMergeManager senderMM = (V2IEnabledMergeManager)im;
            for(Iterator<I2VMergeMessage> i2vIter = senderMM.outboxIterator();
                i2vIter.hasNext();) {
                I2VMergeMessage msg = i2vIter.next();
                MergeV2IAutoVehicleSimModel vehicle =
                        (MergeV2IAutoVehicleSimModel) VinRegistry.getVehicleFromVIN(
                                msg.getVin());
                // Calculate the distance the message must travel
                double txDistance =
                        senderMM.getMergeConnection().getCentroid().distance(
                                vehicle.getPosition());
                // Find out if the message will make it that far
                if(transmit(txDistance, senderMM.getTransmissionPower())) {
                    // Actually deliver the message
                    vehicle.receive(msg);
                }
            }
            // Done delivering the IntersectionManager's messages, so clear the
            // outbox.
            senderMM.clearOutbox();
        }
    }

    /**
     * Whether the transmission of a message is successful
     *
     * @param distance  the distance of the transmission
     * @param power     the power of the transmission
     * @return whether the transmission of a messsage is successful
     */
    private boolean transmit(double distance, double power) {
        // Simple for now
        return distance <= power;
    }
}
