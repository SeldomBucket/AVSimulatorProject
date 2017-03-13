package aim4.map.connections;

import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.util.GeomMath;

import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Create a right-angled intersection which is the
 * intersection of 2 or 3 roads, each with one lane.
 */
public class SimpleIntersection extends BasicConnection {

    /////////////////////////////////
    // CLASS CONSTRUCTORS
    /////////////////////////////////

    /**
     * Basic class constructor.
     * Takes the Roads which intersect to make an intersection
     *
     * @param roads The roads involved in this intersection.
     */
    public SimpleIntersection(List<Road> roads) {
        super(roads);
        // Ensure that the roads given can be used to create a SimpleIntersection
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

    @Override
    protected void validate(List<Road> roads) {
        // There can only be 2 or 3 Roads involved in this SimpleIntersection
        if (roads.size() != 2 && roads.size() != 3){
            throw new IllegalArgumentException("There can only be 2 or 3 Roads " +
                    "involved in a SimpleIntersection. Number of " +
                    "roads given: " + roads.size());
        }
        // There must be one lane in each road
        if (!hasOneLane(roads)){
            throw new IllegalArgumentException("All roads in the SimpleIntersection " +
                    "must have 1 lane.");
        }
        // All lanes must be LineSegmentLanes
        for (Road road : roads){
            for (Lane lane : road.getLanes()){
                if (!(lane instanceof LineSegmentLane)){
                    throw new IllegalArgumentException("The lanes in each road " +
                            "must be a LineSegmentLane to create a SimpleIntersection.");
                }
            }
        }
        // The roads must meet at 90 degrees
        if (!atNinetyDegrees(roads)){
            throw new IllegalArgumentException("Roads in a SimpleIntersection" +
                    " must be at ninety degrees.");
        }
    }

    @Override
    protected void establishEntryAndExitPoints(Area areaOfIntersection) {
        List<Line2D> perimeterSegments =
                GeomMath.polygonalShapePerimeterSegments(areaOfIntersection);

        for (Road road : roads) {
            Lane lane = road.getOnlyLane();
            boolean startsInIntersection = doesLaneStartInPerimeter(lane, perimeterSegments);
            boolean endsInIntersection = doesLaneEndInPerimeter(lane, perimeterSegments);

            /*// A lane cannot start or end in the Intersection
            if (startsInIntersection || endsInIntersection) {
                throw new RuntimeException("A lane in an intersection cannot start or end in the intersection.");
            }*/

            if (!startsInIntersection && !endsInIntersection) {
                Point2D entryPoint;
                Point2D exitPoint;
                // Lanes run straight through the intersection so must have an entry and an exit point
                List<Point2D> intersectionPoints = new ArrayList<Point2D>();
                for (Line2D segment : perimeterSegments) {
                    if (lane.intersectionPoint(segment) != null) {
                        intersectionPoints.add(lane.intersectionPoint(segment));
                    }
                }
                assert (intersectionPoints.size() == 2);

                // Now decide which point is the entry and which is the exit
                // The point closest to the lane start point will be the entry point
                double distance1 = intersectionPoints.get(0).distance(lane.getStartPoint());
                double distance2 = intersectionPoints.get(1).distance(lane.getStartPoint());
                if (distance1 < distance2) {
                    // Point used in distance 1 is the entry point
                    entryPoint = intersectionPoints.get(0);
                    exitPoint = intersectionPoints.get(1);
                } else {
                    // Point used in distance 2 is the entry point
                    entryPoint = intersectionPoints.get(1);
                    exitPoint = intersectionPoints.get(0);
                }
                establishAsEntryPoint(road, lane, entryPoint);
                establishAsExitPoint(road, lane, exitPoint);
            } else if (startsInIntersection){
                // This road will only have an exit point
                Point2D exitPoint = null;
                for (Line2D segment : perimeterSegments) {
                    if (lane.intersectionPoint(segment) != null) {
                        exitPoint = lane.intersectionPoint(segment);
                        break;
                    }
                }
                assert(exitPoint != null);
                establishAsExitPoint(road, lane, exitPoint);
            } else if (endsInIntersection) {
                // This road will only have an entry point
                Point2D entryPoint = null;
                for (Line2D segment : perimeterSegments) {
                    if (lane.intersectionPoint(segment) != null) {
                        entryPoint = lane.intersectionPoint(segment);
                        break;
                    }
                }
                assert(entryPoint != null);
                establishAsEntryPoint(road, lane, entryPoint);
            }
        }
        assert(entryPoints.size() == 2);
        assert(exitPoints.size() == 2);

        // Fill in any of the holes
        this.areaOfConnection = GeomMath.filledArea(areaOfIntersection);
    }
}
