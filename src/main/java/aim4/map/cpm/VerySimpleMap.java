package aim4.map.cpm;

import aim4.map.BasicMap;
import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.SpawnPoint;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.util.ArrayListRegistry;
import aim4.util.Registry;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Map for a car park grid.
 */
public class VerySimpleMap implements BasicMap {

    /** The number of rows */
    private int rows;
    /** The number of columns */
    private int columns;
    /** The number of rows in the grid*/
    private int gridRows;
    /** The number of columns in the grid */
    private int gridColumns;
    /** The dimensions of the map */
    private Rectangle2D dimensions;
    /** The data collection lines */
    private List<DataCollectionLine> dataCollectionLines;
    /** The spawn points */
    // private List<SpawnPoint> spawnPoints;
    /** The horizontal spawn points */
    // private List<SpawnPoint> horizontalSpawnPoints;
    /** The lane registry */
    private Registry<Lane> laneRegistry =
            new ArrayListRegistry<Lane>();

    /**
     * Create a grid map.
     * For now, create one lane with data collection point on each side.
     */
    public VerySimpleMap() {

        columns = 5;
        rows = 5;
        dimensions = new Rectangle2D.Double(0, 0, columns, rows);

        // Create the vertical Road
        // Road southBoundRoad =





    }

    @Override
    public List<Road> getRoads() {
        return null;
    }

    @Override
    public Rectangle2D getDimensions() {
        return null;
    }

    @Override
    public double getMaximumSpeedLimit() {
        return 0;
    }

    @Override
    public Registry<Lane> getLaneRegistry() {
        return null;
    }

    @Override
    public Road getRoad(Lane lane) {
        return null;
    }

    @Override
    public Road getRoad(int laneID) {
        return null;
    }

    @Override
    public List<DataCollectionLine> getDataCollectionLines() {
        return null;
    }

    @Override
    public List<SpawnPoint> getSpawnPoints() {
        return null;
    }

    @Override
    public void printDataCollectionLinesData(String outFileName) {

    }
}
