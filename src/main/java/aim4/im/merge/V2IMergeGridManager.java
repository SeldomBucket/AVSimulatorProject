package aim4.im.merge;

import aim4.im.AczManager;
import aim4.im.AdmissionControlZone;
import aim4.im.merge.policy.grid.MergeGridPolicy;
import aim4.im.merge.policy.grid.V2IMergeGridManagerCallback;
import aim4.im.merge.reservation.grid.ReservationMergeGrid;
import aim4.im.merge.reservation.grid.ReservationMergeGridManager;
import aim4.map.connections.MergeConnection;
import aim4.map.lane.Lane;
import aim4.map.merge.MergeMap;
import aim4.msg.merge.i2v.I2VMergeMessage;
import aim4.msg.merge.v2i.V2IMergeMessage;
import aim4.util.Registry;
import aim4.util.TiledArea;

import java.util.*;

/**
 * Created by Callum on 17/04/2017.
 */
public class V2IMergeGridManager extends BasicMergeManager implements V2IMergeGridManagerCallback, V2IEnabledMergeManager {
    //CONSTANTS//
    /**
     * The maximum amount of time, in seconds, in the future, for which the
     * mergePolicy will accept reservation requests. {@value} seconds.
     */
    public static final double MAXIMUM_FUTURE_RESERVATION_TIME = 10.0; // sec

    /**
     * The default distance the IMergeMaanger can transmit messages.
     * {@value} meters.
     */
    private static final double DEFAULT_TRANSMISSION_POWER = Double.MAX_VALUE; // meters
    /**
     * The default size (capacity) of an {@link AdmissionControlZone} for Lanes
     * exiting the intersection managed by this V2IManager, in meters. {@value}
     * meters.
     */
    private static final double DEFAULT_ACZ_SIZE = 40.0; // meters
    /**
     * The length, in meters, of the AdmissionControlZone for which to return
     * a debug shape.
     */
    private static final double ACZ_DISTANCE_SHAPE_LENGTH = 1; // meter

    /**
     * The merge control mergePolicy.
     */
    private MergeGridPolicy mergePolicy;

    //MESSAGING
    /**
     * The maximum distance the IMergeMaanger can transmit a message, in
     * meters.
     */
    private double transmissionPower = DEFAULT_TRANSMISSION_POWER;
    /** A List of messages received from Vehicles waiting to be processed. */
    private List<V2IMergeMessage> inbox = new ArrayList<V2IMergeMessage>();
    /** A List of messages waiting to be sent to Vehicles. */
    private List<I2VMergeMessage> outbox = new ArrayList<I2VMergeMessage>();

    //RESERVATIONS
    /**
     * The area in the merge.
     */
    private ReservationMergeGrid reservationMergeGrid;
    /**
     * The reservation system
     */
    private ReservationMergeGridManager reservationMergeGridManager;
    /**
     * The tiled area of the intersection
     */
    private TiledArea tiledArea;

    // aczs //

    /**
     * A map from each outgoing lane's id to the admission control zone that
     * governs the lane just outside the merge zone exit.
     */
    private Map<Integer,AdmissionControlZone> aczs =
            new LinkedHashMap<Integer,AdmissionControlZone>();

    /**
     * The ACZ managers
     */
    private Map<Integer,AczManager> aczManagers =
            new LinkedHashMap<Integer,AczManager>();

    //CONSTRUCTORS//
    public V2IMergeGridManager(MergeConnection merge,
                               double currentTime,
                               ReservationMergeGridManager.Config config,
                               Registry<MergeManager> registry,
                               MergeMap layout) {
        super(merge, currentTime, registry);
        this.tiledArea = new TiledArea(merge.getArea(), config.getGranularity());
        this.reservationMergeGrid = new ReservationMergeGrid(tiledArea.getXNum(), tiledArea.getYNum(), config.getGridTimeStep());
        this.reservationMergeGridManager = new ReservationMergeGridManager(config, merge, tiledArea, reservationMergeGrid, layout);
        //Setup AdmissionControlZones
        for(Lane l : getMergeConnection().getExitLanes()){
            AdmissionControlZone acz = new AdmissionControlZone(DEFAULT_ACZ_SIZE);
            aczs.put(l.getId(), acz);
            aczManagers.put(l.getId(), new AczManager(acz));
        }
    }

    //PUBLIC METHODS//
    //ACCESSORS
    /**
     * Get the mergePolicy.
     */
    public MergeGridPolicy getMergePolicy() {
        return mergePolicy;
    }

    /**
     * set the mergePolicy.
     *
     * @param mergePolicy  the mergePolicy
     */
    public void setMergePolicy(MergeGridPolicy mergePolicy) {
        this.mergePolicy = mergePolicy;
    }

    /**
     * Get the MergeManagers's transmission power.
     *
     * @return the MergeManagers's transmission power, in meters
     */
    public double getTransmissionPower() {
        return transmissionPower;
    }

    /**
     * @inheritDoc
     */
    public ReservationMergeGrid getReservationMergeGrid() {
        return reservationMergeGrid;
    }

    /**
     * @inheritDoc
     */
    @Override
    public ReservationMergeGridManager getReservationMergeGridManager() {
        return reservationMergeGridManager;
    }

    /**
     * Get the Admission Control Zone of a given lane.
     *
     * @param laneId  the id of the lane
     * @return the admission control zone of the lane.
     */
    @Override
    public AdmissionControlZone getACZ(int laneId) {
        return aczs.get(laneId);
    }

    /**
     * Get the manager of an ACZ
     */
    @Override
    public AczManager getAczManager(int laneId) {
        return aczManagers.get(laneId);
    }

    //ACTION
    /**
     * Give the V2IMergeManager a chance to respond to messages from vehicles, change
     * policies, and so forth.
     *
     * @param timeStep  the size of the time step to simulate, in seconds
     */
    @Override
    public void act(double timeStep) {
        // First, process all the incoming messages waiting for us
        for(Iterator<V2IMergeMessage> iter = inboxIterator(); iter.hasNext();) {
            V2IMergeMessage msg = iter.next();
            processV2IMergeMessage(msg);
        }
        // Done processing, clear the inbox.
        clearInbox();
        // Second, allow the mergePolicy to act, and send outgoing messages.
        mergePolicy.act(timeStep);
        // Third, allow the reservation grid manager to act
        reservationMergeGridManager.act(timeStep);
        // Advance current time.
        super.act(timeStep);
    }

    //COMMUNICATIONS

    /**
     * Get an iterator for the messages waiting to be read.
     *
     * @return an iterator for the messages waiting to be read.
     */
    public Iterator<V2IMergeMessage> inboxIterator() {
        return inbox.iterator();
    }

    /**
     * Clear out the inbox.
     */
    public void clearInbox() {
        inbox.clear();
    }

    /**
     * Get an iterator for the messages waiting to be delivered from this
     * IntersectionManager.
     *
     * @return an iterator for the messages waiting to be delivered from
     *         this IntersectionManager
     */
    public Iterator<I2VMergeMessage> outboxIterator() {
        return outbox.iterator();
    }

    /**
     * Clear out the outbox.
     */
    public void clearOutbox() {
        outbox.clear();
    }

    /**
     * Adds a message to the incoming queue of messages delivered to this
     * IntersectionManager.
     *
     * @param msg the message to be received
     */
    public void receive(V2IMergeMessage msg) {
        // Just tack the message on to the end of the inbox list
        inbox.add(msg);
    }

    /**
     * Process a V2I message
     *
     * @param msg  the V2I message
     */
    private void processV2IMergeMessage(V2IMergeMessage msg) {
        mergePolicy.processV2IMergeMessage(msg);
    }

    @Override
    public void sendI2VMessage(I2VMergeMessage msg) {
        outbox.add(msg);
    }

}
