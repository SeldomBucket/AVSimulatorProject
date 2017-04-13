package aim4.im.merge.policy;

import aim4.msg.merge.v2i.V2IMergeMessage;

/**
 * Created by Callum on 13/04/2017.
 */
public interface MergePolicy {
    /**
     * Set the V2I merge manager call-back.
     *
     * @param mm  the V2I merge manager's call-back
     */
    void setV2IMergeManagerCallback(V2IMergeManagerCallback mm);

    /**
     * Give the policy a chance to do any processing it might need to do in
     * order to respond to requests, if it hasn't responded to them already.
     * Only used for policies that don't respond immediately to requests.
     *
     * @param timeStep  the size of the time step to simulate, in seconds
     */
    void act(double timeStep);

    /**
     * Process a V2I merge message
     *
     * @param msg  the V2I merge message
     */
    void processV2IMergeMessage(V2IMergeMessage msg);
}
