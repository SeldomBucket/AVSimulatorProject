package aim4.map.mixedcpm.parking;

import aim4.map.BasicMap;
import aim4.map.Road;
import aim4.map.connections.Junction;
import aim4.map.lane.Lane;

import java.util.ArrayList;
import java.util.List;
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
    public enum SearchParameter {
        exactSize,
        correctHeight,
        emptyStack,
        anyFreeSpace
    }

    // Public Methods
    public ManualParkingRoad(Road road,
                             ManualParkingArea parkingArea,
                             Double firstStackLength){
        centreRoad = road;
        stallStackPair =
                new StallStack[] {  new StallStack( road.getOnlyLane().getStartPoint().getX(),
                                                    road.getOnlyLane().getStartPoint().getY(),
                                                    firstStackLength,
                                                    road.getOnlyLane().getLength(),
                                                    false,
                                                    this),
                                    new StallStack( road.getOnlyLane().getStartPoint().getX(),
                                                    road.getOnlyLane().getStartPoint().getY(),
                                                    0,
                                                    road.getOnlyLane().getLength(),
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

    public double getEntireWidth() {
        return centreRoad.getOnlyLane().getWidth() +
                stallStackPair[0].getMaxStallLength() +
                stallStackPair[1].getMaxStallLength();
    }

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

    public ManualStall findNewSpace(StallInfo stallInfo, SearchParameter searchType) {
        for (StallStack stack:stallStackPair) {
            switch (searchType) {
                case exactSize:
                    if (stallInfo.getLength() == stack.getMaxStallLength() &&
                            stallInfo.getWidth() == stack.getIdealStallWidth()){
                        // TODO ED THE SEARCH
                    }
                    if (searchType != SearchParameter.anyFreeSpace) {break;}
                case correctHeight:

                    if (searchType != SearchParameter.anyFreeSpace) {break;}
                case emptyStack:

                    if (searchType != SearchParameter.anyFreeSpace) {break;}
            }
        }
        return null;
    }
}

