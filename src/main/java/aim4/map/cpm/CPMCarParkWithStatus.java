package aim4.map.cpm;

import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.cpm.CPMBasicMap;
import aim4.map.cpm.parking.SensoredLine;
import aim4.map.cpm.parking.StatusMonitor;
import aim4.map.cpm.parking.ParkingArea;
import aim4.vehicle.VinRegistry;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A car park map with a parking area.
 */
public class CPMCarParkWithStatus extends CPMBasicMap {
    // TODO CPM Decide if this should extend CPMBasicMap or CPMMapCarPark

    /** The number of parking lanes. */
    private int numberOfParkingLanes;
    /** The length of the parking lanes used for parking. */
    private double parkingLength;
    /** The length of the parking lanes used for access. */
    private double accessLength;
    /** The parking area. */
    private ParkingArea parkingArea;
    /** The status monitor recording the status of this car park. */
    private StatusMonitor statusMonitor;
    /** A list of sensored lines used by the StatusMonitor. */
    private List<SensoredLine> sensoredLines;
    /** The exit data collection line */
    private CPMExitDataCollectionLine exitDataCollectionLine;

    public CPMCarParkWithStatus(double laneWidth, double speedLimit, double initTime,
                         int numberOfParkingLanes, double parkingLength,
                         double accessLength) {
        super(laneWidth, speedLimit, initTime);
        this.numberOfParkingLanes = numberOfParkingLanes;
        this.parkingLength = parkingLength;
        this.accessLength = accessLength;

        // Calculate the map dimensions
        double mapWidth = (BORDER*2) // The border used to pad the map
                + (laneWidth*2) // The 2 vertical roads either side of the parking area
                + (2*accessLength) // The length of the parking lane used for access (either side)
                + parkingLength; // The length of the parking lanes used for parking
        double mapHeight = (BORDER*2) // The border used to pad the map
                + laneWidth // The horizontal road running across the top of the parking area
                + (laneWidth*numberOfParkingLanes); // The number of horizontal parking lanes
        this.dimensions = new Rectangle2D.Double(0, 0, mapWidth, mapHeight);

        // Calculate the start point for the parking area
        double x = BORDER;
        double y = dimensions.getMaxY() - BORDER - laneWidth;
        Point2D startPoint = new Point2D.Double(x, y);

        // Create the parking area
        this.parkingArea = new ParkingArea(startPoint, this, numberOfParkingLanes,
                parkingLength, laneWidth, accessLength);

        // Create the StatusMonitor
        this.statusMonitor = new StatusMonitor(parkingArea);

        // Add all roads/lanes from parking area to the map's records
        for (Road road : parkingArea.getRoads()){
            horizontalRoads.add(road);
            registerLane(road.getOnlyLane());
        }

        // Create the vertical Roads

        //SOUTH - ENTERS PARKING
        double x1 = BORDER + halfLaneWidth;
        double y1 = mapHeight - BORDER;
        double x2 = x1;
        double y2 = BORDER;
        Road southBoundRoad = createRoadWithOneLane("Southbound Road", x1, y1, x2, y2);
        verticalRoads.add(southBoundRoad);

        //NORTH - LEAVES PARKING
        x1 = mapWidth - BORDER - halfLaneWidth;
        y1 = BORDER;
        x2 = x1;
        y2 = mapHeight - BORDER;
        Road northBoundRoad = createRoadWithOneLane("Northbound Road", x1, y1, x2, y2);
        verticalRoads.add(northBoundRoad);

        // Create the horizontal Roads
        // WEST - EXITS CAR PARK
        x1 = mapWidth - BORDER;
        y1 = mapHeight - BORDER - halfLaneWidth;
        x2 = 0;
        y2 = y1;
        Road westBoundRoad = createRoadWithOneLane("Westbound Avenue", x1, y1, x2, y2);
        horizontalRoads.add(westBoundRoad);

        // EAST - ENTERS CAR PARK
        x1 = 0;
        y1 = mapHeight - BORDER - laneWidth - halfLaneWidth;
        x2 = BORDER + laneWidth;
        y2 = y1;
        Road eastBoundRoad = createRoadWithOneLane("Eastbound Avenue", x1, y1, x2, y2);
        horizontalRoads.add(eastBoundRoad);

        // Record all roads
        roads = new ArrayList<Road>(horizontalRoads);
        roads.addAll(verticalRoads);
        roads = Collections.unmodifiableList(roads);

        // Establish lanes that enter and exit the map
        entranceLane = eastBoundRoad.getOnlyLane();
        exitLanes.add(westBoundRoad.getOnlyLane());

        // Connect roads surrounding the parking area
        makeCorner(northBoundRoad, westBoundRoad);
        makeJunction(westBoundRoad, southBoundRoad);

        // Connect roads in the parking area with the roads surrounding it
        List<Road> roadsInParkingArea = parkingArea.getRoads();
        // Road entryRoad = eastBoundRoad;
        Road firstParkingRoad = parkingArea.getEntryRoad();
        if (roadsInParkingArea.size() == 1) {
            makeJunction(eastBoundRoad, southBoundRoad, firstParkingRoad);
            makeCorner(firstParkingRoad, northBoundRoad);
        } else {
            // Deal with entry road and first parking road
            makeSimpleIntersection(eastBoundRoad, southBoundRoad, firstParkingRoad);
            makeJunction(firstParkingRoad, northBoundRoad);

            // Deal with exit road
            Road lastParkingRoad = parkingArea.getLastRoad();
            makeCorner(lastParkingRoad, southBoundRoad);
            makeCorner(lastParkingRoad, northBoundRoad);

            // Deal with the roads inbetween
            roadsInParkingArea.remove(firstParkingRoad);
            roadsInParkingArea.remove(lastParkingRoad);
            for (Road road : roadsInParkingArea){
                makeJunction(road, southBoundRoad);
                makeJunction(road, northBoundRoad);
            }
        }

        // Set size of array for the data collection lines.
        // One on entry and one on exit
        dataCollectionLines = new ArrayList<DataCollectionLine>(2);
        // Create data collection lines
        x1 = entranceLane.getStartPoint().getX() + BORDER;
        y1 = entranceLane.getStartPoint().getY() + halfLaneWidth;
        x2 = x1;
        y2 = y1 - laneWidth;
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Car Park Entrance",
                        dataCollectionLines.size(),
                        new Point2D.Double(x1, y1),
                        new Point2D.Double(x2, y2),
                        true));

        x1 = exitLanes.get(0).getEndPoint().getX() + BORDER;
        y1 = exitLanes.get(0).getEndPoint().getY() + halfLaneWidth;
        x2 = x1;
        y2 = y1 - laneWidth;
        exitDataCollectionLine =
                new CPMExitDataCollectionLine(
                        "Car Park Exit",
                        dataCollectionLines.size(),
                        new Point2D.Double(x1, y1),
                        new Point2D.Double(x2, y2),
                        true);
        dataCollectionLines.add(exitDataCollectionLine);
        // Set size of array for the sensored lines.
        // One on entry, one on reentry and one on exit
        sensoredLines = new ArrayList<SensoredLine>(3);

        // Create the sensored lines
        // CAR PARK ENTRY
        /*x1 = parkingArea.getStartPoint().getX();
        y1 = parkingArea.getStartPoint().getY();
        x2 = x1;
        y2 = y1 - laneWidth;*/
        // x1 = 0.5;
        x1 = BORDER/2;
        y1 = parkingArea.getStartPoint().getY();
        x2 = x1;
        y2 = y1 - laneWidth;
        sensoredLines.add(
                new SensoredLine(
                        "Entry sensor line",
                        sensoredLines.size(),
                        SensoredLine.SensoredLineType.ENTRY,
                        new Point2D.Double(x1, y1),
                        new Point2D.Double(x2, y2)));

        // CAR PARK REENTRY
        x1 = parkingArea.getStartPoint().getX();
        y1 = parkingArea.getStartPoint().getY();
        x2 = x1 + laneWidth;
        y2 = y1;
        sensoredLines.add(
                new SensoredLine(
                        "Re-entry sensor line",
                        sensoredLines.size(),
                        SensoredLine.SensoredLineType.REENTRY,
                        new Point2D.Double(x1, y1),
                        new Point2D.Double(x2, y2)));

        // CAR PARK EXIT
        x1 = 0.5;
        y1 = parkingArea.getStartPoint().getY();
        x2 = x1;
        y2 = y1 + laneWidth;
        sensoredLines.add(
                new SensoredLine(
                        "Exit sensor line",
                        sensoredLines.size(),
                        SensoredLine.SensoredLineType.EXIT,
                        new Point2D.Double(x1, y1),
                        new Point2D.Double(x2, y2)));

        // Initialise the spawn point
        initializeSpawnPoints(initTime);
    }

    public ParkingArea getParkingArea(){
        return parkingArea;
    }

    public StatusMonitor getStatusMonitor() { return statusMonitor; }

    public List<SensoredLine> getSensoredLines() { return sensoredLines; }

    public CPMExitDataCollectionLine getExitDataCollectionLine() { return exitDataCollectionLine; }

    @Override
    public void printDataCollectionLinesData(String outFileName) {
        PrintStream outfile = null;
        try {
            outfile = new PrintStream(outFileName);
        } catch (FileNotFoundException e) {
            System.err.printf("Cannot open file %s%n", outFileName);
            return;
        }
        // get the data collection time for entry
        dataCollectionLines.remove(exitDataCollectionLine);
        assert dataCollectionLines.size() == 1;
        DataCollectionLine entryDataCollectionLine = dataCollectionLines.get(0);

        outfile.printf("Printing file for CPM simulation%n");
        outfile.printf("VIN,VehicleType,EntryTime,ExitTime,ParkingTime,TimeToRetrieve,EstimatedDistanceTravelled,NumberOfReEntries%n");

        for (int vin : exitDataCollectionLine.getAllVIN()) {
            for(double time : exitDataCollectionLine.getTimes(vin)) {
                outfile.printf("%d,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%d",
                        vin,
                        VinRegistry.getVehicleSpecFromVIN(vin).getName(),
                        entryDataCollectionLine.getTimes(vin).get(0),
                        time,
                        exitDataCollectionLine.getParkingTime(vin),
                        calculateTimeToRetrieve(entryDataCollectionLine, vin, time),
                        exitDataCollectionLine.getEstimatedDistanceTravelled(vin),
                        exitDataCollectionLine.getNumberOfReEntries(vin)
                        );
            }
        }
        // TODO CPM move this to statscreen
        outfile.print("Number of denied entries: " + statusMonitor.getNumberOfDeniedEntries() + "%n");
        outfile.print("Number of allowed entries: " + statusMonitor.getNumberOfAllowedEntries() + "%n");
        outfile.print("Most number of vehicles in car park: " + statusMonitor.getMostNumberOfVehicles() + "%n");

        outfile.close();
    }

    private Double calculateTimeToRetrieve(DataCollectionLine entryDataCollectionLine, int vin, double exitTime){
        double entryTime = entryDataCollectionLine.getTimes(vin).get(0); // TODO CPM why is it a list, will this give us the right thing?
        double parkingTime = exitDataCollectionLine.getParkingTime(vin);
        double retrievalTime = entryTime + parkingTime;
        double timeToRetrieve = exitTime - retrievalTime;
        return timeToRetrieve;
    }
}
