package aim4.map.cpm.testmaps;

import aim4.im.RoadBasedIntersection;
import aim4.map.*;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.cpm.CPMMap;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.util.ArrayListRegistry;
import aim4.util.Registry;
import aim4.vehicle.VinRegistry;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

/**
 * Map with a T-Junction.
 * Class created to test if we can instantiate RoadBasedIntersection.
 */
public class CPMMapWithTJunction implements CPMMap {

    /////////////////////////////////
    // CONSTANTS
    /////////////////////////////////

    /** The length of the no vehicle zone */
    private static final double NO_VEHICLE_ZONE_LENGTH = 28.0;
    // private static final double NO_VEHICLE_ZONE_LENGTH = 10.0;

    /** The length of the map border, used for
     * space between map edge and elements, distance
     * of DCL from edge etc.
     * */
    public static final double BORDER = 28.0;

    /** The position of the data collection line on a lane */
    private static final double DATA_COLLECTION_LINE_POSITION =
            NO_VEHICLE_ZONE_LENGTH;

    /**The initial time*/
    double initTime;
    /**Width of each lane*/
    private double laneWidth;
    /** Half of the lane width*/
    private double halfLaneWidth;
    /**Speed limit*/
    private double speedLimit;
    /** The dimensions of the map */
    private Rectangle2D dimensions;
    /** The data collection lines */
    private List<DataCollectionLine> dataCollectionLines;
    /** The spawn points */
    private List<SpawnPoint> spawnPoints;
    /** The horizontal spawn points */
    private List<SpawnPoint> horizontalSpawnPoints;
    /** The vertical spawn points */
    private List<SpawnPoint> verticalSpawnPoints;
    /** The lane registry */
    private Registry<Lane> laneRegistry =
            new ArrayListRegistry<Lane>();
    /** A mapping form lanes to roads they belong */
    private Map<Lane,Road> laneToRoad = new HashMap<Lane,Road>();
    /** The set of vertical roads */
    private List<Road> verticalRoads = new ArrayList<Road>();
    /** The set of horizontal roads */
    private List<Road> horizontalRoads = new ArrayList<Road>();
    /** The set of roads */
    private List<Road> roads;
    /** The entrance lane, used to create a SpawnPoint*/
    private Lane entranceLane;
    /** The exit lane*/
    private List<Lane> exitLanes = new ArrayList<Lane>();
    /** The set of junctions. */
    private List<Junction> junctions = new ArrayList<Junction>();

    /**
     * Create a map with a T-Junction.
     */
    public CPMMapWithTJunction(int laneWidth, double speedLimit,
                               double initTime, double width,
                               double height) {
        this.laneWidth = laneWidth;
        this.halfLaneWidth = laneWidth/2;
        this.speedLimit = speedLimit;
        this.initTime = initTime;
        this.dimensions = new Rectangle2D.Double(0, 0, width, height);

        // Set size of array for the data collection lines.
        // One on entry and one on each exit
        dataCollectionLines = new ArrayList<DataCollectionLine>(3);

        // Create the vertical Roads

        //NORTH
        Road northBoundRoad = new Road("Northbound Avenue", this);

        // Add a lane to the road
        // Need to find the centre of the lane before creating it
        double x1 = width/2;
        double y1 = (height/2) - halfLaneWidth;
        double x2 = x1;
        double y2 = height;
        Lane northLane = new LineSegmentLane(x1,
                y1,
                x2,
                y2,
                laneWidth, // width
                speedLimit);
        int northLaneId = laneRegistry.register(northLane);
        northLane.setId(northLaneId);
        northBoundRoad.addTheRightMostLane(northLane);
        laneToRoad.put(northLane, northBoundRoad);
        exitLanes.add(northLane);

        entranceLane = northLane;

        verticalRoads.add(northBoundRoad);

        // Create the horizontal Roads
        // EAST
        Road eastBoundRoad = new Road("Eastbound Avenue", this);

        // Add a lane to the road
        // Need to find the centre of the lane before creating it
        x1 = 0;
        y1 = height/2;
        x2 = width;
        y2 = y1;
        Lane eastLane = new LineSegmentLane(x1, // x1
                y1, // y1
                x2, // x2
                y2, // y2
                laneWidth, // width
                speedLimit);
        int eastLaneId = laneRegistry.register(eastLane);
        eastLane.setId(eastLaneId);
        eastBoundRoad.addTheRightMostLane(eastLane);
        laneToRoad.put(eastLane, eastBoundRoad);
        entranceLane = eastLane;
        exitLanes.add(eastLane);

        horizontalRoads.add(eastBoundRoad);

        roads = new ArrayList<Road>(horizontalRoads);
        roads.addAll(verticalRoads);
        roads = Collections.unmodifiableList(roads);

        // generate the data collection lines
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Entrance on Eastbound",
                        dataCollectionLines.size(),
                        new Point2D.Double(BORDER, (height/2)+halfLaneWidth),
                        new Point2D.Double(BORDER, (height/2)-halfLaneWidth),
                        true));
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Exit on Northbound",
                        dataCollectionLines.size(),
                        new Point2D.Double(((width/2)-halfLaneWidth), (height-BORDER)),
                        new Point2D.Double(((width/2)+halfLaneWidth), (height-BORDER)),
                        true));
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Exit on Eastbound",
                        dataCollectionLines.size(),
                        new Point2D.Double((width-BORDER), (height/2)+halfLaneWidth),
                        new Point2D.Double((width-BORDER), (height/2)-halfLaneWidth),
                        true));

        // Now we have created the roads, we need to create the Junction
        List<Road> roadsForJunction = new ArrayList<Road>(3);
        roadsForJunction.add(eastBoundRoad);
        roadsForJunction.add(northBoundRoad);
        Junction junction = new Junction(roadsForJunction);
        junctions.add(junction);


        initializeSpawnPoints(initTime);
    }

    /**
     * Initialize spawn points.
     *
     * @param initTime  the initial time
     */
    private void initializeSpawnPoints(double initTime) {
        spawnPoints = new ArrayList<SpawnPoint>(1);
        horizontalSpawnPoints = new ArrayList<SpawnPoint>(1);
        horizontalSpawnPoints.add(makeSpawnPoint(initTime, entranceLane));

        spawnPoints.addAll(horizontalSpawnPoints);
    }

    /**
     * Make spawn points.
     *
     * @param initTime  the initial time
     * @param lane      the lane
     * @return the spawn point
     */
    private SpawnPoint makeSpawnPoint(double initTime, Lane lane) {
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

    @Override
    public List<Road> getRoads() {
        return roads;
    }

    @Override
    public Rectangle2D getDimensions() {
        return dimensions;
    }

    @Override
    public double getMaximumSpeedLimit() {
        return speedLimit;
    }

    @Override
    public Registry<Lane> getLaneRegistry() {
        return laneRegistry;
    }

    @Override
    public Road getRoad(Lane lane) {
        return laneToRoad.get(lane);
    }

    @Override
    public Road getRoad(int laneID) {
        return laneToRoad.get(laneRegistry.get(laneID));
    }

    /**
     * Get the road by name.
     *
     * @return road, or null if the road doesn't exist.
     * */
    public Road getRoadByName(String roadName) {
        for (Road road: roads){
            if (road.getName() == roadName){
                return road;
            }
        }

        return null;
    }

    @Override
    public List<DataCollectionLine> getDataCollectionLines() {
        return dataCollectionLines;
    }

    @Override
    public List<SpawnPoint> getSpawnPoints() {
        return spawnPoints;
    }

    @Override
    public void printDataCollectionLinesData(String outFileName) {
        PrintStream outfile = null;
        try {
            outfile = new PrintStream(outFileName);
        } catch (FileNotFoundException e) {
            System.err.printf("Cannot open file %s\n", outFileName);
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

    @Override
    public List<Lane> getExitLanes() {
        return exitLanes;
    }

    @Override
    public List<Corner> getCorners() {
        return null;
    }

    @Override
    public List<Junction> getJunctions() {
        return junctions;
    }

    @Override
    public List<SimpleIntersection> getIntersections() {
        return null;
    }
}
