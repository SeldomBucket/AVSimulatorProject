package aim4.map.connections;

import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.map.merge.RoadNames;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Created by Callum on 10/04/2017.
 */
public class S2SMergeConnection extends MergeConnection {
    private Point2D targetEntryPoint;
    private Point2D mergeEntryPoint;
    private Point2D exitPoint;

    /**
     * Basic class constructor.
     * Takes the Roads which meet to make this connection.
     *
     * @param roads the roads involved in this connection.
     */
    public S2SMergeConnection(List<Road> roads, Point2D targetEntryPoint, Point2D mergeEntryPoint, Point2D exitPoint) {
        super(roads);
        this.areaOfConnection = new Area(new Rectangle2D.Double(
                targetEntryPoint.getX(), // Upper left corner X of merge zone
                mergeEntryPoint.getY(),  // Upper left corner Y of merge zone
                Point2D.distance(targetEntryPoint.getX(), targetEntryPoint.getY(),
                        exitPoint.getX(), exitPoint.getY()), //Width of merge zone
                2 * Point2D.distance(mergeEntryPoint.getX(), mergeEntryPoint.getY(),
                        mergeEntryPoint.getX(), mergeEntryPoint.getY()))); //Height of merge zone
        validate(roads);
        this.targetEntryPoint = targetEntryPoint;
        this.mergeEntryPoint = mergeEntryPoint;
        this.exitPoint = exitPoint;
        establishEntryAndExitPoints(areaOfConnection);
        calcWayPoints();
        addWayPointsPath();
    }

    @Override
    protected void validate(List<Road> roads) {
        if(roads.size() != 2) {
            throw new IllegalArgumentException("There must be two roads involved in an S2SMergeConnection" +
                "Number of raods given: " + roads.size());
        }
        if(!hasOneLane(roads)) {
            throw new IllegalArgumentException("All roads in an S2SMergeConnection must have one lane");
        }
        for (Road road : roads){
            for (Lane lane : road.getLanes()){
                if (!(lane instanceof LineSegmentLane)){
                    throw new IllegalArgumentException("The lanes in each road " +
                            "must be a LineSegmentLane to create a S2SMergeConnection.");
                }
            }
        }
    }

    @Override
    protected void establishEntryAndExitPoints(Area areaOfMerge) {
        for(Road road : this.roads) {
            if(road.getName().equals(RoadNames.TARGET_ROAD.toString())){
                establishAsEntryPoint(road, road.getOnlyLane(), targetEntryPoint);
                establishAsExitPoint(road, road.getOnlyLane(), exitPoint);
            }
            else if(road.getName().equals(RoadNames.MERGING_ROAD.toString())){
                establishAsEntryPoint(road, road.getOnlyLane(), mergeEntryPoint);
            }
            else
                throw new IllegalArgumentException("Unexpected road name found");
        }

    }
}
