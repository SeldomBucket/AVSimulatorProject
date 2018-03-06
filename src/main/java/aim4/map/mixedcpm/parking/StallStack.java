package aim4.map.mixedcpm.parking;

import java.awt.geom.Rectangle2D;

import java.util.ArrayList;
import java.util.UUID;

public class StallStack {
    /** The parkingSpaces in this stall stack */
    private ArrayList<ManualStall> stalls;
    private double x;
    private double y;
    private double laneLength;
    /** The maximum length of a stall in this stall stack */
    private double maxStallLength;
    /** The ideal width of a space in this stall */
    private double idealStallWidth = 0;
    /** Whether this is the last stall stack (i.e. the one on the outside edge) */
    private boolean lastStallStack;
    private Rectangle2D boundingBox;
    /** The parking road this stall stack belongs to */
    private ManualParkingRoad parkingRoad;

    /**
     *
     * @param x x position of the stall stack
     * @param y y position of the stall stack
     * @param laneLength length of the lane this stall stack is attached to
     * @param maxStallLength the maximum length of vehicle this stack is
     * @param lastStallStack if this is the stall stack to the left of the road
     */
    public StallStack(double x,
                      double y,
                      double laneLength,
                      double maxStallLength,
                      boolean lastStallStack,
                      ManualParkingRoad parkingRoad){
        stalls = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.laneLength = laneLength;
        this.maxStallLength = maxStallLength;
        this.lastStallStack = lastStallStack;
        this.parkingRoad = parkingRoad;
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
     * 
     * @param stallInfo The information of the stall which is being added
     * @return
     */
    public ManualStall addManualStall(StallInfo stallInfo){
        if (maxStallLength == 0){
            // Set up rectangle
            boundingBox = new Rectangle2D.Double(x, y, laneLength, stallInfo.getWidth());
            ManualStall parkingSpace =  new ManualStall(stallInfo, this);
        }
        return null;
    }

    public boolean removeManualStall(UUID stallID) {
        if (lastStallStack) {
            // If this is the last space in the last stall stack, set the height of this stall stack to 0
            maxStallLength = 0;
            idealStallWidth = 0;
        }
        return false;
    }

    public double getMaxStallLength() {
        return maxStallLength;
    }

    public double getIdealStallWidth() {
        return idealStallWidth;
    }

    // Private methods
}
