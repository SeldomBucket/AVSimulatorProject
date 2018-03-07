package aim4.map.mixedcpm.testmaps;

import aim4.map.Road;
import aim4.map.cpm.parking.StatusMonitor;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.parking.ManualParkingArea;

import java.awt.geom.Rectangle2D;

/**
 * Test map which is just a standard manual car park (for testing the spaces)
 */
public class StaticManualMapMixed extends MixedCPMBasicMap {

    public StaticManualMapMixed(){
        super(3,5,0);

        // TODO ED dimensions of this map
        double width = 30;
        double height = 20;
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
        manualParkingArea.addNewParkingRoad("Test", 10);
        // TODO ED test manual parking area
    }

    @Override
    public StatusMonitor getStatusMonitor() {
        return null;
    }

    @Override
    public ManualParkingArea getManualParkingArea() {
        return null;
    }
}
