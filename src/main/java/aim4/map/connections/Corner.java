package aim4.map.connections;

import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.util.GeomMath;

import java.awt.geom.*;
import java.util.*;

/**
 * Create a right-angled corner which leads from
 * a Road with 1 lane onto another Road with one lane.
 */
// TODO CPM Rename to make it more specific, CornerRightAngleOneLane
public class Corner extends BasicConnection {

    /////////////////////////////////
    // CLASS CONSTRUCTORS
    /////////////////////////////////

    /**
     * Basic class constructor.
     * Takes the Roads which meet at a right angle to make this corner.
     *
     * @param roads The roads involved in this corner.
     */
    public Corner(List<Road> roads) {
        super(roads);
        // Ensure that the roads given can be used to create a Corner
        validate(roads);
        // Now get the entry and exit points for each of the lanes.
        establishEntryAndExitPoints(areaOfConnection);
        // TODO These 2 following methods should be in the superclass, but
        // TODO require use of entry points and exit points.
        // TODO Is there a way around this? Or enforce to be called by subclasses?
        // Calculate the waypoints.
        calcWayPoints();
        // Now build a GeneralPath using the waypoints.
        addWayPointsPath();
    }

    /////////////////////////////////
    // PRIVATE METHODS
    /////////////////////////////////

    /**
     * Ensure that the roads given can be used to create a corner.
     * @param roads The roads given to create this corner.
     */
    protected void validate(List<Road> roads){
        // There can only be 2 Roads involved in a Corner
        if (roads.size() != 2){
            throw new IllegalArgumentException("There can only be 2 Roads " +
                                               "involved in a Corner. Number of " +
                                               "roads given: " + roads.size());
        }
        // There must be one lane in each road
        if (!hasOneLane(roads)){
            throw new IllegalArgumentException("All roads in the Corner must " +
                                               "have the same number of lanes.");
        }
        // All lanes must be LineSegmentLanes
        for (Road road : roads){
            for (Lane lane : road.getLanes()){
                if (!(lane instanceof LineSegmentLane)){
                    throw new IllegalArgumentException("The lanes in each road " +
                                                       "must be a LineSegmentLane.");
                }
            }
        }
        // The roads must meet at 90 degrees
        if (!atNinetyDegrees(roads)){
            throw new IllegalArgumentException("Roads in a Corner must be at ninety degrees.");
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void establishEntryAndExitPoints(Area areaOfCorner) {
        // CHECK ALL THESE ARE MET
        // All lanes from same road should intersect with same segment
        // Each entry point should map to an exit point?
        // Left border, right border and centre of lane should intersect with area for each lane
        List<Line2D> perimeterSegments =
                GeomMath.polygonalShapePerimeterSegments(areaOfCorner);
        for (Road road : roads) {
            // We have already checked that there is only one lane in each road
            Lane lane = road.getOnlyLane();
            boolean startsInCorner = doesLaneStartInPerimeter(lane, perimeterSegments);
            boolean endsInCorner = doesLaneEndInPerimeter(lane, perimeterSegments);

            // Each lane should intersect with only one segment (i.e. start or end point is in area, not both)
            if (startsInCorner && endsInCorner) {
                throw new RuntimeException("The lane in a corner cannot start and end in the corner.");
            }
            if (!startsInCorner && !endsInCorner) {
                throw new RuntimeException("The lane in a corner must either start or end in the corner.");
            }

            Point2D entryPoint;
            Point2D exitPoint;
            // If this lane starts in the corner, then it must have an exit point
            if (startsInCorner) {
                for (Line2D segment : perimeterSegments) {
                    // If the lane intersects (but not with it's start point)
                    if (lane.intersectionPoint(segment) != null &&
                            !isPointOnPerimeterSegment(lane.getStartPoint(), segment)) {
                        exitPoint = lane.intersectionPoint(segment);
                        establishAsExitPoint(road, lane, exitPoint);
                        break;
                    }
                }
            } else {   // If this lane ends in the corner, then it must have an entry point
                for (Line2D segment : perimeterSegments) {
                    // If the lane intersects (but not with it's end point)
                    if (lane.intersectionPoint(segment) != null &&
                            !isPointOnPerimeterSegment(lane.getEndPoint(), segment)) {
                        entryPoint = lane.intersectionPoint(segment);
                        establishAsEntryPoint(road, lane, entryPoint);
                        break;
                    }
                }
            }
        }
        // Fill in any of the holes
        this.areaOfConnection = GeomMath.filledArea(areaOfCorner);
    }
}
