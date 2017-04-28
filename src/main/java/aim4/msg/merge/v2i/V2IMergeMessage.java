package aim4.msg.merge.v2i;

/**
 * Created by Callum on 13/04/2017.
 */
public class V2IMergeMessage {
    // NESTED CLASSES //
    /**
     * The different types of Vehicle to Merge Manager
     * messages.
     */
    public enum Type {
        /**
         * Mesage requesting a reservation or a change of reservation.
         */
        REQUEST,
        /**
         * Message cancelling a currently held reservation.
         */
        CANCEL,
        /**
         * Message indicating that the vehicle has traversed the intersection.
         */
        DONE,
        /**
         * Message requesting entry into the admission control zone.
         */
        ACZ_REQUEST,
        /**
         *  Message cancelling a previous ACZ_REQUEST.
         */
        ACZ_CANCEL,
        /**
         * Message indicating the vehicle has completed entering the admission
         * control zone.
         */
        ACZ_ENTERED,
        /**
         * Message indicating the vehicle has left the admission control zone by
         * leaving the roadway.
         */
        ACZ_EXIT,
        /**
         * Requests a position in the queue for a queue manager
         */
        Q_REQUEST,
        /**
         * Lets a queue manager know that the vehicle has left the merge zone
         */
        Q_DONE,
        /**
         * Message indicating the vehicle has left the admission control zone by
         * driving straight out of it.
         */
        AWAY,
    };

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /**
     * The ID number of the Merge Manager to which this message is
     * being sent.
     */
    private int mmID;
    /**
     * The ID number of the Vehicle sending this message
     */
    private int vin;

    /////////////////////////////////
    // PROTECTED FIELDS
    /////////////////////////////////

    /** The type of this message. */
    protected Type messageType;

    /////////////////////////////////
    // CLASS CONSTRUCTORS
    /////////////////////////////////

    /**
     * Class constructor to be called by subclasses to set the source and
     * destination ID numbers.
     *
     * @param vin      the ID number of the Vehicle sending this message
     * @param mmID the ID number of the MergeManager to which
     *                      this message is being sent
     */
    public V2IMergeMessage(int vin, int mmID) {
        this.vin = vin;
        this.mmID = mmID;
    }

    public V2IMergeMessage(V2IMergeMessage msg) {
        this.vin = msg.vin;
        this.mmID = msg.mmID;
        this.messageType = msg.messageType;
    }

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    /**
     * Get the ID number of the Merge Manager to which this
     * message is being sent.
     *
     * @return the ID number of the MergeManager to which this message is
     *         being sent
     */
    public int getMMID() {
        return mmID;
    }

    /**
     * Get the ID number of the Vehicle sending this message.
     *
     * @return the ID number of the Vehicle sending this message
     */
    public int getVin() {
        return vin;
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
