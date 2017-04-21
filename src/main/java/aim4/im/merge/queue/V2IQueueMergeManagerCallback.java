package aim4.im.merge.queue;

import aim4.msg.merge.i2v.I2VMergeMessage;

/**
 * Created by Callum on 19/04/2017.
 */
public interface V2IQueueMergeManagerCallback {
    /**
     * A callback method for sending a I2V message.
     *
     * @param msg a I2V message
     */
    void sendI2VMessage(I2VMergeMessage msg);
    /**
     * Get the id of the merge manager.
     *
     * @return the id of the merge manager.
     */
    int getId();
}
