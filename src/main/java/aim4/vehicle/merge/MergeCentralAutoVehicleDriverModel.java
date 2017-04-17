package aim4.vehicle.merge;

import aim4.msg.merge.i2v.I2VMergeMessage;
import aim4.msg.merge.v2i.V2IMergeMessage;

import java.util.List;
import java.util.Queue;

/**
 * Created by Callum on 13/04/2017.
 */
public interface MergeCentralAutoVehicleDriverModel extends MergeAutoVehicleDriverModel {
    // communications systems (V2I)

    /**
     * Get the list of all messages currently in the queue of I2V messages
     * waiting to be read by this Vehicle.
     *
     * @return the list of all messages currently in the queue of I2V messages.
     */
    List<I2VMergeMessage> pollAllMessagesFromI2VInbox();

    /**
     * Adds a message to the outgoing queue of messages to be delivered to an
     * IntersectionManager.
     *
     * @param msg the message to send to an IntersectionManager
     */
    void send(V2IMergeMessage msg);

    /**
     * Adds a message to the incoming queue of messages received from
     * IntersectionManagers.
     *
     * @param msg the message to send to another Vehicle
     */
    void receive(I2VMergeMessage msg);

    /**
     * Get the queue of V2I messages waiting to be delivered from this
     * Vehicle.
     *
     * @return the queue of V2I messages to be delivered from this Vehicle
     */
    Queue<V2IMergeMessage> getV2IOutbox();

    /**
     * Get the Vehicle's transmission power.
     *
     * @return the Vehicle's transmission power, in meters
     */
    double getTransmissionPower();
}
