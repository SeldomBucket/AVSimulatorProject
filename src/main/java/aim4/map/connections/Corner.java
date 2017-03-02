package aim4.map.connections;

import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.map.track.WayPoint;
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
    // PRIVATE FIELDS
    /////////////////////////////////



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
     * Check if all roads have the same number of lanes.
     * @param roads the roads involved in this corner.
     *
     * @return boolean - true if all roads have the same number of lanes.
     */
    private boolean hasOneLane(List<Road> roads){

        // Now compare this with the numebr of lanes in all the roads
        for (Road road : roads){
            int numberOfLanesInThisRoad = road.getLanes().size();
            if (numberOfLanesInThisRoad != 1){
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the roads given to create this corner meet at 90 degrees.
      * @param roads
     * @return true if roads meet at ninety degrees.
     */
    private boolean atNinetyDegrees(List<Road> roads){
        // The 2 roads must meet at 90 degrees.
        if (roads.size() == 2){
            LineSegmentLane laneFromRoad1 = (LineSegmentLane) roads.get(0).getLanes().get(0);
            LineSegmentLane laneFromRoad2 = (LineSegmentLane) roads.get(1).getLanes().get(0);
            Point2D intersectionPoint = laneFromRoad1.intersectionPoint(laneFromRoad2.getLine());
            double angleInRadians = GeomMath.angleBetweenTwoPointsWithFixedPoint(laneFromRoad1.getStartPoint(),
                                                                        laneFromRoad2.getStartPoint(),
                                                                        intersectionPoint);
            double angleInDegrees = Math.toDegrees(angleInRadians);
            if (angleInDegrees == 90.0) {
                return true;
            }
        }
        return false;
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
        Point2D entryPoint = null;  // Check these null values somewhere?
        Point2D exitPoint = null;  // Should initialise somewhere else?
        for (Road road : roads) {
            for (Lane lane : road.getLanes()) {
                boolean startsInCorner = doesLaneStartInCorner(lane, perimeterSegments);
                boolean endsInCorner = doesLaneEndInCorner(lane, perimeterSegments);

                // Each lane should intersect with only one segment (i.e. start or end point is in area, not both)
                if (startsInCorner && endsInCorner) {
                    throw new RuntimeException("The lane in a corner cannot start and end in the corner.");
                }
                if (!startsInCorner && !endsInCorner) {
                    throw new RuntimeException("The lane in a corner must either start or end in the corner.");
                }

                // If this lane starts in the corner, then it must have an exit point
                if (startsInCorner) {
                    for (Line2D segment : perimeterSegments) {
                        // If the lane intersects (but not with it's end point)
                        if (lane.intersectionPoint(segment) != null &&
                                !isPointOnPerimeterSegment(lane.getStartPoint(), segment)) {
                            exitPoint = lane.intersectionPoint(segment);
                            exitPoints.put(lane, new WayPoint(exitPoint));
                            // Here we are assuming that the heading of the lane is the same throughout.
                            exitHeadings.put(lane, lane.getInitialHeading());
                            if (!exitRoads.contains(road)){
                                exitRoads.add(road);
                            }
                            break;
                        }
                    }
                } else {   // If this lane ends in the corner, then it must have an entry point
                    for (Line2D segment : perimeterSegments) {
                        // If the lane intersects (but not with it's end point)
                        if (lane.intersectionPoint(segment) != null &&
                                !isPointOnPerimeterSegment(lane.getEndPoint(), segment)) {
                            entryPoint = lane.intersectionPoint(segment);
                            entryPoints.put(lane, new WayPoint(entryPoint));
                            // Here we are assuming that the heading of the lane is the same throughout.
                            entryHeadings.put(lane, lane.getInitialHeading());
                            if (!entryRoads.contains(road)){
                                entryRoads.add(road);
                            }
                            break;
                        }
                    }
                }

            }
        }
        // Fill in any of the holes
        this.areaOfConnection = GeomMath.filledArea(areaOfCorner);
    }

    /**
     * Does a lane start on the perimeter of the corner or inside the corner.
     *
     * @param lane The lane we want to check if it's start point is within the area or on the perimeter of the corner.
     * @param perimeterSegments The perimeter segments of the area of the corner.
     * @return true if the lanes start point is on the perimeter or inside the corner's area.
     */
    private boolean doesLaneStartInCorner(Lane lane, List<Line2D> perimeterSegments){
        for (Line2D segment : perimeterSegments){
            // if the start point is on the perimeter of the corner
            if (isPointOnPerimeterSegment(lane.getStartPoint(), segment)){
                return true;
            }
        }
        // if the start point is inside the corner
        if(areaOfConnection.contains(lane.getStartPoint())) {
            return true;
        }
        return false;
    }

    /**
     * Does a lane end on the perimeter of the corner or inside the corner.
     *
     * @param lane The lane we want to check if it's end point is within the area or on the perimeter of the corner.
     * @param perimeterSegments The perimeter segments of the area of the corner.
     * @return true if the lanes end point is on the perimeter or inside the corner's area.
     */
    private boolean doesLaneEndInCorner(Lane lane, List<Line2D> perimeterSegments){
        for (Line2D segment : perimeterSegments){
            // if the end point is on the perimeter of the corner
            if (isPointOnPerimeterSegment(lane.getEndPoint(), segment)){
                return true;
            }
        }
        // if the end point is inside the corner
        if(areaOfConnection.contains(lane.getEndPoint())) {
            return true;
        }
        return false;
    }

    /**
     * Check if a point is on the perimeter segment line.
     * @param point The point to to check if it is on the perimeter segment line.
     * @param segment The segment of the perimeter we want to check if the point is on.
     * @return true if the point is on the perimeter segment line.
     */
    private boolean isPointOnPerimeterSegment(Point2D point, Line2D segment){
        if (segment.ptSegDist(point) == 0.0){
            return true;
        }
        return false;
    }

    /**
     * Find the entry lanes.
     */
    public List<Lane> getEntryLanes() {
        return new ArrayList<Lane>(entryPoints.keySet());
    }

    /**
     * Find the exit lanes.
     */
    public List<Lane> getExitLanes() {
        return new ArrayList<Lane>(exitPoints.keySet());
    }

    /**
     * Get the Roads incident to the corner.
     *
     * @return the roads involved in this corner.
     */
    public List<Road> getRoads() {
        return roads;
    }

    /**
     * Get the Lanes incident to the corner.
     *
     * @return the lanes involved in this corner.
     */
    public List<Lane> getLanes() {
        return lanes;
    }

    /**
     * Get the Area of this Corner.
     *
     * @return the Area of the corner
     */
    public Area getArea() {
        return areaOfConnection;
    }

    /**
     * Get the centroid of the corner.
     *
     * @return the centroid of the corner
     */
    public Point2D getCentroid() {
        return centroid;
    }

    /**
     * Get the minimal rectangular region that encloses the corner.
     *
     * @return the minimal rectangular region that encloses the corner
     */
    /*public Rectangle2D getBoundingBox() {
        return boundingBox;
    }*/

    /**
     * Get the list of edges.
     *
     * @return the list of edges
     */
    /*public List<Path2D> getEdges() {
        return edges;
    }*/

    /**
     * Get the Roads that enter the corner.
     *
     * @return the Roads that enter the corner.
     */
    public List<Road> getEntryRoads() {
        return entryRoads;
    }

    /**
     * Whether the given Lane enters this Corner.
     *
     * @param l the Lane to consider
     * @return  whether the Lane enters this Corner
     */
    public boolean isEnteredBy(Lane l) {
        return entryPoints.containsKey(l);
    }

    /**
     * Get the Point at which the given Lane enters the corner.
     *
     * @param l the Lane
     * @return  the Point at which the given Lane enters the corner, or
     *          <code>null</code> if it does not
     */
    public WayPoint getEntryPoint(Lane l) {
        return entryPoints.get(l);
    }

    /**
     * Get the heading at which the given lane enters the corner.
     *
     * @param l the Lane
     * @return  the heading at which the Lane enters the corner
     */
    public double getEntryHeading(Lane l) {
        // TODO: what if l is not a lane entering this corner?
        return entryHeadings.get(l);
    }

    /**
     * Get the Roads that exit the corner.
     *
     * @return the Roads that exit the corner
     */
    public List<Road> getExitRoads() {
        return exitRoads;
    }

    /**
     * Whether the given Lane exits this corner.
     *
     * @param l the Lane to consider
     * @return  whether the Lane exits this corner
     */
    public boolean isExitedBy(Lane l) {
        return exitPoints.containsKey(l);
    }

    /**
     * Get the Point at which the given Lane exits the corner.
     *
     * @param l the Lane
     * @return  the Point at which the given Lane exits the corner, or
     *          <code>null</code> if it does not
     */
    public WayPoint getExitPoint(Lane l) {
        return exitPoints.get(l);
    }

    /**
     * Get the heading at which the given Lane exits the corner.
     *
     * @param l the Lane
     * @return  the heading at which the Lane exits the corner
     */
    public double getExitHeading(Lane l) {
        return exitHeadings.get(l);
    }

    /**
     * Get the turn direction of the vehicle at the next corner.
     *
     * @param currentLane    the current lane.
     * @param departureLane  the departure lane.
     * @return the turn direction of the vehicle at the next corner
     */
    /*public Constants.TurnDirection calcTurnDirection(Lane currentLane, Lane departureLane) {


        Road currentRoad = Debug.currentMap.getRoad(currentLane);
        Road departureRoad = Debug.currentMap.getRoad(departureLane);
        if(departureRoad == currentRoad) {
            return Constants.TurnDirection.STRAIGHT;
        } else if(departureRoad == currentRoad.getDual()) {
            return Constants.TurnDirection.U_TURN;
        } else {
            double entryHeading = getEntryHeading(currentLane);
            double exitHeading = getExitHeading(departureLane);
            double theta = GeomMath.canonicalAngle(exitHeading-entryHeading);
            if(Util.isDoubleZero(theta)) {
                return Constants.TurnDirection.STRAIGHT; // despite they are different roads
            } else if(theta < Math.PI) {
                return Constants.TurnDirection.LEFT;
            } else if(theta > Math.PI) {
                return Constants.TurnDirection.RIGHT;
            } else {  // theta = Math.PI
                return Constants.TurnDirection.U_TURN;  // pretty unlikely.
            }
        }
    }*/
}
