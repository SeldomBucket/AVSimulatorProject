package aim4.im.merge.policy.grid;

import aim4.msg.merge.v2i.Request;

/**
 * Created by Callum on 17/04/2017.
 */
public interface MergeGridRequestHandler {
    /**
     * Set the base policy call-back.
     *
     * @param basePolicy  the base policy's call-back
     */
    void setBaseMergeGridPolicyCallback(BaseMergeGridPolicyCallback basePolicy);

    /**
     * Let the request handler to act for a given time period.
     *
     * @param timeStep  the time period
     */
    void act(double timeStep);

    /**
     * Process the request message.
     *
     * @param msg the request message
     */
    void processRequestMsg(Request msg);
}
