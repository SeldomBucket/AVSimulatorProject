package aim4.msg.merge.i2v;

/**
 * Created by Callum on 19/04/2017.
 */
public class QReject extends I2VMergeMessage {
    /**
     * Some of the possible reasons why the request was rejected.
     */
    public enum Reason {
        /** The vehicle was too far away from the merge to enter the queue */
        TOO_FAR,
        /** The vehicle is already in the queue */
        ALREADY_IN_QUEUE
    }

    // PRIVATE FIELDS //
    private Reason reason;

    public QReject(int mmID, int vin, Reason reason) {
        super(mmID, vin);
        this.reason = reason;
    }

    public QReject(QReject msg) {
        this(
                msg.getMMID(),
                msg.getVin(),
                msg.getReason()
        );
    }

    public Reason getReason() {
        return reason;
    }

    public I2VMergeMessage.Type getMessageType() {
        return I2VMergeMessage.Type.Q_REJECT;
    }

}
