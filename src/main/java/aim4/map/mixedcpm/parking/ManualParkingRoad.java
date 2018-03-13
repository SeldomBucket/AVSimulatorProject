package aim4.map.mixedcpm.parking;

import aim4.map.Road;
import aim4.map.connections.Junction;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class ManualParkingRoad implements IManualParkingRoad {

    private Road centreRoad;
    private StallStack stallStackPair[];
    private boolean lastRoad;
    private String roadName;

    /** Parking area this road belongs to */
    private ManualParkingArea parkingArea;

    /**
     * Enum to show which type of space you want to search for
     */
    public enum SearchParameter {
        exactSize,
        correctLength,
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
                             Double firstStackLength) {
        this.roadName = road.getName();
        centreRoad = road;
        Road topRoad = parkingArea.getRoadByName("topRoad");
        stallStackPair =
                new StallStack[] {  new StallStack( road.getOnlyLane().getShape().getBounds2D().getMinX()-firstStackLength,
                                                    topRoad.getOnlyLane().getShape().getBounds2D().getMaxY(),
                                                    this.centreRoad.getOnlyLane().getLength() - parkingArea.getLaneWidth()*2,
                                                    firstStackLength,
                                                    false,
                                                    this,
                                                    this.parkingArea),
                                    new StallStack( road.getOnlyLane().getShape().getBounds2D().getMaxX(),
                                                    topRoad.getOnlyLane().getShape().getBounds2D().getMaxY(),
                                                    this.centreRoad.getOnlyLane().getLength() - parkingArea.getLaneWidth()*2,
                                                    0,
                                                    false,
                                                    this,
                                                    this.parkingArea)
                };
        this.parkingArea = parkingArea;
        this.parkingArea.update();
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
     * gets the name of this road (which is the same as the name of the centre road
     * @return the name
     */
    public String getName()
    {
        return this.roadName;
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
        ManualStall returnStall = null;
        for (StallStack stack:stallStackPair) {
            switch (searchType) {
                case anyFreeSpace:
                case exactSize:
                    if (stallInfo.getLength() == stack.getMaxStallLength() &&
                            stallInfo.getWidth() == stack.getIdealStallWidth()){
                        returnStall = stack.addManualStall(stallInfo);
                    }
                    if (returnStall != null) { return returnStall; }
                    if (!searchType.equals(SearchParameter.anyFreeSpace)) {break;}
                case correctLength:
                    if (stallInfo.getLength() == stack.getMaxStallLength()){
                        returnStall = stack.addManualStall(stallInfo);
                    }
                    if (returnStall != null) { return returnStall; }
                    if (!searchType.equals(SearchParameter.anyFreeSpace)) {break;}
                case emptyStack:
                    if (0 == stack.getMaxStallLength()){
                        returnStall = stack.addManualStall(stallInfo);
                    }
                    if (returnStall != null) { return returnStall; }
                    if (!searchType.equals(SearchParameter.anyFreeSpace)) {break;}
                default:

            }
        }
        return null;
    }

    /**
     * Returns the stall stack
     * @return the pair of stall stacks
     */
    public StallStack[] getStallStackPair(){
        return stallStackPair;
    }

    @Override
    public Point2D getStartPoint() {
        return centreRoad.getOnlyLane().getStartPoint();
    }

    @Override
    public ArrayList<Road> getRoads() {
        ArrayList<Road> returnList = new ArrayList<>();
        returnList.add(centreRoad);
        for (ManualStall stall: getParkingSpaces()) {
            returnList.add(stall.getRoad());
        }
        return returnList;
    }

    public ArrayList<Junction> getJunctions(){
        return centreRoad.getJunctions();
    }
}

