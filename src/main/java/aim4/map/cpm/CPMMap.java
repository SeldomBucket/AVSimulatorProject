package aim4.map.cpm;

import aim4.map.BasicMap;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.cpm.components.CPMSpawnPoint;
import aim4.map.cpm.parking.SensoredLine;
import aim4.map.cpm.parking.parkingarea.ParkingArea;
import aim4.map.cpm.parking.parkingarea.SingleLaneWidthParkingArea;
import aim4.map.cpm.parking.statusmonitor.BasicStatusMonitor;
import aim4.map.lane.Lane;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.util.List;

/**
 * Interface for all CPM maps.
 */
public interface CPMMap extends BasicMap {
    public List<Lane> getExitLanes();
    public double getLaneWidth();
    public List<Corner> getCorners();
    public List<Junction> getJunctions();
    public List<SimpleIntersection> getIntersections();
    public List<CPMSpawnPoint> getSpawnPoints();
    public BasicStatusMonitor getStatusMonitor();
    public void addVehicleToMap(CPMBasicAutoVehicle vehicle);
    public List<CPMBasicAutoVehicle> getVehicles();
    public ParkingArea getParkingArea();
    public List<SensoredLine> getSensoredLines();
    public double getTotalCarParkArea();



}
