package util.mixedcpm;

import aim4.map.mixedcpm.statusmonitor.IStatusMonitor;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.parking.ManualParkingArea;

public class MockMixedCPMMap extends MixedCPMBasicMap {

    public MockMixedCPMMap(double laneWidth, double speedLimit){
        super(laneWidth, speedLimit, 0);
    }

    @Override
    public IStatusMonitor getStatusMonitor() {
        return null;
    }

    @Override
    public ManualParkingArea getManualParkingArea() {
        return null;
    }
}
