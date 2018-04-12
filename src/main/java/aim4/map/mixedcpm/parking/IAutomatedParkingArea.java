package aim4.map.mixedcpm.parking;

import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public interface IAutomatedParkingArea {
    public void addVehicleToMap(MixedCPMBasicManualVehicle vehicle);
    public void update();
    public boolean tryResize(double newMinX);

    public Rectangle2D getDimensions();
}
