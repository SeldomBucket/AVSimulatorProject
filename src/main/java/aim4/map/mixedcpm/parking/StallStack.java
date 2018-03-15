package aim4.map.mixedcpm.parking;


import aim4.map.Road;
import aim4.map.RoadMap;
import aim4.map.connections.Junction;

import java.awt.geom.Rectangle2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class StallStack {
    /** The parkingSpaces in this stall stack */
    private ArrayList<ManualStall> stalls;
    /** The ideal width of a space in this stall */
    private double idealStallWidth = 0;
    /** Whether this is the last stall stack (i.e. the one on the outside edge) */
    private boolean lastStallStack;
    private Rectangle2D boundingBox;
    /** The parking road this stall stack belongs to */
    private IManualParkingRoad parkingRoad;
    private boolean roadOnLeft;
    private RoadMap map;

    /**
     * Constructor for a stall stack
     * @param x x position of the stall stack
     * @param y y position of the stall stack
     * @param stallStackHeight height of this stall stack
     * @param maxStallLength the maximum length of vehicle this stack is
     * @param lastStallStack if this is the stall stack to the left of the road
     * @param parkingRoad the ManualParkingRoad this stall stack belongs to
     */
    public StallStack(double x,
                      double y,
                      double stallStackHeight,
                      double maxStallLength,
                      boolean lastStallStack,
                      IManualParkingRoad parkingRoad,
                      RoadMap map){
        stalls = new ArrayList<>();
        this.lastStallStack = lastStallStack;
        boundingBox = new Rectangle2D.Double(x,y,maxStallLength,stallStackHeight);
        this.parkingRoad = parkingRoad;
        this.roadOnLeft = parkingRoad.getStartPoint().getX() < x;
        this.map = map;
    }

    // Public Methods

    /**
     * Get all the stalls in the stall stack
     * @return the list of stalls
     */
    public ArrayList<ManualStall> getManualStalls() {
        return stalls;
    }

    public void setLastStallStack(boolean lastStallStack){
        this.lastStallStack = lastStallStack;
    }

    /**
     * Attempts to add a manual stall
     * @param stallInfo The information of the stall which is being added
     * @return the ManualStall which was added, null if it wasn't possible
     */
    public ManualStall addManualStall(StallInfo stallInfo){
        double xPosition = roadOnLeft ?
                                this.boundingBox.getMinX():
                                this.boundingBox.getMaxX()-stallInfo.getLength();
        if (stalls.size() == 0){
            this.idealStallWidth = stallInfo.getWidth();
        }
        if (getMaxStallLength() == 0){
            // Set up rectangle
            setMaxStallLength(stallInfo.getLength());
            ManualStall parkingSpace =  new ManualStall(xPosition,
                                                        this.boundingBox.getMinY(),
                                                        stallInfo,
                                                        this,
                                                        this.parkingRoad,
                                                        this.map);
            this.stalls.add(parkingSpace);
            return parkingSpace;
        } else if (getMaxStallLength() >= stallInfo.getLength()){
            double yPosition = -1;
            if (stallInfo.getLength() == getMaxStallLength()){
                if (stallInfo.getWidth() == idealStallWidth){
                    //If length and ideal width match, search from top
                    yPosition = findSpace(stallInfo.getWidth(), true);
                }else{
                    //If length matches and ideal width doesn't, search from bottom
                    yPosition = findSpace(stallInfo.getWidth(), false);
                }
            }else if(stallInfo.getLength() < getMaxStallLength()){
                //If length doesn't match search from bottom
                yPosition = findSpace(stallInfo.getWidth(), false);
            }

            // Add parking space at position if a suitable gap was found
            if (-1 != yPosition) {
                ManualStall parkingSpace = new ManualStall( xPosition,
                                                            yPosition,
                                                            stallInfo,
                                                            this,
                                                            this.parkingRoad,
                                                            this.map);
                this.stalls.add(parkingSpace);
                return parkingSpace;
            }
        }
        return null;
    }

    /**
     * Find the x position of a gap which can fit the entire width of the specified space
     * @param spaceWidth The width of the space we are trying to fit into the stack
     * @param searchFromTop True if you want to search for a gap from top of the stall stack, False if you don't
     * @return the x position of the gap found, or -1 if a gap wasn't found
     */
    private double findSpace(double spaceWidth, boolean searchFromTop){
        Collections.sort(stalls, ManualStall.StallYComparater);
        if (stalls.size() == 0){
            return searchFromTop ? getBounds().getMinY() : getBounds().getMaxY() - spaceWidth;
        }

        if (searchFromTop){
            double lastSpaceYPosition = boundingBox.getMinY();
            for (int i = 0; i < stalls.size(); i++){
                if (lastSpaceYPosition != stalls.get(i).getMinY()){
                    if (lastSpaceYPosition + spaceWidth <= stalls.get(i).getMinY()){
                        return lastSpaceYPosition;
                    }
                }
                if (i == stalls.size()-1){
                    // If this is the last stall, check there is enough space in the end of the stall
                    if (stalls.get(i).getMaxY() + spaceWidth <= boundingBox.getMaxY()){
                        return stalls.get(i).getMaxY();
                    }
                }
                lastSpaceYPosition = stalls.get(i).getMaxY();
            }
        }else{
            double lastSpaceYPosition = boundingBox.getMaxY();
            for (int i = stalls.size()-1; i >= 0; i--){
                if (lastSpaceYPosition != stalls.get(i).getMaxY()){
                    if (lastSpaceYPosition - spaceWidth >= stalls.get(i).getMaxY()){
                        return lastSpaceYPosition - spaceWidth;
                    }
                }
                if (i == 0){
                    // If this is the last stall, check there is enough space in the stall
                    if (stalls.get(i).getMinY() - spaceWidth >= boundingBox.getMinY()){
                        return stalls.get(i).getMinY() - spaceWidth;
                    }
                }
                lastSpaceYPosition = stalls.get(i).getMinY();
            }
        }
        return -1;
    }

    /**
     * Removes a manual stall, and if this is the last stall stack, sets its length to 0
     * @param stallID the stall ID of the stall to be removed
     */
    public void removeManualStall(UUID stallID) {
        if (lastStallStack) {
            // If this is the last space in the last stall stack, set the length of this stall stack to 0

            boundingBox = new Rectangle2D.Double(this.boundingBox.getX(),
                                                 this.boundingBox.getY(),
                                                 0,
                                                 this.boundingBox.getHeight());
            idealStallWidth = 0;
        }

        ManualStall stallToRemove = getManualStallByID(stallID);

        if (stallToRemove != null) {
            if (stallToRemove.getJunction() != null){
                for (Road road : stallToRemove.getJunction().getRoads()){
                    if (road != stallToRemove.getRoad()){
                        road.removeJunction(stallToRemove.getJunction());
                    }
                }
                stallToRemove.getRoad().removeJunction(stallToRemove.getJunction());
            }
            stalls.remove(stallToRemove);
            if (parkingRoad.getManualStalls().size() == 0){
                parkingRoad.deleteFromMap();
            }
        }
    }

    public ManualStall getManualStallByID(UUID stallID){
        for (ManualStall stall:stalls){
            if (stall.getStallID() == stallID) {
                return stall;
            }
        }
        return null;
    }

    /**
     * Gets the maximum stall length this stall stack can fit
     * @return the maximum stall length
     */
    public double getMaxStallLength() {
        return boundingBox.getWidth();
    }

    /**
     * Gets the ideal stall width for this stall stack
     * @return the ideal stall width
     */
    public double getIdealStallWidth() {
        return idealStallWidth;
    }

    /**
     * Gets the bounding box of the stall stack
     * @return the rectangle representing the bounding box
     */
    public Rectangle2D getBounds() {
        return boundingBox;
    }

    public ArrayList<Junction> getJunctions(){
        ArrayList<Junction> junctions = new ArrayList<>();
        for (ManualStall stall : stalls){
            junctions.add(stall.getJunction());
        }
        return junctions;
    }

    // Private methods
    private void setMaxStallLength(double maxStallLength){
        boundingBox = new Rectangle2D.Double(this.boundingBox.getX(),
                this.boundingBox.getY(),
                maxStallLength,
                this.boundingBox.getHeight());
    }
}
