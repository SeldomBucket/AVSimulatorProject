package aim4.map.mixedcpm.parking;

import aim4.map.Road;
import aim4.map.mixedcpm.parking.ManualParkingArea;
import aim4.map.mixedcpm.parking.ManualStall;
import aim4.map.mixedcpm.parking.StallInfo;
import aim4.map.mixedcpm.parking.StallStack;

import java.util.ArrayList;

public interface IManualParkingRoad {
    public ArrayList<ManualStall> getParkingSpaces();
    public Road getCentreRoad();
    public double getEntireWidth();
    public String getName();
    public void setLastRoad(boolean lastRoad);
    public ManualStall findNewSpace(StallInfo stallInfo, aim4.map.mixedcpm.parking.ManualParkingRoad.SearchParameter searchType);
    public StallStack[] getStallStackPair();
}
