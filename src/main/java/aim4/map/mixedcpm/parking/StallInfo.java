package aim4.map.mixedcpm.parking;

import java.util.HashMap;
import java.util.Map;

/**
 * StallInfo describes a stall, without referring to a specific, existing stall
 */
public class StallInfo {

    /**
     * maps stall type to the padding around the edge of the stall
     */
    private static final Map<StallTypes, Double[]> stallPadding;
    static
    {
        stallPadding = new HashMap<>();

        Double[] noPadding = {0.0, 0.0};
        stallPadding.put(StallTypes.NoPaddingTest, noPadding);

        Double[] standardPadding = {0.5, 0.5};
        stallPadding.put(StallTypes.Standard, standardPadding);
        
        Double[] disabledPadding = {1.0, 1.0};
        stallPadding.put(StallTypes.Disabled, disabledPadding);
    }

    /** width of the stall */
    private double width;
    /** length of the stall */
    private double length;
    /** type of the stall */
    private StallTypes type;

    /**
     * constructor for StallInfo from another StallInfo
     * @param stallInfo StallInfo to copy
     */
    public StallInfo(StallInfo stallInfo)
    {
        this(stallInfo.getWidth(), stallInfo.getLength(),stallInfo.getType());
    }

    /**
     * Constructor for StallInfo
     * @param vehicleWidth the width of the vehicle to park in the stall
     * @param vehicleLength the length of the vehicle to park in the stall
     * @param type the type of stall
     */
    public StallInfo(double vehicleWidth, double vehicleLength, StallTypes type){
        this.type = type;
        this.width = vehicleWidth + stallPadding.get(type)[0];
        this.width = vehicleLength + stallPadding.get(type)[1];
    }

    /**
     * gets width
     * @return width of the stall
     */
    public double getWidth(){
        return width;
    }

    /**
     * gets the length of the stall
     * @return the length of the stall
     */
    public double getLength(){
        return length;
    }

    /**
     * gets the type of the stall
     * @return the type of the stall
     */
    public StallTypes getType(){
        return type;
    }

}
