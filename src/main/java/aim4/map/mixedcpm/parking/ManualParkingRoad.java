package aim4.map.mixedcpm.parking;

import aim4.map.BasicMap;
import aim4.map.Road;
import aim4.map.lane.Lane;

import java.util.ArrayList;
import java.util.UUID;

public class ManualParkingRoad{

    private Road centreRoad;
    private StallStack stallStackPair[];
    private boolean lastRoad;
    private UUID roadID;

    /** Parking area this road belongs to */
    private ManualParkingArea parkingArea;

    /**
     * Enum to show which type of space you want to search for
     */
    public enum StallSearchParameter{
        exactSize,
        correctHeight,
        emptyStack,
        anyFreeSpace
    }

    // Public Methods
    public ManualParkingRoad(String name,
                             BasicMap map,
                             Lane lane,
                             ManualParkingArea parkingArea){
        ArrayList<Lane> laneList = new ArrayList<>();
        laneList.add(lane);
        centreRoad = new Road(name, laneList, map);
        stallStackPair =
                new StallStack[] {  new StallStack(0,
                                                    lane.getStartPoint().getX(),
                                                    lane.getStartPoint().getY(),
                                                    lane.getLength(),
                                                    false,
                                                    this),
                                    new StallStack(0,
                                                    lane.getStartPoint().getX(),
                                                    lane.getStartPoint().getY(),
                                                    lane.getLength(),
                                                    false,
                                                    this)
        };
        this.parkingArea = parkingArea;
        this.parkingArea.update();
        this.roadID = UUID.randomUUID();
    }

    public ArrayList<ManualStall> getParkingSpaces(){
        ArrayList<ManualStall> returnList = stallStackPair[0].getManualStalls();
        returnList.addAll(stallStackPair[1].getManualStalls());
        return returnList;
    }

    public Road getCentreRoad() { return centreRoad; }

    public UUID getID()
    {
        return this.roadID;
    }

    public void setLastRoad(boolean lastRoad){
        this.lastRoad = lastRoad;
        if (lastRoad){
            stallStackPair[1].setLastStallStack(true);
        } else {
            stallStackPair[0].setLastStallStack(false);
            stallStackPair[1].setLastStallStack(false);
        }
    }

    public ManualStall findNewSpace(StallInfo stallInfo, StallSearchParameter searchType) {
        for (StallStack stack:stallStackPair) {
            switch (searchType) {
                case exactSize:
                    if (stallInfo.getLength() == stack.getMaxStallLength() &&
                            stallInfo.getWidth() == stack.getIdealStallWidth()){
                        //DO THE SEARCH
                    }
                    if (searchType != StallSearchParameter.anyFreeSpace) {break;}
                case correctHeight:

                    if (searchType != StallSearchParameter.anyFreeSpace) {break;}
                case emptyStack:

                    if (searchType != StallSearchParameter.anyFreeSpace) {break;}
            }
        }
        return null;
    }
}

