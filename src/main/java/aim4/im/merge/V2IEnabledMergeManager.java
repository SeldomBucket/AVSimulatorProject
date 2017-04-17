package aim4.im.merge;

import aim4.map.connections.MergeConnection;
import aim4.msg.merge.i2v.I2VMergeMessage;
import aim4.msg.merge.v2i.V2IMergeMessage;

import java.util.Iterator;

/**
 * Created by Callum on 17/04/2017.
 */
public interface V2IEnabledMergeManager extends MergeManager {
    void receive(V2IMergeMessage msg);
    void clearInbox();
    Iterator<V2IMergeMessage> inboxIterator();
    void clearOutbox();
    Iterator<I2VMergeMessage> outboxIterator();
    MergeConnection getMergeConnection();
}
