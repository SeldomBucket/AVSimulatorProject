package aim4.map.mixedcpm.testmaps;

import aim4.map.Road;
import aim4.map.cpm.parking.StatusMonitor;
import aim4.map.mixedcpm.MixedMixedCPMBasicMap;
import aim4.map.mixedcpm.parking.ManualParkingArea;

/**
 * Test map which is just a standard manual car park (for testing the spaces)
 */
public class StaticManualMapMixed extends MixedMixedCPMBasicMap {

    public StaticManualMapMixed(){
        super(3,5,0);
        Road topRoad = makeRoadWithOneLane("topRoad",
                                                BORDER,
                                                BORDER,
                                                BORDER,
                                                this.dimensions.getWidth()- BORDER);
        Road bottomRoad = makeRoadWithOneLane("bottomRoad",
                                                this.dimensions.getHeight() - BORDER,
                                                BORDER,
                                                this.dimensions.getHeight() - BORDER,
                                                this.dimensions.getWidth()- BORDER);
        this.manualParkingArea = new ManualParkingArea(topRoad, bottomRoad, this);

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
