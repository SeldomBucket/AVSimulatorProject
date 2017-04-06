package aim4.map.cpm;

import aim4.map.DataCollectionLine;
import aim4.vehicle.VehicleSimModel;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The data collection line for CPM simulations.
 */
public class CPMExitDataCollectionLine extends DataCollectionLine {

    /** The record of the estimated distance travelled of the vehicle passing through the line */
    private Map<Integer,Double> vinToEstimatedDistanceTravelled;

    /** The record of the retrieval time of the vehicle passing through the line */
    private Map<Integer,Double> vinToRetrievalTime;

    /** The record of the parking time of the vehicle passing through the line */
    private Map<Integer,Double> vinToParkingTime;

    /**
     * Create a data collection line.
     *
     * @param name       the name of the data collection line
     * @param id         the ID of the line
     * @param p1         the first point of the line
     * @param p2         the second point of the line
     * @param isNoRepeat Whether vehicles should not be counted more than once
     *                   when it passes through the line more than once within
     */
    public CPMExitDataCollectionLine(String name, int id, Point2D p1, Point2D p2, boolean isNoRepeat) {
        super(name, id, p1, p2, isNoRepeat);
        this.vinToEstimatedDistanceTravelled = new HashMap<Integer,Double>();
        this.vinToRetrievalTime = new HashMap<Integer,Double>();
        this.vinToParkingTime = new HashMap<Integer,Double>();
    }

    /**
     * Whether the vehicle intersects the line.
     *
     * @param v     the vehicle
     * @param time  the current time
     * @param p1    the first point of the vehicle
     * @param p2    the second point of the vehicle
     * @return whether the vehicle intersects the line
     */
    public boolean intersect(VehicleSimModel v, double time,
                             Point2D p1, Point2D p2) {
        assert v instanceof CPMBasicAutoVehicle;
        int vin = v.getVIN();
        if (!isNoRepeat
                || !vinToTime.containsKey(vin)
                || vinToTime.get(vin).get(vinToTime.get(vin).size()-1)
                + NO_REPEAT_TIME_PERIOD < time) {
            if (line.intersectsLine(p1.getX(), p1.getY(), p2.getX(), p2.getY())) {
                if (!vinToTime.containsKey(vin)) {
                    List<Double> times = new LinkedList<Double>();
                    times.add(time);
                    vinToTime.put(vin, times);
                } else {
                    vinToTime.get(vin).add(time);
                }
                double parkingTime = ((CPMBasicAutoVehicle) v).getParkingTime();
                vinToParkingTime.put(vin, parkingTime);
                vinToEstimatedDistanceTravelled.put(vin, ((CPMBasicAutoVehicle) v).getEstimatedDistanceTravelled());
                System.out.println("INTERSECT WITH DCL");
                return true;
            } else {
                return false;
            }
        } else {  // the vehicle passed through this data collection line
            // twice or more within last NO_REPEAT_TIME_PERIOD seconds
            return false;
        }
    }

    /**
     * Get the parking time of a vehicle passing through the line.
     *
     * @param vin  the VIN of the vehicle
     * @return the parking time of the vehicle passing through the line
     */
    public Double getParkingTime(int vin) {
        return vinToParkingTime.get(vin);
    }

    /**
     * Get the estimated distance travelled of a vehicle passing through the line.
     *
     * @param vin  the VIN of the vehicle
     * @return the estimated distance travelled of the vehicle passing through the line
     */
    public Double getEstimatedDistanceTravelled(int vin) {
        return vinToEstimatedDistanceTravelled.get(vin);
    }
}
