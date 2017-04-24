package aim4.map.cpm;

import aim4.map.DataCollectionLine;
import aim4.map.cpm.components.CPMExitDataCollectionLine;
import aim4.map.cpm.parking.parkingarea.SingleLaneWidthParkingArea;
import aim4.map.cpm.parking.SensoredLine;
import aim4.map.cpm.parking.StatusMonitor;
import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
    private SingleLaneWidthParkingArea parkingArea;
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

        // Calculate the height of the parking area
        double parkingAreaHeight = calculateParkingAreaHeight(parkingLaneSets);

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
    }

    private double getMaxLaneWidth(List<Pair<Integer, Double>> parkingLaneSets) {
        double maxLaneWidth = 0.0;
        for (Pair<Integer, Double> pair : parkingLaneSets) {
            if (pair.getValue() > maxLaneWidth) {
                maxLaneWidth = pair.getValue();
            }
        }
        return maxLaneWidth;
    }

    private double calculateParkingAreaHeight(List<Pair<Integer, Double>> parkingLaneSets) {
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

    @Override
    public StatusMonitor getStatusMonitor() {
        return null;
    }

    @Override
    public SingleLaneWidthParkingArea getParkingArea() {
        return null;
    }
}
