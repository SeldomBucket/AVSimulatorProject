package aim4.msg.merge.i2v;

/**
 * Created by Callum on 19/04/2017.
 */
public class QConfirm extends I2VMergeMessage {
    public QConfirm(int mmID, int vin) {
        super(mmID, vin);
    }

    public QConfirm(I2VMergeMessage msg) {
        super(msg);
    }

    public I2VMergeMessage.Type getMessageType() {
        return I2VMergeMessage.Type.Q_CONFIRM;
    }
}
