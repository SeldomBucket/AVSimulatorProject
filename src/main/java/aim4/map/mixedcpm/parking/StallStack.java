package aim4.map.mixedcpm.parking;

import java.awt.geom.Rectangle2D;

import java.util.ArrayList;
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
    private ManualParkingRoad parkingRoad;

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
                      ManualParkingRoad parkingRoad){
        stalls = new ArrayList<>();
        this.lastStallStack = lastStallStack;
        this.parkingRoad = parkingRoad;
        boundingBox = new Rectangle2D.Double(x,y,maxStallLength,stallStackHeight);
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
        // TODO ED addManualStall
        if (getMaxStallLength() == 0){
            // Set up rectangle
            setMaxStallLength(stallInfo.getLength());
            ManualStall parkingSpace =  new ManualStall(stallInfo, this);
        }
        return null;
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
                                                this.boundingBox.getHeight(),
                                                0);
            idealStallWidth = 0;
        }
        // TODO ED removeManualStall
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

    // Private methods
    private void setMaxStallLength(double maxStallLength){
        boundingBox = new Rectangle2D.Double(this.boundingBox.getX(),
                this.boundingBox.getY(),
                this.boundingBox.getHeight(),
                maxStallLength);
    }
}
