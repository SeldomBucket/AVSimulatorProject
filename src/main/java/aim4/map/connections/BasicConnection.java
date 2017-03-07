package aim4.map.connections;

import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.map.track.WayPoint;
import aim4.util.GeomMath;

import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * The base class to connect roads together. Used to create Corners
 * and Junctions.
 */
public abstract class BasicConnection implements RoadConnection {

    /////////////////////////////////
    // PROTECTED FIELDS
    /////////////////////////////////

    // area

    /**
     * The space governed by this connection.
     */
    protected Area areaOfConnection;

    /**
     * The centroid of this connection.
     */
    protected Point2D centroid;

    // road

    /** The roads which meet to make this connection. */
    protected List<Road> roads = new ArrayList<Road>();

    /** The entry roads incidents to this connection. */
    protected List<Road> entryRoads = new ArrayList<Road>();

    /** The exit roads incidents to this connection. */
    protected List<Road> exitRoads = new ArrayList<Road>();

    // lanes

    /** The lanes which meet to make this connection. */
    protected List<Lane> lanes = new ArrayList<Lane>();

    // points

    /**
     * A list of the coordinates where lanes enter or exit the connection,
     * ordered by angle from the centroid.
     */
    protected List<Point2D> points = new ArrayList<Point2D>();

    /**
     * A map from lanes to the coordinates at which those lanes enter the
     * connection.
     */
    protected Map<Lane,WayPoint> entryPoints = new LinkedHashMap<Lane,WayPoint>();

    /**
     * A map from lanes to the coordinates at which those lanes exit the
     * connection.
     */
    protected Map<Lane,WayPoint> exitPoints = new LinkedHashMap<Lane,WayPoint>();

    // headings

    /**
     * A map from Lanes to the headings, in radians, of those Lanes at the
     * point at which they enter the space governed by this connection.
     */
    protected Map<Lane,Double> entryHeadings = new HashMap<Lane,Double>();

    /**
     * A map from Lanes to the headings, in radians, of those Lanes at the
     * point at which they exit the space governed by this connection.
     */
    protected Map<Lane,Double> exitHeadings = new HashMap<Lane,Double>();

    /////////////////////////////////
    // CLASS CONSTRUCTORS
    /////////////////////////////////

    /**
     * Basic class constructor.
     * Takes the Roads which meet to make this connection.
     *
     * @param roads the roads involved in this connection.
     */
    public BasicConnection(List<Road> roads) {
        this.roads = roads;
        // Get the list of Lanes we are using.
        extractLanes(roads);
        // Find the area of the connection.
        this.areaOfConnection = findAreaOfConnection(roads);
        // Find the centroid of the corner
        centroid = GeomMath.polygonalShapeCentroid(areaOfConnection);
    }

    /////////////////////////////////
    // PROTECTED METHODS
    /////////////////////////////////

    /**
     * Given a List of Roads, pull out all the individual lanes.
     *
     * @param roads a list of Roads
     */
    protected void extractLanes(List<Road> roads) {
        for(Road road : roads) {
            for(Lane lane : road.getLanes()) {
                lanes.add(lane);
            }
        }
    }

    /**
     * Check if all roads have the same number of lanes.
     * @param roads the roads to check.
     *
     * @return boolean - true if all roads have the same number of lanes.
     */
    protected boolean hasOneLane(List<Road> roads){

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
     * Check if the roads given meet at 90 degrees.
     * @param roads
     * @return true if roads meet at ninety degrees.
     */
    protected boolean atNinetyDegrees(List<Road> roads){
        // The 2 roads must meet at 90 degrees.
        if (roads.size() == 2){
            LineSegmentLane laneFromRoad1 = (LineSegmentLane) roads.get(0).getLanes().get(0);
            LineSegmentLane laneFromRoad2 = (LineSegmentLane) roads.get(1).getLanes().get(0);
            Point2D intersectionPoint = laneFromRoad1.intersectionPoint(laneFromRoad2.getLine());
            double angleInRadians = GeomMath.angleBetweenTwoPointsWithFixedPoint(laneFromRoad1.getStartPoint(),
                    laneFromRoad2.getStartPoint(),
                    intersectionPoint);
            double angleInDegrees = Math.toDegrees(angleInRadians);
            if (Math.abs(angleInDegrees) == 90.0 || Math.abs(angleInDegrees) == 270.0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find the Area that represents the connection of Roads.
     *
     * @param roads a list of Roads that make the connection
     * @return the area which represents the area of connection.
     */
    protected Area findAreaOfConnection(List<Road> roads) {
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
        Area strictAreaOfConnection = new Area();
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
                    strictAreaOfConnection.add(overlap);
                }
            }
        }
        return strictAreaOfConnection;
    }

    /**
     * Calculate the list of points, ordered by angle to the centroid, where
     * Lanes either enter or exit the corner.
     */
    protected void calcWayPoints() {
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
     * Take the Area formed by joining the circumferential points and add it
     * to the area of the corner.
     */
    protected void addWayPointsPath() {
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
        areaOfConnection.add(new Area(gp));
    }

    /**
     * Does a lane start on or inside the perimeter.
     *
     * @param lane The lane we want to check if it's start point is on or inside the perimeter.
     * @param perimeterSegments The perimeter segments of the area of the connection.
     * @return true if the lanes start point is on or inside the perimeter of the connection.
     */
    protected boolean doesLaneStartInPerimeter(Lane lane, List<Line2D> perimeterSegments){
        for (Line2D segment : perimeterSegments){
            // if the start point is on the perimeter
            if (isPointOnPerimeterSegment(lane.getStartPoint(), segment)){
                return true;
            }
        }
        // if the start point is inside the connection
        if(areaOfConnection.contains(lane.getStartPoint())) {
            return true;
        }
        return false;
    }

    /**
     * Does a lane end on or inside the perimeter.
     *
     * @param lane The lane we want to check if it's end point is on or inside the perimeter.
     * @param perimeterSegments The perimeter segments of the area of the connection.
     * @return true if the lanes end point is on or inside the perimeter of the connection.
     */
    protected boolean doesLaneEndInPerimeter(Lane lane, List<Line2D> perimeterSegments){
        for (Line2D segment : perimeterSegments){
            // if the end point is on the perimeter
            if (isPointOnPerimeterSegment(lane.getEndPoint(), segment)){
                return true;
            }
        }
        // if the end point is inside the connection
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
    protected boolean isPointOnPerimeterSegment(Point2D point, Line2D segment){
        if (segment.ptSegDist(point) == 0.0){
            return true;
        }
        return false;
    }

    /**
     * Establish a new entry point to the connection.
     * @param road The road which enters the connection.
     * @param lane The lane we are adding an entry point for, which is part of the given road.
     * @param entryPoint The entry point we want to record for this lane.
     */
    protected void establishAsEntryPoint(Road road, Lane lane, Point2D entryPoint){
        entryPoints.put(lane, new WayPoint(entryPoint));
        // Here we are assuming that the heading of the lane is the same throughout.
        entryHeadings.put(lane, lane.getInitialHeading());
        if (!entryRoads.contains(road)){
            entryRoads.add(road);
        }
    }

    /**
     * Establish a new exit point to the connection.
     * @param road The road which exits the connection.
     * @param lane The lane we are adding an exit point for, which is part of the given road.
     * @param exitPoint The exit point we want to record for this lane.
     */
    protected void establishAsExitPoint(Road road, Lane lane, Point2D exitPoint){
        exitPoints.put(lane, new WayPoint(exitPoint));
        // Here we are assuming that the heading of the lane is the same throughout.
        exitHeadings.put(lane, lane.getInitialHeading());
        if (!exitRoads.contains(road)){
            exitRoads.add(road);
        }

    }

    /////////////////////////////////
    // ABSTRACT METHODS
    /////////////////////////////////

    /**
     * Ensure that the roads given can be used to make the connection.
     * Throw an exception if the given roads are invalid.
     * @param roads
     */
    protected abstract void validate(List<Road> roads);

    /**
     * Determine the points at which each Lane enters or exits the area
     * of the connections and record them. Also record the entry/exit
     * headings and entry/exit Roads.
     */
    protected abstract void establishEntryAndExitPoints(Area areaOfCorner);

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

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
}
