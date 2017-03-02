package aim4.map.cpm;

import aim4.map.BasicMap;
import aim4.map.connections.CornerRightAngleOneWay;
import aim4.map.lane.Lane;

import java.util.List;

/**
 * Interface for all CPM maps.
 */
public interface CPMMap extends BasicMap {
    public Lane getExitLane();
    public List<CornerRightAngleOneWay> getCorners();


}
