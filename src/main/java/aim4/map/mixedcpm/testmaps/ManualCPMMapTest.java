package aim4.map.mixedcpm.testmaps;

import aim4.map.Road;
import aim4.map.cpm.parking.StatusMonitor;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.parking.ManualParkingArea;

import java.awt.geom.Rectangle2D;

/**
 * Test map which is just a standard manual car park (for testing the spaces)
 */
public class ManualCPMMapTest extends MixedCPMBasicMap {

    public ManualCPMMapTest(double height, double width, double laneWidth, double speedLimit, double initTime){
        super(laneWidth,speedLimit,initTime);

        this.dimensions = new Rectangle2D.Double(0.0,0.0,width + BORDER*2,height + BORDER*2);

        Road topRoad = makeRoadWithOneLane("topRoad",
                                                BORDER,
                                                BORDER,
                                                this.dimensions.getWidth()- BORDER,
                                                BORDER);
        Road bottomRoad = makeRoadWithOneLane("bottomRoad",
                                                BORDER,
                                                this.dimensions.getHeight() - BORDER,
                                                this.dimensions.getWidth()- BORDER,
                                                this.dimensions.getHeight() - BORDER);

        this.manualParkingArea = new ManualParkingArea(topRoad, bottomRoad, this, new Rectangle2D.Double(BORDER, BORDER, width, height));
    }

    /**
     * Gets the status monitor of this map
     * @return null as this map doesn't have any status monitors
     */
    @Override
    public StatusMonitor getStatusMonitor() {
        return null;
    }

    /**
     * Gets the manual parking area of this map
     * @return the manual parking area
     */
    @Override
    public ManualParkingArea getManualParkingArea() {
        return manualParkingArea;
    }
}
