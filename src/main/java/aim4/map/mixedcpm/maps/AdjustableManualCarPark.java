package aim4.map.mixedcpm.maps;

import aim4.map.mixedcpm.statusmonitor.AdjustableManualStatusMonitor;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.parking.ManualParkingArea;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Map which is just a standard manual car park (for testing the spaces)
 */
public class AdjustableManualCarPark extends MixedCPMBasicMap {


    public AdjustableManualCarPark(double height, double width, double laneWidth, double speedLimit, double initTime){
        super(laneWidth,speedLimit,initTime);

        this.dimensions = new Rectangle2D.Double(0.0,0.0,width + BORDER*2,height + BORDER*2);

        this.dataCollectionLines = new ArrayList<>();

        initializeTopAndBottomRoads();

        this.spawnPoints = new ArrayList<>();

        this.spawnPoints.add(makeSpawnPoint(initTime, topRoad.getOnlyLane()));

        this.manualParkingArea = new ManualParkingArea(topRoad, bottomRoad, this, new Rectangle2D.Double(BORDER, BORDER, width, height));

        statusMonitor = new AdjustableManualStatusMonitor(this);
    }


}
