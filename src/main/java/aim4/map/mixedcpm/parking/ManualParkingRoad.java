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

    /**
     * Constructor for ManualParkingRoad
     * @param road The road for the centre of this ManualParkingRoad
     * @param parkingArea the ManualParkingArea this ManualParkingRoad belongs to
     * @param firstStackLength the length of the first stack
     */
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

    /**
     * get parking spaces from both stall stacks
     * @return list of parking spaces
     */
    public ArrayList<ManualStall> getParkingSpaces(){
        ArrayList<ManualStall> returnList = stallStackPair[0].getManualStalls();
        returnList.addAll(stallStackPair[1].getManualStalls());
        return returnList;
    }

    /**
     * gets the centre road
     * @return the centre road
     */
    public Road getCentreRoad() {
        return centreRoad;
    }

    /**
     * gets the width of the entire ManualParkingRoad, including both stall stacks
     * @return the entire width of this ManualParkingRoad
     */
    public double getEntireWidth() {
        return centreRoad.getOnlyLane().getWidth() +
                stallStackPair[0].getMaxStallLength() +
                stallStackPair[1].getMaxStallLength();
    }

    /**
     * gets ID of this road
     * @return the ID
     */
    public UUID getID()
    {
        return this.roadID;
    }

    /**
     * marks this as the last road, and marks the last stall stack if applicable
     * @param lastRoad whether this road is the last road or not
     */
    public void setLastRoad(boolean lastRoad){
        this.lastRoad = lastRoad;
        if (lastRoad){
            stallStackPair[1].setLastStallStack(true);
        } else {
            stallStackPair[0].setLastStallStack(false);
            stallStackPair[1].setLastStallStack(false);
        }
    }

    /**
     * Finds a new space based on the size of the stallInfo
     * @param stallInfo the parameters of the space to find
     * @param searchType the way the space should be searched for
     * @return the stall if it was found, null if not
     */
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

