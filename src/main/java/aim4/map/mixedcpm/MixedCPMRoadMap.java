package aim4.map.mixedcpm;

import aim4.map.Road;
import aim4.map.RoadMap;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.util.ArrayListRegistry;
import aim4.util.Registry;

import java.awt.geom.Rectangle2D;
import java.util.*;

public abstract class MixedCPMRoadMap implements RoadMap {

    // general
    /**Width of each lane*/
    protected double laneWidth;
    /**Half of the width of each lane*/
    protected double halfLaneWidth;
    /**Speed limit*/
    protected double speedLimit;
    /** The dimensions of the map */
    protected Rectangle2D dimensions;


    // lanes and roads
    /** The lane registry */
    protected Registry<Lane> laneRegistry =
            new ArrayListRegistry<Lane>();
    /** A mapping form lanes to roads they belong */
    protected Map<Lane, Road> laneToRoad = new HashMap<Lane,Road>();
    protected Lane entranceLane;
    /** The exit lanes*/
    protected List<Lane> exitLanes = new ArrayList<Lane>();
    /** The set of roads */
    protected List<Road> roads = new ArrayList<Road>();

    // road connections
    /** The set of corners */
    protected List<Corner> corners = new ArrayList<Corner>();
    /**The set of intersections */
    protected List<SimpleIntersection> intersections = new ArrayList<SimpleIntersection>();

    /** The set of junctions. */
    private List<Junction> junctions = new ArrayList<Junction>();

    /**
     * Constructor for the MixedCPMRoadMap
     * @param laneWidth the standard width of the lanes in this map
     * @param speedLimit the standard speed limit of this map
     */
    public MixedCPMRoadMap(double laneWidth, double speedLimit) {
        this.laneWidth = laneWidth;
        this.halfLaneWidth = laneWidth/2;
        assert speedLimit < 30;
        this.speedLimit = speedLimit;
    }

    /**
     * register a lane in the lane registry
     * @param lane the lane to be registered
     */
    protected void registerLane(Lane lane){
        int laneId = laneRegistry.register(lane);
        lane.setId(laneId);
    }

    /**
     * Creates a road with one lane and adds it to the map
     * @param roadName the name of the road
     * @param x1 x of the centre of the start of the road
     * @param y1 y of the centre of the start of the road
     * @param x2 x of the centre of the end of the road
     * @param y2 y of the centre of the end of the road
     * @return
     */
    protected Road makeRoadWithOneLane(String roadName, double x1,
                                       double y1, double x2, double y2,
                                       double laneWidth){
        // Create the road
        Road road = new Road(roadName, this);
        // Add a lane to the road
        Lane lane = new LineSegmentLane(x1,
                y1,
                x2,
                y2,
                laneWidth,
                speedLimit);
        registerLane(lane);
        // Add lane to road
        road.addTheRightMostLane(lane);
        laneToRoad.put(lane, road);
        this.roads.add(road);

        return road;
    }

    /**
     * Make a corner and store it in the map
     * @param road1 road1 of the corner
     * @param road2 road2 of the corner
     */
    protected void makeCorner(Road road1, Road road2){
        // Put the roads into a list
        List<Road> roadsForCorner = new ArrayList<Road>(2);
        roadsForCorner.add(road1);
        roadsForCorner.add(road2);
        Corner corner = new Corner(roadsForCorner);
        corners.add(corner);
    }

    /**
     * Make a junction and store it in the map - roads must be at 90 degrees
     * @param road1 road1 of the junction
     * @param road2 road2 of the junction
     */
    protected void makeJunction(Road road1, Road road2){
        // Put the roads into a list
        List<Road> roadsForJunction = new ArrayList<Road>(2);
        roadsForJunction.add(road1);
        roadsForJunction.add(road2);
        Junction junction = new Junction(roadsForJunction);
        junctions.add(junction);
        road1.addJunction(junction);
        road2.addJunction(junction);
    }


    /**
     * Make a junction and store it in the map - roads must be at 90 degrees
     * @param road1 road1 of the junction
     * @param road2 road2 of the junction
     * @param road3 road3 of the junction
     */
    protected void makeJunction(Road road1, Road road2, Road road3){
        // Put the roads into a list
        List<Road> roadsForJunction = new ArrayList<Road>(3);
        roadsForJunction.add(road1);
        roadsForJunction.add(road2);
        roadsForJunction.add(road3);
        Junction junction = new Junction(roadsForJunction);
        junctions.add(junction);
        road1.addJunction(junction);
        road2.addJunction(junction);
        road3.addJunction(junction);
    }

    /**
     * Make an intersection and store it in the map - roads must be at 90 degrees
     * @param road1 road1 of the intersection
     * @param road2 road2 of the intersection
     */
    protected void makeSimpleIntersection(Road road1, Road road2){
        // Put the roads into a list
        List<Road> roadsForIntersection = new ArrayList<Road>(2);
        roadsForIntersection.add(road1);
        roadsForIntersection.add(road2);
        SimpleIntersection intersection = new SimpleIntersection(roadsForIntersection);
        intersections.add(intersection);
        road1.addSimpleIntersection(intersection);
        road2.addSimpleIntersection(intersection);
    }

    /**
     * Make an intersection and store it in the map - roads must be at 90 degrees
     * @param road1 road1 of the intersection
     * @param road2 road2 of the intersection
     * @param road3 road3 of the intersection
     */
    protected void makeSimpleIntersection(Road road1, Road road2, Road road3){
        // Put the roads into a list
        List<Road> roadsForIntersection = new ArrayList<Road>(3);
        roadsForIntersection.add(road1);
        roadsForIntersection.add(road2);
        roadsForIntersection.add(road3);
        SimpleIntersection intersection = new SimpleIntersection(roadsForIntersection);
        intersections.add(intersection);
        road1.addSimpleIntersection(intersection);
        road2.addSimpleIntersection(intersection);
        road3.addSimpleIntersection(intersection);
    }

    /**
     * Remove a road and all simple intersections/junctions connected to it from the map
     * @param road road to be removed
     * @return whether the road was removed successfully or not
     */
    public boolean removeRoad(Road road){
        // Find all the roads attached to this road, and delete the corresponding junction
        for (Junction junction : road.getJunctions()){
            for (Road connectedRoad : junction.getRoads()){
                if (!connectedRoad.equals(road)) {
                    connectedRoad.removeJunction(junction);
                }
            }
        }
        this.junctions.removeAll(road.getJunctions());

        // Find all the roads attached to this road, and delete the corresponding intersection
        for (SimpleIntersection intersection : road.getIntersections()){
            for (Road connectedRoad : intersection.getRoads()){
                if (!connectedRoad.equals(road)) {
                    connectedRoad.removeSimpleIntersection(intersection);
                }
            }
        }
        this.intersections.removeAll(road.getIntersections());

        for (Lane lane : road.getLanes()) {
            this.laneToRoad.remove(lane);
        }

        this.roads.remove(road);
        return false;
    }

    // GETTERS

    public List<Road> getRoads() {
        HashSet<Road> set = new HashSet<>();
        set.addAll(this.roads);
        // TODO ED When have automated section of car park, add here
        return new ArrayList<>(set);
    }

    public Rectangle2D getDimensions() {
        return dimensions;
    }

    public double getMaximumSpeedLimit() {
        return speedLimit;
    }

    public Registry<Lane> getLaneRegistry() {
        return laneRegistry;
    }

    public double getLaneWidth(){
        return laneWidth;
    }

    public Road getRoad(Lane lane) {
        return laneToRoad.get(lane);
    }

    public Road getRoad(int laneID) {
        return laneToRoad.get(laneRegistry.get(laneID));
    }

    public Road getRoadByName(String roadName) {
        for (Road road: this.getRoads()){
            if (road.getName().equals(roadName)){
                return road;
            }
        }
        return null;
    }

    public List<Lane> getExitLanes() {
        return exitLanes;
    }

    public List<Corner> getCorners() {
        return corners;
    }

    public List<Junction> getJunctions() {
        return junctions;
    }

    public List<SimpleIntersection> getIntersections() {
        return intersections;
    }


}
