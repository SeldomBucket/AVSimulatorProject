package aim4.map.cpm;

import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.cpm.components.CPMExitDataCollectionLine;
import aim4.map.cpm.parking.parkingarea.MultiLaneWidthParkingArea;
import aim4.map.cpm.parking.parkingarea.SingleLaneWidthParkingArea;
import aim4.map.cpm.parking.SensoredLine;
import aim4.map.cpm.parking.StatusMonitor;
import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Becci on 23-Apr-17.
 */
public class CPMCarParkMultiLaneWidth extends CPMBasicMap {

    /**
     * The length of the parking lanes used for parking.
     */
    private double parkingLength;
    /**
     * The length of the parking lanes used for access.
     */
    private double accessLength;
    /**
     * The parking area.
     */
    private MultiLaneWidthParkingArea parkingArea;
    /**
     * The status monitor recording the status of this car park.
     */
    private StatusMonitor statusMonitor;
    /**
     * A list of sensored lines used by the StatusMonitor.
     */
    private List<SensoredLine> sensoredLines;
    /**
     * The exit data collection line.
     */
    private CPMExitDataCollectionLine exitDataCollectionLine;
    /**
     * The entry data collection line.
     */
    private DataCollectionLine entryDataCollectionLine;
    /**
     * The total area of the car park.
     */
    private double totalCarParkArea; // in square metres
    /**
     * A list of <numberOfParkingLanes, parkingLaneWidth> pairs.
     */
    private List<Pair<Integer, Double>> parkingLaneSets;
    /**
     * The maximum lane width in parkingLaneSets.
     * */
    private double maxLaneWidth;
    /**
     * Half of the maximum lane width in parkingLaneSets.
     * */
    private double halfMaxLaneWidth;

    public CPMCarParkMultiLaneWidth(double speedLimit, double initTime,
                                    double parkingLength, double accessLength,
                                    List<Pair<Integer, Double>> parkingLaneSets) {
        super(speedLimit, initTime);

        if (parkingLaneSets.size() == 0 ||
                (parkingLaneSets.size() == 1 && parkingLaneSets.get(0).getKey() == 0)) {
            throw new RuntimeException("There must be at least 1 parking lane!");
        }

        this.parkingLaneSets = parkingLaneSets;
        this.parkingLength = parkingLength;
        this.accessLength = accessLength;

        // Get the maximum lane width - the roads used to access the parking area must be
        // at least this wide to cater for the widest vehicle.
        this.maxLaneWidth = getMaxLaneWidth(parkingLaneSets);
        this.halfMaxLaneWidth = maxLaneWidth/2;

        // Calculate the height of the parking area
        double parkingAreaHeight = calculateParkingAreaHeight();

        // Calculate the map dimensions
        double mapWidth = (BORDER * 2) // The border used to pad the map
                + (maxLaneWidth * 2) // The 2 vertical roads either side of the parking area
                + (2 * accessLength) // The length of the parking lane used for access (either side)
                + parkingLength; // The length of the parking lanes used for parking
        double mapHeight = (BORDER * 2) // The border used to pad the map
                + maxLaneWidth // The horizontal road running across the top of the parking area
                + parkingAreaHeight; // The height of the parking area
        this.dimensions = new Rectangle2D.Double(0, 0, mapWidth, mapHeight);

        // Calculate the start point for the parking area
        double x = BORDER;
        double y = dimensions.getMaxY() - BORDER - maxLaneWidth;
        Point2D startPoint = new Point2D.Double(x, y);

        // Create the parking area
        this.parkingArea = new MultiLaneWidthParkingArea(startPoint, this,
                parkingLength, accessLength, parkingLaneSets);

        // Create the StatusMonitor
        // this.statusMonitor = new StatusMonitor(parkingArea);

        // Add all roads/lanes from parking area to the map's records
        for (Road road : parkingArea.getRoads()) {
            horizontalRoads.add(road);
            registerLane(road.getOnlyLane());
        }

        // Create the vertical Roads

        //SOUTH - ENTERS PARKING
        double x1 = BORDER + halfMaxLaneWidth;
        double y1 = mapHeight - BORDER;
        double x2 = x1;
        double y2 = BORDER;
        Road southBoundRoad = createRoadWithOneLane("Southbound Road", x1, y1, x2, y2, maxLaneWidth);
        verticalRoads.add(southBoundRoad);

        //NORTH - LEAVES PARKING
        x1 = mapWidth - BORDER - halfMaxLaneWidth;
        y1 = BORDER;
        x2 = x1;
        y2 = mapHeight - BORDER;
        Road northBoundRoad = createRoadWithOneLane("Northbound Road", x1, y1, x2, y2, maxLaneWidth);
        verticalRoads.add(northBoundRoad);

        // Create the horizontal Roads
        // WEST - EXITS CAR PARK
        x1 = mapWidth - BORDER;
        y1 = mapHeight - BORDER - halfMaxLaneWidth;
        x2 = 0;
        y2 = y1;
        Road westBoundRoad = createRoadWithOneLane("Westbound Avenue", x1, y1, x2, y2, maxLaneWidth);
        horizontalRoads.add(westBoundRoad);

        // EAST - ENTERS CAR PARK
        x1 = 0;
        y1 = mapHeight - BORDER - maxLaneWidth - halfMaxLaneWidth;
        x2 = BORDER + maxLaneWidth;
        y2 = y1;
        Road eastBoundRoad = createRoadWithOneLane("Eastbound Avenue", x1, y1, x2, y2, maxLaneWidth);
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
            for (Road road : roadsInParkingArea) {
                makeJunction(road, southBoundRoad);
                makeJunction(road, northBoundRoad);
            }
        }

        // Set size of array for the data collection lines.
        // One on entry and one on exit
        dataCollectionLines = new ArrayList<DataCollectionLine>(2);
        // Create data collection lines
        x1 = entranceLane.getStartPoint().getX() + BORDER;
        y1 = entranceLane.getStartPoint().getY() + halfMaxLaneWidth;
        x2 = x1;
        y2 = y1 - maxLaneWidth;
        entryDataCollectionLine = new DataCollectionLine(
                "Car Park Entrance",
                dataCollectionLines.size(),
                new Point2D.Double(x1, y1),
                new Point2D.Double(x2, y2),
                true);
        dataCollectionLines.add(entryDataCollectionLine);

        x1 = exitLanes.get(0).getEndPoint().getX() + BORDER;
        y1 = exitLanes.get(0).getEndPoint().getY() + halfMaxLaneWidth;
        x2 = x1;
        y2 = y1 - maxLaneWidth;
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
        x1 = BORDER / 2;
        y1 = parkingArea.getStartPoint().getY();
        x2 = x1;
        y2 = y1 - maxLaneWidth;
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
        x2 = x1 + maxLaneWidth;
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
        y2 = y1 + maxLaneWidth;
        sensoredLines.add(
                new SensoredLine(
                        "Exit sensor line",
                        sensoredLines.size(),
                        SensoredLine.SensoredLineType.EXIT,
                        new Point2D.Double(x1, y1),
                        new Point2D.Double(x2, y2)));

        // Initialise the spawn point
        initializeSpawnPoints(initTime);

        // Calculate the total area of the car park
        calculateAreaOfCarPark();
    }

    private void calculateAreaOfCarPark() {
        double totalArea = 0;

        // Add the area of the parking area (l*h)
        totalArea += parkingArea.getTotalLength() * calculateParkingAreaHeight();

        // Add the West road, but only up to the
        // length of the parking area
        totalArea += getRoadByName("Westbound Avenue").getOnlyLane().getWidth() * parkingArea.getTotalLength();

        totalCarParkArea = totalArea;
    }

    public double getMaxLaneWidth(List<Pair<Integer, Double>> parkingLaneSets) {
        double maxLaneWidth = 0.0;
        for (Pair<Integer, Double> pair : parkingLaneSets) {
            if (pair.getValue() > maxLaneWidth) {
                maxLaneWidth = pair.getValue();
            }
        }
        return maxLaneWidth;
    }

    private double calculateParkingAreaHeight() {
        double height = 0.0;
        for (Pair<Integer, Double> pair : parkingLaneSets) {
            height += pair.getKey() * pair.getValue();
        }
        return height;
    }

    @Override
    public double getLaneWidth() {
        return 0;
    }

    public double getMaxLaneWidth() {
        return maxLaneWidth;
    }

    @Override
    public StatusMonitor getStatusMonitor() {
        return null;
    }

    @Override
    public SingleLaneWidthParkingArea getParkingArea() {
        return null;
    }
}
