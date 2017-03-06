package aim4.map.cpm;

import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.SpawnPoint;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.lane.Lane;
import aim4.util.ArrayListRegistry;
import aim4.util.Registry;
import aim4.vehicle.VinRegistry;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The base class for all CPM Maps.
 */
public abstract class CPMBasicMap implements CPMMap{
    /////////////////////////////////
    // CONSTANTS
    /////////////////////////////////

    /** The length of the no vehicle zone */
    protected static final double NO_VEHICLE_ZONE_LENGTH = 28.0;
    // private static final double NO_VEHICLE_ZONE_LENGTH = 10.0;

    /** The length of the map border, used for
     * space between map edge and elements, distance
     * of DCL from edge etc.
     * */
    protected static final double BORDER = 28.0;

    /** The position of the data collection line on a lane */
    protected static final double DATA_COLLECTION_LINE_POSITION =
            NO_VEHICLE_ZONE_LENGTH;

    // general
    /**The initial time*/
    protected double initTime;
    /**Width of each lane*/
    protected double laneWidth;
    /**Half of the width of each lane*/
    protected double halfLaneWidth;
    /**Speed limit*/
    protected double speedLimit;
    /** The dimensions of the map */
    protected Rectangle2D dimensions;
    /** The data collection lines */
    protected List<DataCollectionLine> dataCollectionLines;

    // spawn points
    /** The spawn points */
    protected List<SpawnPoint> spawnPoints;
    /** The horizontal spawn points */
    protected List<SpawnPoint> horizontalSpawnPoints;
    /** The vertical spawn points */
    protected List<SpawnPoint> verticalSpawnPoints;

    // lanes and roads
    /** The lane registry */
    protected Registry<Lane> laneRegistry =
            new ArrayListRegistry<Lane>();
    /** A mapping form lanes to roads they belong */
    protected Map<Lane,Road> laneToRoad = new HashMap<Lane,Road>();
    /** The entrance lane, used to create a SpawnPoint*/
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

    public CPMBasicMap(int laneWidth, double speedLimit,
                       double initTime, double width,
                       double height) {

        this.laneWidth = laneWidth;
        this.halfLaneWidth = laneWidth/2;
        this.speedLimit = speedLimit;
        this.initTime = initTime;
        this.dimensions = new Rectangle2D.Double(0, 0, width, height);

    }

    /**
     * Initialize spawn points.
     *
     * @param initTime  the initial time
     */
    protected void initializeSpawnPoints(double initTime) {
        spawnPoints = new ArrayList<SpawnPoint>(1);
        horizontalSpawnPoints = new ArrayList<SpawnPoint>(1);
        horizontalSpawnPoints.add(makeSpawnPoint(initTime, entranceLane));

        spawnPoints.addAll(horizontalSpawnPoints);
    }

    /**
     * Make the spawn point.
     *
     * @param initTime  the initial time
     * @param lane      the lane
     * @return the spawn point
     */
    protected SpawnPoint makeSpawnPoint(double initTime, Lane lane) {
        double startDistance = 0.0;
        double normalizedStartDistance = lane.normalizedDistance(startDistance);
        Point2D pos = lane.getPointAtNormalizedDistance(normalizedStartDistance);
        double heading = lane.getInitialHeading();
        double steeringAngle = 0.0;
        double acceleration = 0.0;
        double d = lane.normalizedDistance(startDistance + NO_VEHICLE_ZONE_LENGTH);
        Rectangle2D noVehicleZone =
                lane.getShape(normalizedStartDistance, d).getBounds2D();

        return new SpawnPoint(initTime, pos, heading, steeringAngle, acceleration,
                lane, noVehicleZone);
    }

    protected void makeCorner(Road road1, Road road2){
        // Put the roads into a list
        List<Road> roadsForCorner = new ArrayList<Road>(2);
        roadsForCorner.add(road1);
        roadsForCorner.add(road2);
        Corner corner = new Corner(roadsForCorner);
        corners.add(corner);
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

    public Road getRoad(Lane lane) {
        return laneToRoad.get(lane);
    }

    public Road getRoad(int laneID) {
        return laneToRoad.get(laneRegistry.get(laneID));
    }

    public Road getRoadByName(String roadName) {
        for (Road road: roads){
            if (road.getName() == roadName){
                return road;
            }
        }
        return null;
    }

    public List<DataCollectionLine> getDataCollectionLines() {
        return dataCollectionLines;
    }

    public List<SpawnPoint> getSpawnPoints() {
        return spawnPoints;
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

    public void printDataCollectionLinesData(String outFileName) {
        PrintStream outfile = null;
        try {
            outfile = new PrintStream(outFileName);
        } catch (FileNotFoundException e) {
            System.err.printf("Cannot open file %s%n", outFileName);
            return;
        }
        // TODO: sort by time and LineId and VIN
        outfile.printf("Printing file for CPM simulation%n");
        outfile.printf("VIN,Time,DCLname,vType,startLaneId%n");
        for (DataCollectionLine line : dataCollectionLines) {
            for (int vin : line.getAllVIN()) {
                for(double time : line.getTimes(vin)) {
                    outfile.printf("%d,%.4f,%s,%s,%d%n",
                            vin, time, line.getName(),
                            VinRegistry.getVehicleSpecFromVIN(vin).getName(),
                            VinRegistry.getSpawnPointFromVIN(vin).getLane().getId());
                }
            }
        }

        outfile.close();
    }
}
