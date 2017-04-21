package aim4.im.merge.queue;

/**
 * Created by Callum on 19/04/2017.
 */

import aim4.msg.merge.i2v.QConfirm;
import aim4.msg.merge.i2v.QGo;
import aim4.msg.merge.i2v.QReject;
import aim4.msg.merge.v2i.QDone;
import aim4.msg.merge.v2i.QRequest;

import java.util.LinkedList;
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
        this.callback = callback;
        this.mergeFree = true;
    }

    // ACTION //
    public void act() {
        if(mergeFree && !vinQueue.isEmpty()) {
            mergeFree = false;
            sendGo(vinQueue.poll());
        }
    }

    // COMMUNICATION //
    public void processRequest(QRequest request) {
        if(vinQueue.contains(request.getVin())) {
            sendReject(request.getVin(), QReject.Reason.ALREADY_IN_QUEUE);
        } else if(request.getDistanceToMerge() > MAX_DISTANCE_TO_MERGE_TO_ACCEPT) {
            sendReject(request.getVin(), QReject.Reason.TOO_FAR);
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
