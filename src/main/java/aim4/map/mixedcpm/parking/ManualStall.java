package aim4.map.mixedcpm.parking;

import java.awt.geom.Rectangle2D;
import java.util.UUID;

public class ManualStall extends StallInfo{
    //TODO ED ManualStall

    /** The ID of this manual stall */
    private UUID stallID;
    /** The stall stack this stall belongs to */
    private StallStack stallStack;

    /**
     * Constructor for ManualStall
     * @param stallInfo the parameters of the stall
     * @param stallStack the StallStack this belongs to
     */
    ManualStall(StallInfo stallInfo, StallStack stallStack) {
        super(stallInfo.getWidth(), stallInfo.getLength(), stallInfo.getType());
        stallID = UUID.randomUUID();
    }

    /**
     * gets the ID of this stall
     * @return the ID
     */
    public UUID getStallID() {
        return stallID;
    }
}
