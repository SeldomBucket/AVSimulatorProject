package aim4.map.cpm.parking;

import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.util.*;

/**
 * An object which holds an updated status of the car park,
 * including the space left in each parking lane and the
 * remaining capacity of the car park.
 */
public class StatusMonitor {
    /** The parking area that we are recording the status of. */
    private ParkingArea parkingArea;
    /** The remaining capacity of the car park. */
    private double remainingCapacity = 0;
    /** A mapping from parking lanes to the amount of
     * space left for parking on that lane. */
    private Map<ParkingLane, Double> parkingLanesSpace = new HashMap<ParkingLane, Double>();
    /** A list of vehicles which are currently in the car park. */
    private List<CPMBasicAutoVehicle> vehicles = new ArrayList<CPMBasicAutoVehicle>();

    /**
     * Create a StatusMonitor to record the status of the car park.
     * @param parkingArea The parking area to record the status of.
     */
    public StatusMonitor(ParkingArea parkingArea) {
        this.parkingArea = parkingArea;

        initialiseParkingLanesSpace(parkingArea);
        calculateRemainingCapacity();
    }

    /**
     * Create a mapping from each parking lane to the length of
     * the parking space available in that lane.
     * @param parkingArea The parking area to extract the parking
     *                    lanes from.
     */
    private void initialiseParkingLanesSpace(ParkingArea parkingArea){
        for (ParkingLane lane : parkingArea.getParkingLanes()) {
            parkingLanesSpace.put(lane, lane.getTotalParkingLength());
        }
    }

    /**
     * Calculate the remaining capacity of the car park by totalling
     * the available space in each parking lane.
     */
    private void calculateRemainingCapacity() {
        Iterator it = parkingLanesSpace.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            remainingCapacity += (Double) pair.getValue();
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public double getRemainingCapacity() {
        return remainingCapacity;
    }
}
