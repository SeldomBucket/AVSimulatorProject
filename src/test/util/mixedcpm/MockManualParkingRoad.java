package util.mixedcpm;

import aim4.map.Road;
import aim4.map.mixedcpm.parking.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class MockManualParkingRoad implements IManualParkingRoad {

    double x;

    public MockManualParkingRoad(double x){
        this.x = x;
    }

    @Override
    public ArrayList<ManualStall> getParkingSpaces() {
        return null;
    }

    @Override
    public Road getCentreRoad() {
        return null;
    }

    @Override
    public double getEntireWidth() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setLastRoad(boolean lastRoad) {

    }

    @Override
    public ManualStall findNewSpace(StallInfo stallInfo, ManualParkingRoad.SearchParameter searchType) {
        return null;
    }

    @Override
    public StallStack[] getStallStackPair() {
        return new StallStack[0];
    }

    @Override
    public Point2D getStartPoint() {
        return new Point2D.Double(x,0);
    }
}
