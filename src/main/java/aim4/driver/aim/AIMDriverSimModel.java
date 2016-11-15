package aim4.driver.aim;

/**
 * Created by Callum on 15/11/2016.
 */

import aim4.driver.DriverSimModel;
import aim4.map.Road;
import aim4.map.SpawnPoint;

/**
 * An AIM driver from simulators' viewpoint.
 */
public interface AIMDriverSimModel extends DriverSimModel {
    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    // origin and destination

    /**
     * Get where this driver is coming from.
     *
     * @return the Road where this driver is coming from
     */
    SpawnPoint getSpawnPoint();

    /**
     * Set where this driver agent is coming from.
     *
     * @param spawnPoint the spawn point that generated the driver
     */
    void setSpawnPoint(SpawnPoint spawnPoint);

    /**
     * Get where this driver is going.
     *
     * @return the Road where this driver is going
     */
    Road getDestination();

    /**
     * Set where this driver is going.
     *
     * @param destination the Road where this driver should go
     */
    void setDestination(Road destination);
}
