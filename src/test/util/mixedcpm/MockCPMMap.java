package util.mixedcpm;

import aim4.map.Road;
import aim4.map.RoadMap;
import aim4.map.cpm.parking.StatusMonitor;
import aim4.map.lane.Lane;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.parking.ManualParkingArea;
import aim4.util.Registry;

import java.awt.geom.Rectangle2D;
import java.util.List;

public class MockCPMMap extends MixedCPMBasicMap {

    public MockCPMMap(double laneWidth, double speedLimit){
        super(laneWidth, speedLimit, 0);
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
