package aim4.map.cpm;

import aim4.map.DataCollectionLine;
import aim4.map.cpm.components.CPMExitDataCollectionLine;
import aim4.map.cpm.parking.ParkingArea;
import aim4.map.cpm.parking.SensoredLine;
import aim4.map.cpm.parking.StatusMonitor;
import javafx.util.Pair;

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
    private ParkingArea parkingArea;
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

    public CPMCarParkMultiLaneWidth(double speedLimit, double initTime,
                                    double parkingLength, double accessLength,
                                    List<Pair<Integer, Double>> parkingLaneSets) {
        super(speedLimit, initTime);
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
    public ParkingArea getParkingArea() {
        return null;
    }
}
