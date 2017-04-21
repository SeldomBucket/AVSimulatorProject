package aim4.msg.merge.i2v;

/**
 * Created by Callum on 19/04/2017.
 */
public class QGo extends I2VMergeMessage {
    public QGo(int mmID, int vin) {
        super(mmID, vin);
    }

    public QGo(I2VMergeMessage msg) {
        super(msg);
    }

    public I2VMergeMessage.Type getMessageType() {
        return I2VMergeMessage.Type.Q_GO;
    }
}
