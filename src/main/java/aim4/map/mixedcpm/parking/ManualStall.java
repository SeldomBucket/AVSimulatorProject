package aim4.map.mixedcpm.parking;

import aim4.map.Road;

import java.awt.geom.Rectangle2D;
import java.util.Comparator;
import java.util.UUID;

public class ManualStall extends StallInfo implements Comparable<ManualStall>{
    //TODO ED ManualStall

    /** The ID of this manual stall */
    private UUID stallID;
    /** The stall stack this stall belongs to */
    private StallStack stallStack;
    private Rectangle2D boundingBox;
    private Road road;

    /**
     * Constructor for ManualStall
     * @param stallInfo the parameters of the stall
     * @param stallStack the StallStack this belongs to
     */
    ManualStall(double x, double y, StallInfo stallInfo, StallStack stallStack) {
        super(stallInfo);
        stallID = UUID.randomUUID();
        boundingBox = new Rectangle2D.Double(x,y,stallInfo.getLength(), stallInfo.getWidth());
        this.stallStack = stallStack;
        // TODO Road for parking in
    }

    public double getMinX(){
        return boundingBox.getMinX();
    }

    public double getMaxX(){
        return boundingBox.getMaxX();
    }

    public double getMinY(){
        return boundingBox.getMinY();
    }

    public double getMaxY(){
        return boundingBox.getMaxY();
    }
    /**
     * gets the ID of this stall
     * @return the ID
     */
    public UUID getStallID() {
        return stallID;
    }

    public Road getRoad(){
        return road;
    }

    @Override
    public int compareTo(ManualStall manualStall) {
        Double minY = this.getMinY();
        Double minYCompare = manualStall.getMinY();
        return minY.compareTo(minYCompare);
    }

    public static Comparator<ManualStall> StallYComparater = new Comparator<ManualStall>() {

        public int compare(ManualStall stall1, ManualStall stall2) {
            return stall1.compareTo(stall2);
        }
    };

}
