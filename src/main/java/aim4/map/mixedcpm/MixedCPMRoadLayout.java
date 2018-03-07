package aim4.map.mixedcpm;

import aim4.map.Road;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.lane.LineSegmentLane;
import aim4.vehicle.mixedcpm.MixedCPMBasicVehicleModel;
import java.util.List;

/**
 * Interface for all Mixed CPM road maps.
 */
public interface MixedCPMRoadLayout {
    public List<Road> getRoads();
    public List<Corner> getCorners();
    public List<Junction> getJunctions();
    public List<SimpleIntersection> getIntersections();
    public void addVehicleToMap(MixedCPMBasicVehicleModel vehicle);
    public List<MixedCPMBasicVehicleModel> getVehicles();

}
