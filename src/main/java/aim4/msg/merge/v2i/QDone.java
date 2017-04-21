package aim4.msg.merge.v2i;

/**
 * Created by Callum on 19/04/2017.
 */
public class QDone extends V2IMergeMessage {
    // CONSTRUCTOR //
    public QDone(int vin, int mmID) {
        super(vin, mmID);
    }

    public QDone(V2IMergeMessage msg) {
        super(msg);
    }

    public Type getMessageType() {
        return Type.Q_DONE;
    }
}
