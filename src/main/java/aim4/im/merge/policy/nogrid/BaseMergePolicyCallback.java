package aim4.im.merge.policy.nogrid;

import aim4.im.merge.policy.nogrid.BaseMergePolicy.ReserveParam;
import aim4.im.merge.reservation.nogrid.ReservationMerge;
import aim4.msg.merge.i2v.Reject;
import aim4.msg.merge.v2i.Request;

import java.util.List;

/**
 * Created by Callum on 13/04/2017.
 */
public interface BaseMergePolicyCallback {
    /**
     * Send a confirm message
     *
     * @param latestRequestId  the latest request id of the vehicle
     * @param reserveParam     the reservation parameter
     */
    void sendConfirmMessage(int latestRequestId,
                            BaseMergePolicy.ReserveParam reserveParam);
    /**
     * Send a reject message
     *
     * @param vin              the VIN
     * @param latestRequestId  the latest request id of the vehicle
     * @param reason           the reason of rejection
     */
    void sendRejectMsg(int vin, int latestRequestId, Reject.Reason reason);

    /**
     * Compute the reservation parameter given the request message and a
     * set of proposals.
     *
     * @param msg        the request message
     * @param proposals  the set of proposals
     * @return the reservation parameters; null if the reservation is infeasible.
     */
    ReserveParam findReserveParam(Request msg, List<Request.Proposal> proposals);

    /**
     * Get the current time
     *
     * @return the current time
     */
    double getCurrentTime();

    /**
     * Check whether the vehicle currently has a reservation.
     *
     * @param vin  the VIN of the vehicle
     * @return whether the vehicle currently has a reservation.
     */
    boolean hasReservation(int vin);
    /**
     * Get the reservation merge
     * @return the reservation merge
     */
    ReservationMerge getReservationMerge();
}
