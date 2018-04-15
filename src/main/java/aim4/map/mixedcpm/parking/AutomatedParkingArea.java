package aim4.map.mixedcpm.parking;

import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.MixedCPMRoadMap;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AutomatedParkingArea extends MixedCPMRoadMap  implements IAutomatedParkingArea {

    private Road entryRoad;
    private Road exitRoad;
    private Rectangle2D dimensions;
    private MixedCPMBasicMap map;
    private List<AutomatedParkingRoad> parkingRoads = new ArrayList<>();

    public AutomatedParkingArea(Road topRoad, Road bottomRoad, double laneWidth, double speedLimit, MixedCPMBasicMap map, Rectangle2D dimensions){
        super(laneWidth, speedLimit);
        this.entryRoad = topRoad;
        this.exitRoad = bottomRoad;
        this.roads.add(topRoad);
        this.roads.add(bottomRoad);
        this.map = map;
        this.dimensions = dimensions;
        this.laneWidth = 2.5; // lane width is 2.5 as the largest vehicle is a VAN which is 2.014m wide
    }

    public AutomatedParkingRoad findTargetLane(VehicleSpec spec){
        for (AutomatedParkingRoad road: parkingRoads) {
            if (road.addVehicle(spec)){
                return road;
            }
        }

        String roadName = "AutomatedParkingRoad:" + UUID.randomUUID().toString();
        AutomatedParkingRoad road = addParkingRoad(roadName);
        if (road != null){
            return road;
        }

        return null;
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


    private AutomatedParkingRoad addParkingRoad(String roadName){
        double spacePointer = dimensions.getMaxX() - (parkingRoads.size() * laneWidth);

        // Don't add if it can't fit in the space
        if (this.dimensions.getMinX() > spacePointer - this.laneWidth){
            if (parkingRoads.size() == 0){
                if (!tryResize(this.dimensions.getMinX() - (this.laneWidth*2))){
                    return null;
                }
            }else if (!tryResize(this.dimensions.getMinX() - this.laneWidth)) {
                return null;
            }
        }

        double roadX = this.dimensions.getMinX() + (this.halfLaneWidth);

        AutomatedParkingRoad parkingRoad = this.makeParkingRoadWithOneLane(
                roadName,
                roadX,
                this.entryRoad.getOnlyLane().getShape().getBounds2D().getMinY(),
                roadX,
                this.exitRoad.getOnlyLane().getShape().getBounds2D().getMaxY(),
                laneWidth);

        this.makeJunction(this.entryRoad, parkingRoad);
        this.makeJunction(this.exitRoad, parkingRoad);

        this.parkingRoads.add(parkingRoad);

        return parkingRoad;
    }

    /**
     * Creates a road with one lane and adds it to the map
     * @param roadName the name of the road
     * @param x1 x of the centre of the start of the road
     * @param y1 y of the centre of the start of the road
     * @param x2 x of the centre of the end of the road
     * @param y2 y of the centre of the end of the road
     * @return
     */
    protected AutomatedParkingRoad makeParkingRoadWithOneLane(String roadName, double x1,
                                              double y1, double x2, double y2,
                                              double laneWidth){
        // Create the road
        AutomatedParkingRoad road = new AutomatedParkingRoad(roadName, this);
        // Add a lane to the road
        Lane lane = new LineSegmentLane(x1,
                y1,
                x2,
                y2,
                laneWidth,
                speedLimit);
        registerLane(lane);
        // Add lane to road
        road.addTheRightMostLane(lane);
        laneToRoad.put(lane, road);
        this.roads.add(road);

        return road;
    }



    public Road getEntryRoad() {
        return entryRoad;
    }

    public Road getExitRoad() {
        return exitRoad;
    }
}
