package aim4.map.mixedcpm.parking;

import java.awt.geom.Rectangle2D;

public class ManualStall extends StallInfo{

    ManualStall(StallInfo stallInfo) {
        super(stallInfo.width, stallInfo.length, stallInfo.type);
    }
}
