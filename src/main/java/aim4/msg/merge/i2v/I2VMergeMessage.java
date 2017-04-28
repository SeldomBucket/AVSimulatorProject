package aim4.msg.merge.i2v;

/**
 * Created by Callum on 13/04/2017.
 */
public class I2VMergeMessage {
    // NESTED CLASSES //
    /**
     * The different types of Merge Manager to
     * Vehicle messages.
     */
    public enum Type {
        /** Message confirming a reservation Request. */
        CONFIRM,
        /** Message rejecting a Request. */
        REJECT,
        /** Message granting a request to enter the admission control zone. */
        ACZ_CONFIRM,
        /** Message rejecting a request to enter the admission control zone. */
        ACZ_REJECT,
        /** Message confirming that a vehicle has a place on the queue */
        Q_CONFIRM,
        /** Message alerting a vehicle to the fact that it does not have a place on the queue */
        Q_REJECT,
        /** Message telling a vehicle to go through the merge */
        Q_GO
    };

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////
    /**
     * The ID number of the Vehicle to which this message is being sent.
     */
    private int vin;
    /**
     * The ID number of the Merge Manager from which this message
     * is being sent.
     */
    private int mmID;
    /**
     * The type of this message.
     */
    protected Type messageType;

    /////////////////////////////////
    // CLASS CONSTRUCTORS
    /////////////////////////////////

    /**
     * Class constructor to be called by subclasses to set the source
     *
     * @param mmID  the ID number of the MergeManager sending this message
     * @param vin   the ID number of the Vehicle to which this message is being
     *              sent
     */
    public I2VMergeMessage(int mmID, int vin) {
        this.mmID = mmID;
        this.vin = vin;
    }

    /**
     * Create a new copy of the message.
     *
     * @param msg  the message
     */
    public I2VMergeMessage(I2VMergeMessage msg) {
        this.mmID = msg.mmID;
        this.vin = msg.vin;
        this.messageType = msg.messageType;
    }

    // PUBLIC METHODS //

    /**
     * Get the ID number of the Vehicle to which this message is being
     * sent.
     *
     * @return the ID number of the Vehicle to which this message is being sent
     */
    public int getVin() {
        return vin;
    }

    /**
     * Get the ID number of the Merge Manager sending this
     * message.
     *
     * @return the ID number of the MergeManager sending this message
     */
    public int getMMID() {
        return mmID;
    }

    /**
     * Get the type of this message.
     *
     * @return the type of this message
     */
    public Type getMessageType() {
        return messageType;
    }
}
