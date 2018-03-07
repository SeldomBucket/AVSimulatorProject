package aim4.map;

import aim4.map.aim.AIMSpawnPoint;
import aim4.map.lane.Lane;
import aim4.util.Registry;

import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Created by Callum on 28/11/2016.
 */
public interface BasicMap extends RoadMap{

    /**
     * Get the list of data collection line.
     *
     * @return the data collection lines
     */
    List<DataCollectionLine> getDataCollectionLines();

    /**
     * Get the list of spawn points.
     *
     * @return the list of spawn points
     */
    List<? extends SpawnPoint> getSpawnPoints();

    /**
     * Print the data collected in data collection lines to the given file
     *
     * @param outFileName  the name of the file to which the data are outputted.
     */
    void printDataCollectionLinesData(String outFileName);
}
