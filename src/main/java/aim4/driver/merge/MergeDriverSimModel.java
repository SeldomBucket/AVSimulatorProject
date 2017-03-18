package aim4.driver.merge;

import aim4.driver.DriverSimModel;
import aim4.map.merge.MergeSpawnPoint;

/**
 * Created by Callum on 14/03/2017.
 */
public interface MergeDriverSimModel extends DriverSimModel {
    /**
     * Sets where the driver agent is coming from
     * @param spawnPoint the spawn point that generated the driver.
     */
    void setSpawnPoint(MergeSpawnPoint spawnPoint);
}
