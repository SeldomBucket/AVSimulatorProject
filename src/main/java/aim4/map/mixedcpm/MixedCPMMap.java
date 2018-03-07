package aim4.map.mixedcpm;

import aim4.map.BasicMap;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.cpm.CPMSpawnPoint;
import aim4.map.cpm.parking.ParkingArea;
import aim4.map.cpm.parking.StatusMonitor;
import aim4.map.lane.Lane;
import aim4.map.mixedcpm.parking.ManualParkingArea;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;
import aim4.vehicle.mixedcpm.MixedCPMBasicAutoVehicle;

import java.util.List;

/**
 * Interface for all MixedCPM maps.
 */
public interface MixedCPMMap extends BasicMap {
    public List<Lane> getExitLanes();
    public double getLaneWidth();
    public List<Corner> getCorners();
    public List<Junction> getJunctions();
    public List<SimpleIntersection> getIntersections();
    public List<MixedCPMSpawnPoint> getSpawnPoints();
    public StatusMonitor getStatusMonitor();
    public void addVehicleToMap(MixedCPMBasicAutoVehicle vehicle);
    public List<MixedCPMBasicAutoVehicle> getVehicles();
    public ManualParkingArea getManualParkingArea();
    public void update();

}