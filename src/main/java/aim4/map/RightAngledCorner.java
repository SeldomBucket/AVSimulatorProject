package aim4.map;

import aim4.config.Constants;
import aim4.config.Debug;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.map.track.WayPoint;
import aim4.util.GeomMath;
import aim4.util.Util;

import javax.sound.sampled.Line;
import java.awt.geom.*;
import java.util.*;

/**
 * Create a right-angled corner which leads from one Road onto another Road.
 */
public class RightAngledCorner {

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /**
     * The space governed by this corner.
     */
    private Area areaOfCorner;

    /**
     * The centroid of this corner.
     */
    private Point2D centroid;

    // road

    /** The roads which meet to make this corner. */
    private List<Road> roads = new ArrayList<Road>();

    /** The entry roads incidents to this corner. */
    private List<Road> entryRoads = new ArrayList<Road>();

    /** The exit roads incidents to this corner. */
    private List<Road> exitRoads = new ArrayList<Road>();

    // lanes

    /** The lanes which meet to make this corner. */
    private List<Lane> lanes = new ArrayList<Lane>();

    // points

    /**
     * A list of the coordinates where lanes enter or exit the corner,
     * ordered by angle from the centroid.
     */
    private List<Point2D> points = new ArrayList<Point2D>();

    /**
     * A map from lanes to the coordinates at which those lanes enter the
     * corner.
     */
    private Map<Lane,WayPoint> entryPoints = new LinkedHashMap<Lane,WayPoint>();

    /**
     * A map from lanes to the coordinates at which those lanes exit the
     * corner.
     */
    private Map<Lane,WayPoint> exitPoints = new LinkedHashMap<Lane,WayPoint>();

    /**
     * A map from Lanes to the headings, in radians, of those Lanes at the
     * point at which they enter the space governed by this Corner.
     */
    private Map<Lane,Double> entryHeadings = new HashMap<Lane,Double>();

    /**
     * A map from Lanes to the headings, in radians, of those Lanes at the
     * point at which they exit the space governed by this Corner.
     */
    private Map<Lane,Double> exitHeadings = new HashMap<Lane,Double>();

    /////////////////////////////////
    // CLASS CONSTRUCTORS
    /////////////////////////////////

    /**
     * Basic class constructor.
     * Takes the Roads which meet at a right angle to make this corner.
     *
     * @param road1 a Road which will be part of the corner.
     * @param road2 a Road which will be part of the corner.
     */
    public RightAngledCorner(Road road1, Road road2) {
        List<Road> roads = new ArrayList<Road>(2);
        roads.add(road1);
        roads.add(road2);
        // Ensure that the roads given can be used to create a Corner
        validate(roads);
        this.roads = roads;
        // Get the list of Lanes we are using.
        extractLanes(roads);
        // Now get the entry and exit points for each of the lanes.
        this.areaOfCorner = findAreaOfCorner(roads);
        establishEntryAndExitPoints(areaOfCorner);
        // Find the centroid of the corner
        centroid = GeomMath.polygonalShapeCentroid(areaOfCorner);
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
    private void validate(List<Road> roads){
        // There can only be 2 Roads involved in a Corner
        if (roads.size() != 2){
            throw new IllegalArgumentException("There can only be 2 Roads " +
                                               "involved in a Corner. Number of " +
                                               "roads given: " + roads.size());
        }
        // There must be the same number of lanes in each road
        if (!haveSameNumberOfLanes(roads)){
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
    private boolean haveSameNumberOfLanes(List<Road> roads){
        // Get the number of lanes in the first road
        int numberOfLanesForAllRoads = roads.get(0).getLanes().size();
        // Now compare this with the numebr of lanes in all the roads
        for (Road road : roads){
            int numberOfLanesInThisRoad = road.getLanes().size();
            if (numberOfLanesInThisRoad != numberOfLanesForAllRoads){
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
     * Given a List of Roads, pull out all the individual lanes.
     *
     * @param roads a list of Roads
     */
    private void extractLanes(List<Road> roads) {
        for(Road road : roads) {
            for(Lane lane : road.getLanes()) {
                lanes.add(lane);
            }
        }
    }

    /**
     * Find the Area that represents the corner made between the Roads.
     *
     * @param roads a list of Roads that make the corner
     * @return the area in which the two of these Roads intersect
     */
    private Area findAreaOfCorner(List<Road> roads) {
        // Lanes in the same road should never intersect. So use the union of
        // their shapes. Then find the pairwise overlaps of all the roads,
        // and union all of that.
        // Create a place to store the Areas for each road
        List<Area> roadAreas = new ArrayList<Area>(roads.size());
        for(Road road : roads) {
            Area roadArea = new Area();
            // Find the union of the shapes of the lanes for each road
            for(Lane lane : road.getLanes()) {
                // Add the area from each constituent lane
                roadArea.add(new Area(lane.getShape()));
            }
            roadAreas.add(roadArea);
        }
        // Now we have the Areas for each road, we need to find the union of the
        // pairwise overlaps
        Area strictAreaOfCorner = new Area();
        for(int i = 0; i < roadAreas.size(); i++) {
            // Want to make sure we only do the cases where j < i, i.e. don't do
            // both (i,j) and (j,i).
            for(int j = 0; j < i; j++) {
                // If the ith road and jth road are the duals of each other, there
                // won't be an overlap
                if(roads.get(i).getDual() != roads.get(j)) {
                    // Now add the overlap of roads i and j
                    // Make a copy because intersect is destructive
                    Area overlap = new Area(roadAreas.get(i));
                    overlap.intersect(roadAreas.get(j));
                    strictAreaOfCorner.add(overlap);
                }
            }
        }
        return strictAreaOfCorner;
    }

    /**
     * Determine the points at which each Lane enters or exits the corner
     * and record them. Also record the entry/exit headings and entry/exit Roads.
     */
    private void establishEntryAndExitPoints(Area areaOfCorner) {
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
        this.areaOfCorner = GeomMath.filledArea(areaOfCorner);
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
        if(areaOfCorner.contains(lane.getStartPoint())) {
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
        if(areaOfCorner.contains(lane.getEndPoint())) {
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
     * Calculate the list of points, ordered by angle to the centroid, where
     * Lanes either enter or exit the corner.
     */
    private void calcWayPoints() {
        SortedMap<Double, Point2D> circumferentialPointsByAngle =
                new TreeMap<Double, Point2D>();
        for(Point2D p : exitPoints.values()) {
            circumferentialPointsByAngle.put(GeomMath.angleToPoint(p,centroid),p);
        }
        for(Point2D p : entryPoints.values()) {
            circumferentialPointsByAngle.put(GeomMath.angleToPoint(p,centroid),p);
        }
        for(Point2D p : circumferentialPointsByAngle.values()) {
            points.add(p);
        }
    }

    /**
     * Calculate the list of edges.
     * EDGES OF WHAT
     */
   /* private void calcEdges() {
        // TODO: need to fix this problem.
        PathIterator iter = areaOfCorner.getBounds2D().getPathIterator(null);
        double[] coords = new double[6];

        double px = 0, py = 0;
        Path2D edge = null;

        while(!iter.isDone()) {
            int type = iter.currentSegment(coords);
            switch(type) {
                case PathIterator.SEG_MOVETO:
                    assert edge == null;
                    px = coords[0];
                    py = coords[1];
                    break;
                case PathIterator.SEG_LINETO:
                    edge = new Path2D.Double();
                    edge.moveTo(px, py);
                    edge.lineTo(coords[0], coords[1]);
                    px = coords[0];
                    py = coords[1];
                    edges.add(edge);
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
                default:
                    throw new RuntimeException("RoadCorner::calcEdges(): " +
                            "unknown path iterator type.");
            }
            iter.next();
        }
    }*/

    /**
     * Take the Area formed by joining the circumferential points and add it
     * to the area of the corner.
     */
    private void addWayPointsPath() {
        GeneralPath gp = null;
        for(Point2D p : points) {
            if(gp == null) {
                gp = new GeneralPath();
                gp.moveTo((float)p.getX(),(float)p.getY());
            } else {
                gp.lineTo((float)p.getX(),(float)p.getY());
            }
        }
        gp.closePath();
        areaOfCorner.add(new Area(gp));
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
        return areaOfCorner;
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
