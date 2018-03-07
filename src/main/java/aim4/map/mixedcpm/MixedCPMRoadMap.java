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
import aim4.vehicle.mixedcpm.MixedCPMBasicAutoVehicle;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MixedCPMRoadMap implements RoadMap {

    /////////////////////////////////
    // CONSTANTS
    /////////////////////////////////

    /** The length of the no vehicle zone */
    protected static final double NO_VEHICLE_ZONE_LENGTH = 28.0;

    /** The length of the map border, used for
     * space between map edge and elements, distance
     * of DCL from edge etc.
     * */
    protected static final double BORDER = 28.0;

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
    /** The set of vertical roads */
    protected List<Road> verticalRoads = new ArrayList<Road>();
    /** The set of horizontal roads */
    protected List<Road> horizontalRoads = new ArrayList<Road>();
    /** The set of roads */
    protected List<Road> roads;

    // road connections
    /** The set of corners */
    protected List<Corner> corners = new ArrayList<Corner>();
    /** The set of junctions. */
    protected List<Junction> junctions = new ArrayList<Junction>();
    /**The set of intersections */
    protected List<SimpleIntersection> intersections = new ArrayList<SimpleIntersection>();

    public MixedCPMRoadMap(double laneWidth, double speedLimit) {
        this.laneWidth = laneWidth;
        this.halfLaneWidth = laneWidth/2;
        assert speedLimit < 30;
        this.speedLimit = speedLimit;
    }

    protected void registerLane(Lane lane){
        int laneId = laneRegistry.register(lane);
        lane.setId(laneId);
    }


    protected Road makeRoadWithOneLane(String roadName, double x1,
                                       double y1, double x2, double y2){
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

    protected void makeCorner(Road road1, Road road2){
        // Put the roads into a list
        List<Road> roadsForCorner = new ArrayList<Road>(2);
        roadsForCorner.add(road1);
        roadsForCorner.add(road2);
        Corner corner = new Corner(roadsForCorner);
        corners.add(corner);
    }

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

    public boolean removeRoad(Road road){
        // Find all the roads attached to this road, and delete the corresponding junction
        for (Junction junction : road.getJunctions()){
            for (Road junctionRoad : junction.getRoads()){
                junctionRoad.removeJunction(junction);
            }
        }
        this.junctions.removeAll(road.getJunctions());

        // Find all the roads attached to this road, and delete the corresponding intersection
        for (SimpleIntersection intersection : road.getIntersections()){
            for (Road junctionRoad : intersection.getRoads()){
                junctionRoad.removeSimpleIntersection(intersection);
            }
        }
        this.intersections.removeAll(road.getIntersections());

        for (Lane lane : road.getLanes()) {
            this.laneToRoad.remove(lane);
        }

        this.roads.remove(road);
        return false;
    }

    public List<Road> getRoads() {
        return roads;
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
        for (Road road: roads){
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
