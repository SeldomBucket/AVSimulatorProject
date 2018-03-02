package aim4.map.mixedcpm.parking;

import aim4.map.BasicMap;
import aim4.map.Road;
import aim4.map.lane.Lane;

import java.awt.geom.Line2D;
import java.util.ArrayList;

public class ManualParkingRoad extends Road{

    private StallStack stallStackPair[];
    private boolean HasParkingSpaces;

    // Public Methods
    public ManualParkingRoad(String name, BasicMap map, Lane lane){
        super(name, map);
        this.addTheRightMostLane(lane);
        stallStackPair = new StallStack[] { new StallStack(0,
                                                    lane.getStartPoint().getX(),
                                                    lane.getStartPoint().getY(),
                                                    lane.getLength()),
                                            new StallStack(0,
                                                    lane.getStartPoint().getX(),
                                                    lane.getStartPoint().getY(),
                                                    lane.getLength()) };
        }

    public ArrayList<ParkingSpace> getParkingSpaces(){
        ArrayList<ParkingSpace> returnList = stallStackPair[0].getParkingSpaces();
        returnList.addAll(stallStackPair[1].getParkingSpaces());
        return returnList;
    }
}
