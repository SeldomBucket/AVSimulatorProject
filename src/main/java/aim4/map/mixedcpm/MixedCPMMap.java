package aim4.map.mixedcpm;

import aim4.map.BasicMap;
import aim4.map.Road;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.mixedcpm.parking.IAutomatedParkingArea;
import aim4.map.mixedcpm.parking.IManualParkingArea;
import aim4.map.mixedcpm.statusmonitor.IStatusMonitor;
import aim4.map.lane.Lane;
import aim4.map.mixedcpm.parking.ManualParkingArea;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;
import aim4.vehicle.mixedcpm.MixedCPMBasicVehicle;

import java.util.List;

/**
 * Interface for all MixedCPM maps.
 */
public interface MixedCPMMap extends BasicMap {
    public Road getTopRoad();
    public Road getBottomRoad();
    public List<Lane> getExitLanes();
    public double getLaneWidth();
    public List<Corner> getCorners();
    public List<Junction> getJunctions();
    public List<Junction> getStallJunctions();
    public List<SimpleIntersection> getIntersections();
    public List<MixedCPMSpawnPoint> getSpawnPoints();
    public IStatusMonitor getStatusMonitor();
    public void addVehicleToMap(MixedCPMBasicVehicle vehicle);
    public List<MixedCPMBasicVehicle> getVehicles();
    public IManualParkingArea getManualParkingArea();
    public IAutomatedParkingArea getAutomatedParkingArea();
    public double getTotalCarParkArea();
    public void update();

}
