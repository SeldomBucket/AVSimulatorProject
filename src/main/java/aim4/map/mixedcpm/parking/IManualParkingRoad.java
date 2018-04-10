package aim4.map.mixedcpm.parking;

import aim4.map.Road;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public interface IManualParkingRoad {
    public ArrayList<ManualStall> getManualStalls();
    public Road getCentreRoad();
    public double getEntireWidth();
    public String getName();
    public void setLastRoad(boolean lastRoad);
    public ManualStall findNewSpace(StallSpec stallSpec, aim4.map.mixedcpm.parking.ManualParkingRoad.SearchParameter searchType);
    public StallStack[] getStallStackPair();
    public Point2D getStartPoint();
    public ArrayList<Road> getRoads();
    public void markForDelete();
    public boolean isToBeDeleted();
}
