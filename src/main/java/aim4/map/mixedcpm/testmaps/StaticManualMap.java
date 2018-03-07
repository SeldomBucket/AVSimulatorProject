package aim4.map.mixedcpm.testmaps;

import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.cpm.CPMSpawnPoint;
import aim4.map.cpm.parking.ParkingArea;
import aim4.map.cpm.parking.StatusMonitor;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.parking.ManualParkingArea;
import aim4.util.Registry;
import aim4.vehicle.mixedcpm.MixedCPMBasicAutoVehicle;

import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Test map which is just a standard manual car park (for testing the spaces)
 */
public class StaticManualMap extends MixedCPMBasicMap {

    public StaticManualMap(){
        super(3,5,0);
        Road topRoad = createRoadWithOneLane("topRoad",
                                                BORDER,
                                                BORDER,
                                                BORDER,
                                                this.dimensions.getWidth()- BORDER);
        Road bottomRoad = createRoadWithOneLane("bottomRoad",
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
