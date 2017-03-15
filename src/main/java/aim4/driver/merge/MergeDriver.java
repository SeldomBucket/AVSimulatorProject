package aim4.driver.merge;

import aim4.driver.BasicDriver;
import aim4.map.merge.MergeSpawnPoint;

/**
 * Created by Callum on 14/03/2017.
 */
public abstract class MergeDriver extends BasicDriver implements MergeDriverSimModel {
    private MergeSpawnPoint spawnPoint;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSpawnPoint(MergeSpawnPoint spawnPoint) {
        this.spawnPoint = spawnPoint;
    }
}
