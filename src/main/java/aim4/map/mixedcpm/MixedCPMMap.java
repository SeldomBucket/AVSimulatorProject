package aim4.map.mixedcpm;

import aim4.map.BasicMap;
import aim4.map.Road;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.mixedcpm.parking.ManualParkingRoad;
import aim4.map.mixedcpm.parking.StatusMonitor;
import aim4.map.lane.Lane;
import aim4.map.mixedcpm.parking.ManualParkingArea;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

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
    public StatusMonitor getStatusMonitor();
    public void addVehicleToMap(MixedCPMBasicManualVehicle vehicle);
    public List<MixedCPMBasicManualVehicle> getVehicles();
    public ManualParkingArea getManualParkingArea();
    public void update();

}
