package aim4.map.mixedcpm.parking;

import java.awt.geom.Rectangle2D;
import java.util.UUID;

public class ManualStall extends StallInfo{
    private UUID stallID;
    /** The stall stack this stall belongs to */
    private StallStack stallStack;

    ManualStall(StallInfo stallInfo, StallStack stallStack) {
        super(stallInfo.getWidth(), stallInfo.getLength(), stallInfo.getType());
        stallID = UUID.randomUUID();
    }

    public UUID getStallID() {
        return stallID;
    }
}
