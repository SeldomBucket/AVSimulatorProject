package aim4.msg.merge.v2i;

/**
 * Created by Callum on 13/04/2017.
 */
public class Away extends V2IMergeMessage {
    // PRIVATE FIELDS //

    /**
     * The ID number of the reservation.
     */
    private int reservationID;

    /////////////////////////////////
    // CLASS CONSTRUCTORS
    /////////////////////////////////

    /**
     * Basic class constructor with all required fields.
     *
     * @param sourceID              the ID number of the Vehicle sending this
     *                              message
     * @param destinationID         the ID number of the IntersectionManager to
     *                              which this message is being sent
     * @param reservationID         the ID number of the reservation
     */
    public Away(int sourceID, int destinationID, int reservationID) {
        // Set source and destination
        super(sourceID, destinationID);
        this.reservationID = reservationID;
        messageType = Type.AWAY;
    }

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    /**
     * Get the ID number of the reservation.
     */
    public int getReservationID() {
        return reservationID;
    }
}
