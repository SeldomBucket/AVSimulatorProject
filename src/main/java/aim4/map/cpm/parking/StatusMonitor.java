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

    public void vehicleOnEntry(CPMBasicAutoVehicle vehicle) {
        // Find the lane with the most room available
        Map.Entry<ParkingLane, Double>  parkingLaneEntry = findLeastFullParkingLane();

        // Check there is room for this vehicle
        double vehicleLength = vehicle.getSpec().getLength();
        double distanceBetweenVehicles = 0.2; // TODO CPM find this value
        double spaceNeeded = vehicleLength + distanceBetweenVehicles;
        if (willVehicleFit(parkingLaneEntry, spaceNeeded)) {
            // Update the space avilable on that lane
            double newAvailbleSpace = parkingLaneEntry.getValue() - spaceNeeded;
            parkingLanesSpace.put(parkingLaneEntry.getKey(), newAvailbleSpace);
            // TODO CPM Send vehicle message with parking lane
        } else {
            // TODO CPM If not, send vehicle a message to wait
        }
    }

    public void vehicleOnReEntry(CPMBasicAutoVehicle vehicle) {
        // Send it a parking lane
    }

    public void vehicleOnExit(CPMBasicAutoVehicle vehicle) {
        // Update capacity
        // Check if any vehicles waiting to enter
    }

    private Map.Entry<ParkingLane, Double> findLeastFullParkingLane() {
        Map.Entry<ParkingLane, Double> maxEntry = null;

        for (Map.Entry<ParkingLane, Double> entry : parkingLanesSpace.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }
        return maxEntry;
    }

    private boolean willVehicleFit(Map.Entry<ParkingLane, Double> parkingLaneEntry,
                                   Double spaceNeeded) {

        double spaceOnParkingLane = parkingLaneEntry.getValue();
        if (spaceOnParkingLane > (spaceNeeded)) {
            return true;
        }
        return false;
    }
}
