package aim4.driver;

import aim4.map.lane.Lane;

/**
 * Created by Callum on 28/11/2016.
 */
public interface Driver extends DriverSimModel {
    void addCurrentlyOccupiedLane(Lane lane);
}
