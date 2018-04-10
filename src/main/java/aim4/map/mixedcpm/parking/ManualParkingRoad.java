package aim4.map.mixedcpm.parking;

import aim4.map.Road;
import aim4.map.connections.Junction;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class ManualParkingRoad implements IManualParkingRoad {

    private Road centreRoad;
    private StallStack stallStackPair[];
    private boolean lastRoad;
    private String roadName;
    private boolean toBeDeleted;

    /** Parking area this road belongs to */
    private ManualParkingArea parkingArea;

    /**
     * Enum to show which type of space you want to search for
     */
    public enum SearchParameter {
        exactSize,
        correctLength,
        emptyStack,
        anyGap
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
        this.centreRoad = road;

        Road topRoad = parkingArea.getRoadByName("topRoad");
        Road bottomRoad = parkingArea.getRoadByName("bottomRoad");
        Rectangle2D roadShape = road.getOnlyLane().getShape().getBounds2D();
        Rectangle2D topRoadShape = topRoad.getOnlyLane().getShape().getBounds2D();
        Rectangle2D bottomRoadShape = bottomRoad.getOnlyLane().getShape().getBounds2D();

        this.parkingArea = parkingArea;
        this.stallStackPair =
                new StallStack[] {  new StallStack( roadShape.getMinX()-firstStackLength,
                                                    topRoadShape.getMaxY(),
                                                    bottomRoadShape.getMinY()-topRoadShape.getMaxY(),
                                                    firstStackLength,
                                                    false,
                                                    this,
                                                    this.parkingArea),
                                    new StallStack( roadShape.getMaxX(),
                                                    topRoadShape.getMaxY(),
                                                    bottomRoadShape.getMinY()-topRoadShape.getMaxY(),
                                                    0,
                                                    false,
                                                    this,
                                                    this.parkingArea)
                };
        this.parkingArea.update();
    }

    /**
     * get parking spaces from both stall stacks
     * @return list of parking spaces
     */
    public ArrayList<ManualStall> getManualStalls(){
        ArrayList<ManualStall> returnList = new ArrayList<>(stallStackPair[0].getManualStalls());
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
            stallStackPair[0].setLastStallStack(false);
            stallStackPair[1].setLastStallStack(true);
        } else {
            stallStackPair[0].setLastStallStack(false);
            stallStackPair[1].setLastStallStack(false);
        }
    }

    /**
     * Finds a new space based on the size of the stallSpec
     * @param stallSpec the parameters of the space to find
     * @param searchType the way the space should be searched for
     * @return the stall if it was found, null if not
     */
    public ManualStall findNewSpace(StallSpec stallSpec, SearchParameter searchType) {
        ManualStall returnStall = null;
        for (StallStack stack:stallStackPair) {
            switch (searchType) {
                case anyGap:
                case exactSize:
                    if (stallSpec.getLength() == stack.getMaxStallLength() &&
                            stallSpec.getWidth() == stack.getIdealStallWidth()){
                        returnStall = stack.addManualStall(stallSpec);
                    }
                    if (returnStall != null) {
                        this.toBeDeleted = false;
                        return returnStall;
                    }
                    if (!searchType.equals(SearchParameter.anyGap)) {break;}
                case correctLength:
                    if (stallSpec.getLength() == stack.getMaxStallLength()){
                        returnStall = stack.addManualStall(stallSpec);
                    }
                    if (returnStall != null) {
                        this.toBeDeleted = false;
                        return returnStall;
                    }
                    if (!searchType.equals(SearchParameter.anyGap)) {break;}
                case emptyStack:
                    if (0 == stack.getMaxStallLength()){
                        returnStall = stack.addManualStall(stallSpec);
                    }
                    if (returnStall != null) {
                        this.toBeDeleted = false;
                        return returnStall;
                    }
                    if (!searchType.equals(SearchParameter.anyGap)) {break;}
                default:
                    returnStall = stack.addManualStall(stallSpec);
                    if (returnStall != null) {
                        this.toBeDeleted = false;
                        return returnStall;
                    }
            }
        }
        return null;
    }

    public ManualStall getManualStallByName(String stallName){
        ManualStall returnStall = stallStackPair[0].getManualStallByName(stallName);
        if (returnStall == null){
            returnStall = stallStackPair[1].getManualStallByName(stallName);
        }
        return returnStall;
    }

    public void markForDelete() {
        this.toBeDeleted = true;
    }

    public boolean isToBeDeleted(){
        return this.toBeDeleted;
    }

    /**
     * Returns the stall stack
     * @return the pair of stall stacks
     */
    public StallStack[] getStallStackPair(){
        return stallStackPair;
    }

    public Point2D getStartPoint() {
        return centreRoad.getOnlyLane().getStartPoint();
    }

    public ArrayList<Road> getRoads() {
        ArrayList<Road> returnList = new ArrayList<>();
        returnList.add(centreRoad);
        for (ManualStall stall: getManualStalls()) {
            returnList.add(stall.getRoad());
        }
        return returnList;
    }

    public ArrayList<Junction> getJunctions(){
        return centreRoad.getJunctions();
    }
}

