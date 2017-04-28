package aim4.im.merge;

import aim4.msg.merge.i2v.I2VMergeMessage;
import aim4.msg.merge.v2i.V2IMergeMessage;

import java.util.Iterator;

/**
 * Created by Callum on 17/04/2017.
 */
public interface V2IEnabledMergeManager extends MergeManager {
    /**
     * Get the MergeManagers's transmission power.
     *
     * @return the MergeManagers's transmission power, in meters
     */
    public double getTransmissionPower();
    /**
     * Adds a message to the incoming queue of messages delivered to this
     * IntersectionManager.
     *
     * @param msg the message to be received
     */
    void receive(V2IMergeMessage msg);
    /**
     * Clear out the inbox.
     */
    void clearInbox();
    /**
     * Get an iterator for the messages waiting to be read.
     *
     * @return an iterator for the messages waiting to be read.
     */
    Iterator<V2IMergeMessage> inboxIterator();
    /**
     * Clear out the outbox.
     */
    void clearOutbox();
    /**
     * Get an iterator for the messages waiting to be delivered from this
     * IntersectionManager.
     *
     * @return an iterator for the messages waiting to be delivered from
     *         this IntersectionManager
     */
    Iterator<I2VMergeMessage> outboxIterator();
}
