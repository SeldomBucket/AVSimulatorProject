package aim4.im.merge.policy.grid;

import aim4.msg.merge.i2v.Reject;
import aim4.msg.merge.v2i.Request;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Callum on 17/04/2017.
 */
public class FCFSMergeGridRequestHandler implements MergeGridRequestHandler {
    // PRIVATE FIELDS //
    Queue<Integer> vinQueue = new LinkedList<Integer>();

    /** The base policy */
    private BaseMergeGridPolicyCallback basePolicy = null;

    // PUBLIC METHODS //
    /**
     * Set the base policy call-back.
     *
     * @param basePolicy  the base policy's call-back
     */
    @Override
    public void setBaseMergeGridPolicyCallback(BaseMergeGridPolicyCallback basePolicy) {
        this.basePolicy = basePolicy;
    }

    /**
     * Let the request handler to act for a given time period.
     *
     * @param timeStep  the time period
     */
    @Override
    public void act(double timeStep) {
        // do nothing
    }

    /**
     * Process the request message.
     *
     * @param msg the request message
     */
    @Override
    public void processRequestMsg(Request msg) {
        int vin = msg.getVin();
        // If the vehicle has got a reservation already, reject it.
        if (basePolicy.hasReservation(vin)) {
            basePolicy.sendRejectMsg(vin,
                    msg.getRequestId(),
                    Reject.Reason.CONFIRMED_ANOTHER_REQUEST);
            return;
        }

        // filter the proposals
        BaseMergeGridPolicy.ProposalFilterResult filterResult =
                BaseMergeGridPolicy.standardProposalsFilter(msg.getProposals(),
                        basePolicy.getCurrentTime());
        if (filterResult.isNoProposalLeft()) {
            basePolicy.sendRejectMsg(vin,
                    msg.getRequestId(),
                    filterResult.getReason());
        }

        //Using vinQueue to reduce delay. Those who have been sending good requests for longer will get priority.
        if(!vinQueue.contains(vin))
            vinQueue.add(vin);

        if(vinQueue.peek() != vin) {
            basePolicy.sendRejectMsg(vin, msg.getRequestId(), Reject.Reason.NO_CLEAR_PATH);
        } else {
            // try to see if reservation is possible for the remaining proposals.
            BaseMergeGridPolicy.ReserveParam reserveParam =
                    basePolicy.findReserveParam(msg, filterResult.getProposals());
            if (reserveParam != null) {
                basePolicy.sendConfirmMessage(msg.getRequestId(), reserveParam);
                vinQueue.remove();
            } else {
                basePolicy.sendRejectMsg(vin, msg.getRequestId(),
                        Reject.Reason.NO_CLEAR_PATH);
            }
        }
    }
}
