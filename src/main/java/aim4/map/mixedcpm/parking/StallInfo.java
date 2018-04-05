package aim4.map.mixedcpm.parking;

import aim4.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * StallInfo describes a stall, without referring to a specific, existing stall
 */
public class StallInfo {

    /**
     * maps stall type to the padding around the edge of the stall
     */
    private static final Map<StallType, Double[]> stallPadding;
    static
    {
        stallPadding = new HashMap<>();

        Double[] noPadding = {0.0, 0.0};
        stallPadding.put(StallType.NoPaddingTest, noPadding);

        Double[] standardPadding = {1.0, 1.0};
        stallPadding.put(StallType.Standard, standardPadding);

        // 1 metre buffer on outside compared to standard stall
        Double[] disabledPadding = {2.0, 2.0};
        stallPadding.put(StallType.Disabled, disabledPadding);
    }

    /** width of the stall */
    private double width;
    /** length of the stall */
    private double length;
    /** type of the stall */
    private StallType type;

    /**
     * constructor for StallInfo from another StallInfo
     * @param stallInfo StallInfo to copy
     */
    public StallInfo(StallInfo stallInfo)
    {
        this.width = stallInfo.getWidth();
        this.length = stallInfo.getLength();
        this.type = stallInfo.getType();
    }

    /**
     * Constructor for StallInfo
     * @param vehicleWidth the width of the vehicle to park in the stall
     * @param vehicleLength the length of the vehicle to park in the stall
     * @param type the type of stall
     */
    public StallInfo(double vehicleWidth, double vehicleLength, StallType type){
        this.type = type;
        // Round space size to nearest 10cm and add the padding needed for the vehicle
        this.width = Util.roundToDecimalPlaces(vehicleWidth, 1) + stallPadding.get(type)[0];
        this.length = Util.roundToDecimalPlaces(vehicleLength, 1) + stallPadding.get(type)[1];
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
    public StallType getType(){
        return type;
    }

}
