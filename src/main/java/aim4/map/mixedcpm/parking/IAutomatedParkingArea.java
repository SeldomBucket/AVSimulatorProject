package aim4.map.mixedcpm.parking;

import aim4.map.Road;
import aim4.map.RoadMap;
import aim4.map.connections.Junction;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public interface IAutomatedParkingArea extends RoadMap {
    public void update();
    public boolean tryResize(double newMinX);

    public AutomatedParkingRoad findTargetLane(VehicleSpec spec);
    public Road getEntryRoad();
    public Road getExitRoad();
}
