package aim4.map.cpm;

import aim4.map.BasicMap;
import aim4.map.lane.Lane;

/**
 * Interface for all CPM maps.
 */
public interface CPMMap extends BasicMap {
    public Lane getExitLane();


}
