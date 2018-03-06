package aim4.map.mixedcpm.parking;

import java.util.HashMap;
import java.util.Map;

public class StallInfo {

    private static final Map<StallTypes, Double[]> stallPadding;
    static
    {
        stallPadding = new HashMap<>();
        
        Double[] standardPadding = {0.5, 0.5};
        stallPadding.put(StallTypes.Standard, standardPadding);
        
        Double[] disabledPadding = {1.0, 1.0};
        stallPadding.put(StallTypes.Disabled, disabledPadding);
    }

    private double width;
    private double length;
    private StallTypes type;

    public StallInfo(StallInfo stallInfo)
    {
        this.width = stallInfo.getWidth();
        this.length = stallInfo.getLength();
        this.type = stallInfo.getType();

    }

    public StallInfo(double vehicleWidth, double vehicleLength, StallTypes type){
        this.type = type;
        this.width = vehicleWidth + stallPadding.get(type)[0];
        this.width = vehicleLength + stallPadding.get(type)[1];
    }

    public double getWidth(){return width;}
    public double getLength(){return length;}
    public StallTypes getType(){return type;}

}
