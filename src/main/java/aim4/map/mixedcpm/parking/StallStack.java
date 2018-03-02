package aim4.map.mixedcpm.parking;

import java.awt.geom.Rectangle2D;

import java.util.ArrayList;

public class StallStack {
    /** The parkingSpaces in this stall stack */
    private ArrayList<ManualStall> stalls;
    private double x;
    private double y;
    private double laneLength;
    /** The maximum length of a stall in this stall stack */
    private double maxStallLength;
    /** The ideal width of a space in this stall*/
    private double idealStallWidth;
    private Rectangle2D boundingBox;
    private boolean leftStallStack;

    /**
     *
     * @param x x position of the stall stack
     * @param y y position of the stall stack
     * @param laneLength length of the lane this stall stack is attached to
     * @param maxStallLength the maximum length of vehicle this stack is
     * @param leftStallStack
     */
    public StallStack(double x, double y, double laneLength, double maxStallLength, boolean leftStallStack){
        stalls = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.laneLength = laneLength;
        this.maxStallLength = maxStallLength;
        this.leftStallStack = leftStallStack;
    }

    // Public Methods

    /**
     * Get all the stalls in the stall stack
     * @return the list of stalls
     */
    public ArrayList<ManualStall> getManualStalls() {
        return stalls;
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
            ManualStall parkingSpace =  new ManualStall(stallInfo);
        }
        return null;
    }


    // Private methods
}
