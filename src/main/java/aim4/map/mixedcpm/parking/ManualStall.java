package aim4.map.mixedcpm.parking;

import java.awt.geom.Rectangle2D;

public class ManualStall extends StallInfo{

    ManualStall(StallInfo stallInfo) {
        super(stallInfo.getWidth(), stallInfo.getLength(), stallInfo.getType());
    }
}
