package aim4.im.merge.queue;

/**
 * Created by Callum on 19/04/2017.
 */

import aim4.msg.merge.i2v.QConfirm;
import aim4.msg.merge.i2v.QGo;
import aim4.msg.merge.i2v.QReject;
import aim4.msg.merge.v2i.QDone;
import aim4.msg.merge.v2i.QRequest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Manages the vehicle queue for a V2IQueueMergeManager.
 */
public class QueueManager {
    // CONSTANTS //
    private static final double MAX_DISTANCE_TO_MERGE_TO_ACCEPT = 150;

    // PRIVATE FIELDS //
    /**
     * The VIN queue containing the order the vehicles move through the merge.
     */
    private Queue<Integer> vinQueue;
    /**
     * The VIN list containing vehicle the last 2 * MAX_DISTANCE_TO_MERGE_TO_ACCEPT vehicles that have been given GO
     * commands. Used to ensure that no erroneous VEHICLE_IN_FRONT_NOT_IN_QUEUE messages are sent
     */
    private List<Integer> vinsSentGoCache;
    /**
     * Used to index vinsSentGoCache
     */
    private int vinsSentGoCacheIndex;
    /**
     * The merge manager callback
     * @param callback
     */
    private V2IQueueMergeManagerCallback callback;

    /**
     * Boolean indicating whether the merge is free
     */
    private boolean mergeFree;

    public QueueManager(V2IQueueMergeManagerCallback callback) {
        this.vinQueue = new LinkedList<Integer>();
        this.vinsSentGoCache = new ArrayList<Integer>(new Double(2*MAX_DISTANCE_TO_MERGE_TO_ACCEPT).intValue());
        this.vinsSentGoCacheIndex = 0;
        this.callback = callback;
        this.mergeFree = true;
    }

    // ACTION //
    public void act() {
        if(mergeFree && !vinQueue.isEmpty()) {
            mergeFree = false;
            int vin = vinQueue.poll();
            sendGo(vin);
            vinsSentGoCache.add(vinsSentGoCacheIndex, vin);
            if(vinsSentGoCacheIndex < vinsSentGoCache.size() - 1)
                vinsSentGoCacheIndex++;
            else
                vinsSentGoCacheIndex = 0;
        }
    }

    // COMMUNICATION //
    public void processRequest(QRequest request) {
        if(vinQueue.contains(request.getVin())) {
            sendReject(request.getVin(), QReject.Reason.ALREADY_IN_QUEUE);
        } else if(request.getDistanceToMerge() > MAX_DISTANCE_TO_MERGE_TO_ACCEPT) {
            sendReject(request.getVin(), QReject.Reason.TOO_FAR);
        } else if(!vinQueue.contains(request.getVehicleInFrontVIN()) &&
                request.getVehicleInFrontVIN() != 0 &&
                !vinsSentGoCache.contains(request.getVehicleInFrontVIN())){
            sendReject(request.getVin(), QReject.Reason.VEHICLE_IN_FRONT_NOT_IN_QUEUE);
        } else {
            vinQueue.add(request.getVin());
            sendConfirm(request.getVin());
        }
    }

    public void processDone(QDone done) {
        mergeFree = true;
    }

    public void sendConfirm(int vin) {
        QConfirm confirm = new QConfirm(callback.getId(), vin);
        callback.sendI2VMessage(confirm);
    }

    public void sendReject(int vin, QReject.Reason reason) {
        QReject reject = new QReject(callback.getId(), vin, reason);
        callback.sendI2VMessage(reject);
    }

    public void sendGo(int vin) {
        QGo go = new QGo(callback.getId(), vin);
        callback.sendI2VMessage(go);
    }

}
