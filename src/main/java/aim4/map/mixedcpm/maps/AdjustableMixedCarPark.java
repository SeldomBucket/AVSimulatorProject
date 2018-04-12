package aim4.map.mixedcpm.maps;

import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.statusmonitor.AdjustableManualStatusMonitor;
import aim4.map.mixedcpm.parking.ManualParkingArea;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class AdjustableMixedCarPark extends MixedCPMBasicMap {

    public AdjustableMixedCarPark(double height, double width, double laneWidth, double speedLimit, double initTime){
        super(laneWidth,speedLimit,initTime);

        this.dimensions = new Rectangle2D.Double(0.0,0.0,width + BORDER*2,height + BORDER*2);

        this.dataCollectionLines = new ArrayList<>();

        initializeTopAndBottomRoads();

        this.spawnPoints = new ArrayList<>();

        this.spawnPoints.add(makeSpawnPoint(initTime, topRoad.getOnlyLane()));

        this.manualParkingArea = new ManualParkingArea(topRoad, bottomRoad, this, new Rectangle2D.Double(BORDER, BORDER, 0, height));

        statusMonitor = new AdjustableManualStatusMonitor(manualParkingArea);
    }

    @Override
    public boolean canResizeManualArea(double maxX) {
        boolean outsideArea = super.canResizeManualArea(maxX);
        return outsideArea;
    }
}
