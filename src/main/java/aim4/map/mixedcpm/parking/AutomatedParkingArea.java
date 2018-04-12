package aim4.map.mixedcpm.parking;

import aim4.map.Road;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.MixedCPMRoadMap;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

import java.awt.geom.Rectangle2D;

public class AutomatedParkingArea implements IAutomatedParkingArea {

    Road topRoad;
    Road bottomRoad;
    Rectangle2D dimensions;
    MixedCPMBasicMap map;

    public AutomatedParkingArea(Road topRoad, Road bottomRoad, MixedCPMBasicMap map, Rectangle2D dimensions){
        this.topRoad = topRoad;
        this.bottomRoad = bottomRoad;
        this.map = map;
        this.dimensions = dimensions;
    }

    @Override
    public void addVehicleToMap(MixedCPMBasicManualVehicle vehicle) {

    }

    @Override
    public void update() {

    }

    @Override
    public boolean tryResize(double newMinX) {
        if (newMinX > dimensions.getMinX()){
            dimensions = new Rectangle2D.Double(newMinX,
                                                dimensions.getY(),
                                                dimensions.getMaxX() - newMinX,
                                                dimensions.getHeight());
            return true;
        }else{
            if (map.canResizeAutomatedArea(newMinX)) {
                dimensions = new Rectangle2D.Double(newMinX,
                                                    dimensions.getY(),
                                                    dimensions.getMaxX() - newMinX,
                                                    dimensions.getHeight());
                return true;
            }
        }
        return false;
    }

    @Override
    public Rectangle2D getDimensions() {
        return dimensions;
    }
}
