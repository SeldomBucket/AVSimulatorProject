package aim4.im.merge;

import aim4.im.merge.queue.QueueManager;
import aim4.im.merge.queue.V2IQueueMergeManagerCallback;
import aim4.map.connections.MergeConnection;
import aim4.msg.merge.i2v.I2VMergeMessage;
import aim4.msg.merge.v2i.QDone;
import aim4.msg.merge.v2i.QRequest;
import aim4.msg.merge.v2i.V2IMergeMessage;
import aim4.util.Registry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Callum on 19/04/2017.
 */
public class V2IQueueMergeManager extends BasicMergeManager implements V2IEnabledMergeManager, V2IQueueMergeManagerCallback {
    // CONSTANTS //
    /**
     * The default distance the MergeManager can transmit messages.
     * {@value} meters.
     */
    private static final double DEFAULT_TRANSMISSION_POWER = Double.MAX_VALUE; // meters

    // PRIVATE FIELDS //
    /** The queueManager handling the order vehicles move through the merge */
    private QueueManager queueManager;
    /** A List of messages received from Vehicles waiting to be processed. */
    private List<V2IMergeMessage> inbox = new ArrayList<V2IMergeMessage>();
    /** A List of messages waiting to be sent to Vehicles. */
    private List<I2VMergeMessage> outbox = new ArrayList<I2VMergeMessage>();

    /**
     * Creates a MergeManager
     *
     * @param merge       The merge connection this manager will manage vehicles through.
     * @param currentTime The current simulation time.
     * @param mmRegistry  a Merge Manager registry
     */
    public V2IQueueMergeManager(MergeConnection merge, double currentTime, Registry<MergeManager> mmRegistry) {
        super(merge, currentTime, mmRegistry);
        this.queueManager = new QueueManager(this);
    }

    // ACTION //
    /**
     * Give the V2IQueueMergeManager a chance to respond to messages from vehicles, change
     * policies, and so forth.
     *
     * @param timeStep  the size of the time step to simulate, in seconds
     */
    @Override
    public void act(double timeStep) {
        // First, process all the incoming messages waiting for us
        for(Iterator<V2IMergeMessage> iter = inboxIterator(); iter.hasNext();) {
            V2IMergeMessage msg = iter.next();
            processV2IMergeMessage(msg);
        }
        // Done processing, clear the inbox.
        clearInbox();
        // Let queue manager act.
        queueManager.act();
        // Advance current time.
        super.act(timeStep);
    }

    // COMMUNICATIONS //
    /**
     * Process a V2I message
     *
     * @param msg  the V2I message
     */
    private void processV2IMergeMessage(V2IMergeMessage msg) {
        switch(msg.getMessageType()) {
            case Q_REQUEST: queueManager.processRequest((QRequest) msg); break;
            case Q_DONE: queueManager.processDone((QDone) msg); break;
            default: throw new UnsupportedOperationException(String.format(
                    "V2IQueueMergeManager does not support %s messages",
                    msg.getMessageType().toString()));
        }
    }

    @Override
    public double getTransmissionPower() {
        return DEFAULT_TRANSMISSION_POWER;
    }

    @Override
    public void receive(V2IMergeMessage msg) {
        inbox.add(msg);
    }

    @Override
    public void clearInbox() {
        inbox.clear();
    }

    @Override
    public Iterator<V2IMergeMessage> inboxIterator() {
        return inbox.iterator();
    }

    @Override
    public void clearOutbox() {
        outbox.clear();
    }

    @Override
    public Iterator<I2VMergeMessage> outboxIterator() {
        return outbox.iterator();
    }

    @Override
    public void sendI2VMessage(I2VMergeMessage msg) {
        outbox.add(msg);
    }
}
