package aim4.map.mixedcpm.parking;

import aim4.map.lane.LineSegmentLane;

import java.awt.geom.Line2D;

public class ManualParkingLane extends LineSegmentLane {
    public ManualParkingLane(Line2D line, double width, double speedLimit){
        super(line, width, speedLimit);
    }
}
