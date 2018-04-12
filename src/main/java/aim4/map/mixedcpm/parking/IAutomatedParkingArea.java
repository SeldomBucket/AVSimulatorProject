package aim4.map.mixedcpm.parking;

import aim4.map.Road;
import aim4.map.RoadMap;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public interface IAutomatedParkingArea  extends RoadMap {
    public void update();
    public boolean tryResize(double newMinX);
    public Road getRoadByName(String name);
    public AutomatedParkingRoad findTargetLane(VehicleSpec spec);
    public Rectangle2D getDimensions();
}
