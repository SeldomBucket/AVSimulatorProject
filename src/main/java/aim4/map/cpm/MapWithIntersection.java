package aim4.map.cpm;

import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.connections.Corner;
import aim4.map.SpawnPoint;
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
 * Map with a conventional intersection, four roads intersecting
 * (as in AIM simulation). Class created to test if we can
 * instantiate RoadBasedIntersection.
 */
public class MapWithIntersection implements CPMMap {

    /////////////////////////////////
    // CONSTANTS
    /////////////////////////////////

    /** The length of the no vehicle zone */
    private static final double NO_VEHICLE_ZONE_LENGTH = 28.0;
    // private static final double NO_VEHICLE_ZONE_LENGTH = 10.0;

    /** The position of the data collection line on a lane */
    private static final double DATA_COLLECTION_LINE_POSITION =
            NO_VEHICLE_ZONE_LENGTH;

    /**The initial time*/
    double initTime;
    /**Width of each lane*/
    private double laneWidth;
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
    /** The entrance lane, used to create a SpawnPoint */
    private Lane entranceLane;
    /** The exit lane */
    private Lane exitLane;


    /**
     * Create a map with a conventional intersection: 4 roads
     * (North, South, East, West) intersecting in the centre
     * of the map.
     */
    public MapWithIntersection() {
        laneWidth = 4;
        speedLimit = 25.0;
        initTime = 0.0;

        // Generate the size of the map
        double width = 325;
        double height = 325;
        dimensions = new Rectangle2D.Double(0, 0, width, height);

        // Set size of array for the data collection lines.
        // One on entry and one on exit
        dataCollectionLines = new ArrayList<DataCollectionLine>(2);

        // Create the vertical Roads

        //SOUTH
        Road southBoundRoad = new Road("Southbound Avenue", this);

        // Add a lane to the road
        // Need to find the centre of the lane before creating it
        double x = (width/2) + (laneWidth/2);
        Lane southLane = new LineSegmentLane(x, // x1
                height, // y1
                x, // x2
                0, // y2
                laneWidth, // width
                speedLimit);
        int southLaneId = laneRegistry.register(southLane);
        southLane.setId(southLaneId);
        southBoundRoad.addTheRightMostLane(southLane);
        laneToRoad.put(southLane, southBoundRoad);

        verticalRoads.add(southBoundRoad);

        //NORTH
        Road northBoundRoad = new Road("Northbound Avenue", this);

        // Add a lane to the road
        // Need to find the centre of the lane before creating it
        x = (width/2) - (laneWidth/2);
        Lane northLane = new LineSegmentLane(x, // x1
                0, // y1
                x, // x2
                height, // y2
                laneWidth, // width
                speedLimit);
        int northLaneId = laneRegistry.register(northLane);
        northLane.setId(northLaneId);
        northBoundRoad.addTheRightMostLane(northLane);
        laneToRoad.put(northLane, northBoundRoad);

        verticalRoads.add(northBoundRoad);

        // Create the horizontal Roads
        // WEST
        Road westBoundRoad = new Road("Westbound Avenue", this);

        //Add a lane to the road
        // Need to find the centre of the lane before creating it
        double y = (height/2) + (laneWidth/2);
        Lane westLane = new LineSegmentLane(0, // x1
                y, // y1
                width, // x2
                y, // y2
                laneWidth, // width
                speedLimit);
        int westLaneId = laneRegistry.register(westLane);
        westLane.setId(westLaneId);
        westBoundRoad.addTheRightMostLane(westLane);
        laneToRoad.put(westLane, westBoundRoad);

        horizontalRoads.add(westBoundRoad);

        // EAST
        Road eastBoundRoad = new Road("Eastbound Avenue", this);

        //Add a lane to the road
        // Need to find the centre of the lane before creating it
        y = (height/2) - (laneWidth/2);

        Lane eastLane = new LineSegmentLane(width, // x1
                y, // y1
                0, // x2
                y, // y2
                laneWidth, // width
                speedLimit);
        int eastLaneId = laneRegistry.register(eastLane);
        eastLane.setId(eastLaneId);
        eastBoundRoad.addTheRightMostLane(eastLane);
        laneToRoad.put(eastLane, eastBoundRoad);
        entranceLane = eastLane;

        horizontalRoads.add(eastBoundRoad);

        // store all the roads we've created
        roads = new ArrayList<Road>(horizontalRoads);
        roads.addAll(verticalRoads);
        roads = Collections.unmodifiableList(roads);

        // generate the data collection line
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Entrance on Eastbound",
                        dataCollectionLines.size(),
                        new Point2D.Double((width-20), ((height/2)-(laneWidth/2))),
                        new Point2D.Double(20, (height-laneWidth)),
                        true));

        // Now we have created the roads, we need to create the intersection
        // TODO: CPM We might need to add method back in to take map
        // RoadBasedIntersection intersection1 = new RoadBasedIntersection(roads);

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
        return 0;
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
        outfile.printf("Printing file for CPM simulation\n");
        outfile.printf("VIN,Time,DCLname,vType,startLaneId\n");
        for (DataCollectionLine line : dataCollectionLines) {
            for (int vin : line.getAllVIN()) {
                for(double time : line.getTimes(vin)) {
                    outfile.printf("%d,%.4f,%s,%s,%d\n",
                            vin, time, line.getName(),
                            VinRegistry.getVehicleSpecFromVIN(vin).getName(),
                            VinRegistry.getSpawnPointFromVIN(vin).getLane().getId());
                }
            }
        }

        outfile.close();
    }

    @Override
    public Lane getExitLane() {
        return null;
    }

    @Override
    public List<Corner> getCorners() {
        return null;
    }
}
