package aim4.map.mixedcpm.parking;

import aim4.map.Road;
import aim4.vehicle.VehicleSpec;

import java.awt.geom.Rectangle2D;

public class AutomatedParkingRoad extends Road {

    private IAutomatedParkingArea parkingArea;
    private double startOfLaneYCoord;
    private double endOfLaneYCoord;
    private double backOfVehiclesPointer;
    private double minDistanceBetweenVehicles = 0.1;

    /**
     * Constructor for ManualParkingRoad
     * @param parkingArea the ManualParkingArea this ManualParkingRoad belongs to
     */
    public AutomatedParkingRoad(String roadName,
                                IAutomatedParkingArea parkingArea) {
        super(roadName, parkingArea);

        Road topRoad = parkingArea.getRoadByName("topRoad");
        Road bottomRoad = parkingArea.getRoadByName("bottomRoad");

        Rectangle2D topRoadShape = topRoad.getOnlyLane().getShape().getBounds2D();
        Rectangle2D bottomRoadShape = bottomRoad.getOnlyLane().getShape().getBounds2D();

        this.startOfLaneYCoord = topRoadShape.getMaxX();
        this.endOfLaneYCoord = bottomRoadShape.getMinY();

        this.backOfVehiclesPointer = this.endOfLaneYCoord;

        this.parkingArea = parkingArea;

        this.parkingArea.update();
    }

    public boolean addVehicle(VehicleSpec spec){
        double tempBackOfVehiclesPointer = this.backOfVehiclesPointer;
        tempBackOfVehiclesPointer -= (spec.getLength() + minDistanceBetweenVehicles);
        if (this.startOfLaneYCoord <= tempBackOfVehiclesPointer){
            this.backOfVehiclesPointer = tempBackOfVehiclesPointer;
            return true;
        }
        return false;
    }

    public double getEndOfLaneYCoord() {
        return endOfLaneYCoord;
    }
}
