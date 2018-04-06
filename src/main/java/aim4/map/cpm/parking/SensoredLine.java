package aim4.map.cpm.parking;

import aim4.vehicle.VehicleSimModel;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A SensoredLine is used by a IStatusMonitor: when a vehicle
 * crosses a SensoredLine the IStatusMonitor can decide how
 * to update it's records of the car park, and whether any
 * messages need to be sent to CPMBasicCoordinator.
 */
public class SensoredLine {

    public enum SensoredLineType {
        /** Is crossed on entry to the car park. */
        ENTRY,
        /** Is crossed on reentry to the car park */
        REENTRY,
        /** Is passed on exit of the car park.  */
        EXIT
    }

    /////////////////////////////////
    // CONSTANTS
    /////////////////////////////////

    /** The no repeat time period */
    private static final double NO_REPEAT_TIME_PERIOD = 2.0; // seconds

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /** The name of this data collection line */
    private String name;
    /** The ID of this sensored line */
    private int id;
    /** The line to represent this sensored line */
    private Line2D line;
    /** The type of this sensored line */
    SensoredLineType type;
    /** The record of the times of the vehicle passing through the line */
    private Map<Integer,List<Double>> vinToTime;

    /////////////////////////////////
    // CONSTRUCTORS
    /////////////////////////////////

    public SensoredLine(String name, int id, SensoredLineType type,
                        Point2D p1, Point2D p2) {
        this.name = name;
        this.id = id;
        this.line = new Line2D.Double(p1,p2);
        this.type = type;
        this.vinToTime = new HashMap<Integer,List<Double>>();
    }

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    /**
     * Whether the vehicle intersects the line.
     *
     * @param vehicle     the vehicle
     * @param time  the current time
     * @param p1    the first point of the vehicle
     * @param p2    the second point of the vehicle
     * @return whether the vehicle intersects the line
     */
    public boolean intersect(VehicleSimModel vehicle, double time,
                             Point2D p1, Point2D p2) {
        int vin = vehicle.getVIN();
        if (!vinToTime.containsKey(vin)
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
                System.out.println("INTERSECT WITH " + type + " SENSORED LINE: " + time);
                return true;
            } else {
                return false;
            }
        } else {  // the vehicle passed through this sensored line
            // twice or more within last NO_REPEAT_TIME_PERIOD seconds
            return false;
        }
    }

    /**
     * Get the name of the line.
     *
     * @return the name of the line
     */
    public String getName() {
        return name;
    }

    /**
     * Get the ID of the line.
     *
     * @return the ID of the line
     */
    public int getId() {
        return id;
    }

    /**
     * Get the type of the line.
     *
     * @return the type of the line
     */
    public SensoredLineType getType() { return type; }

    /**
     * Get the line that represents this sensored line
     *
     * @return the line for this sensored line
     */
    public Line2D getLine() { return line; }
}
