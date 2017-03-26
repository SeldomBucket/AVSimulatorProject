package util.map;

import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.map.merge.MergeMap;
import aim4.map.merge.MergeSpawnPoint;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by Callum on 17/03/2017.
 */
public class SingleLaneOnlyMap extends MergeMap {
    private static final double HEIGHT = 50.0;
    private static final double LANE_WIDTH = 4.0;
    private static final double LANE_Y_POS = 25.0;
    private static final double LANE_START_X_POS = 0.0;
    private static final double LANE_UPPER = LANE_Y_POS + LANE_WIDTH/2;
    private static final double LANE_LOWER = LANE_Y_POS - LANE_WIDTH/2;

    private int laneRegistryID;

    public SingleLaneOnlyMap(double initTime, double speedLimit, double laneLength) {
        //Set our dimensions
        setDimensions(new Rectangle2D.Double(0, 0, laneLength, HEIGHT));

        //Create road
        Road road = new Road("Single Lane Road", this);

        //Create lane

        Point2D laneStart = new Point2D.Double(LANE_START_X_POS, LANE_Y_POS);
        Point2D laneEnd = new Point2D.Double(laneLength, LANE_Y_POS);
        Line2D laneLine = new Line2D.Double(
                laneStart.getX(),
                laneStart.getY(),
                laneEnd.getX(),
                laneEnd.getY()
        );
        Lane lane = new LineSegmentLane(laneLine, LANE_WIDTH, speedLimit);

        //Register lane and add to road
        int laneRegistryID = getLaneRegistry().register(lane);
        lane.setId(laneRegistryID);
        road.addTheRightMostLane(lane);
        addLaneToRoad(lane,road);

        //Add data collection lines
        addDataCollectionLine("Lane Entrance",
                new Point2D.Double(LANE_START_X_POS, LANE_UPPER),
                new Point2D.Double(LANE_START_X_POS, LANE_LOWER),
                true);
        addDataCollectionLine("Lane Exit",
                new Point2D.Double(laneLength, LANE_UPPER),
                new Point2D.Double(laneLength, LANE_LOWER),
                true);

        //Add road to roads
        addRoad(road);

        //Create spawn points
        MergeSpawnPoint spawn = makeSpawnPoint(road.getLanes().get(0), 0.0, 0.0);
        addSpawnPoint(spawn);
    }

    public Lane getLane() {
        return getLaneRegistry().get(laneRegistryID);
    }

    public DataCollectionLine getEntranceDCLine() {
        return getDataCollectionLines().get(0);
    }

    public DataCollectionLine getExitDCLine() {
        return getDataCollectionLines().get(1);
    }
}
