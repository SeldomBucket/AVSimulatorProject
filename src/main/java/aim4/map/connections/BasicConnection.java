package aim4.map.connections;

import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.track.WayPoint;
import aim4.util.GeomMath;

import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
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
}
