package aim4.map.mixedcpm.parking;

import aim4.map.BasicMap;
import aim4.map.Road;
import aim4.map.lane.Lane;

import java.awt.geom.Line2D;
import java.util.ArrayList;

public class ManualParkingRoad{

    private Road centreRoad;
    private StallStack stallStackPair[];
    private boolean HasParkingSpaces;

    // Public Methods
    public ManualParkingRoad(String name, BasicMap map, Lane lane){
        ArrayList<Lane> laneList = new ArrayList<>();
        laneList.add(lane);
        centreRoad = new Road(name, laneList, map);
        stallStackPair = new StallStack[] { new StallStack(0,
                                                    lane.getStartPoint().getX(),
                                                    lane.getStartPoint().getY(),
                                                    lane.getLength(),
                                                    true),
                                            new StallStack(0,
                                                    lane.getStartPoint().getX(),
                                                    lane.getStartPoint().getY(),
                                                    lane.getLength(),
                                                    false) };
        }

    public ArrayList<ManualStall> getParkingSpaces(){
        ArrayList<ManualStall> returnList = stallStackPair[0].getManualStalls();
        returnList.addAll(stallStackPair[1].getManualStalls());
        return returnList;
    }

    public Road getCentreRoad() { return centreRoad; }
}
