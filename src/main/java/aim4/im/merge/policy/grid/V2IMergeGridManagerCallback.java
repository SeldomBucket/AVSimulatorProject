package aim4.im.merge.policy.grid;

import aim4.im.AczManager;
import aim4.im.AdmissionControlZone;
import aim4.im.merge.reservation.ReservationMergeGrid;
import aim4.im.merge.reservation.ReservationMergeGridManager;
import aim4.map.connections.MergeConnection;
import aim4.msg.merge.i2v.I2VMergeMessage;

/**
 * Created by Callum on 17/04/2017.
 */
public interface V2IMergeGridManagerCallback {
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

    /**
     * Get the current time
     *
     * @return the current time
     */
    double getCurrentTime();

    /**
     * Get the merge connection managed by this merge manager.
     *
     * @return the merge connection managed by this merge manager
     */
    MergeConnection getMergeConnection();

    /**
     * Get the merge area.
     *
     * @return the merge area
     */
    ReservationMergeGrid getReservationMergeGrid();

    /**
     * Get the merge reservation manager
     *
     * @return the merge reservation manager
     */
    ReservationMergeGridManager getReservationMergeGridManager();

    /**
     * Get the Admission Control Zone of a given lane.
     *
     * @param laneId  the id of the lane
     * @return the admission control zone of the lane.
     */
    AdmissionControlZone getACZ(int laneId);

    /**
     * Get the manager of an ACZ
     */
    AczManager getAczManager(int laneId);
}
