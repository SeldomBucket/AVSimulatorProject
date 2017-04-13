package aim4.im.merge.policy;

/**
 * Created by Callum on 13/04/2017.
 */

import aim4.msg.merge.v2i.Request;

/**
 * The merge request handler.
 */
public interface MergeRequestHandler {
    /**
     * Set the base policy call-back.
     *
     * @param basePolicy  the base policy's call-back
     */
    void setBaseMergePolicyCallback(BaseMergePolicyCallback basePolicy);

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
