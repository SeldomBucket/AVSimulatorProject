package aim4.map.mixedcpm.parking;

import aim4.map.Road;
import aim4.map.RoadMap;
import aim4.map.connections.Junction;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ManualStall extends StallInfo implements Comparable<ManualStall>{
    //TODO ED ManualStall

    /** The ID of this manual stall */
    private String stallName;
    /** The stall stack this stall belongs to */
    private StallStack stallStack;
    private Rectangle2D boundingBox;
    private Road stallRoad;
    private IManualParkingRoad parkingRoad;
    private Junction junction;
    private boolean isToLeftOfParkingRoad;

    /**
     * Constructor for ManualStall
     * @param stallInfo the parameters of the stall
     * @param stallStack the StallStack this belongs to
     */
    ManualStall(double x, double y, StallInfo stallInfo, StallStack stallStack, IManualParkingRoad parkingRoad, RoadMap map) {
        super(stallInfo);
        stallName = "Stall:" + UUID.randomUUID();
        boundingBox = new Rectangle2D.Double(x,y,stallInfo.getLength(), stallInfo.getWidth());
        this.stallStack = stallStack;
        this.parkingRoad = parkingRoad;
        Road centreRoad = parkingRoad.getCentreRoad();
        if (centreRoad != null) {
            this.isToLeftOfParkingRoad = x < parkingRoad.getStartPoint().getX();
            double roadStartXPosition, roadYPosition, roadEndXPosition;
            if (isToLeftOfParkingRoad) {
                roadStartXPosition = centreRoad.getOnlyLane().getShape().getBounds2D().getMaxX();
                roadEndXPosition = x;
            } else {
                roadStartXPosition = centreRoad.getOnlyLane().getShape().getBounds2D().getMinX();
                roadEndXPosition = x + stallInfo.getLength();
            }
            roadYPosition = y + stallInfo.getWidth() / 2;

            LineSegmentLane lane = new LineSegmentLane(roadStartXPosition,
                    roadYPosition,
                    roadEndXPosition,
                    roadYPosition,
                    stallInfo.getWidth(),
                    1);
            ArrayList<Lane> lanes = new ArrayList<>();
            lanes.add(lane);
            this.stallRoad = new Road(stallName, lanes, map);

            List<Road> roadsForJunction = new ArrayList<Road>(2);
            roadsForJunction.add(this.stallRoad);
            roadsForJunction.add(centreRoad);
            junction = new Junction(roadsForJunction);
            this.stallRoad.addJunction(junction);
            centreRoad.addJunction(junction);
        }
    }

    public double getMinX(){
        return boundingBox.getMinX();
    }

    public double getMaxX(){
        return boundingBox.getMaxX();
    }

    public double getMinY(){
        return boundingBox.getMinY();
    }

    public double getMaxY(){
        return boundingBox.getMaxY();
    }

    public String getName(){
        return stallName;
    }

    public Road getRoad(){
        return stallRoad;
    }

    public Lane getLane(){
        return stallRoad.getOnlyLane();
    }

    public Junction getJunction(){
        return junction;
    }

    public boolean isLeftOfParkingRoad(){
        return isToLeftOfParkingRoad;
    }

    public void delete(){
        stallStack.removeManualStall(this.stallName);
    }

    public IManualParkingRoad getParkingRoad(){
        return this.parkingRoad;
    }

    @Override
    public int compareTo(ManualStall manualStall) {
        Double minY = this.getMinY();
        Double minYCompare = manualStall.getMinY();
        return minY.compareTo(minYCompare);
    }

    public static Comparator<ManualStall> StallYComparater = new Comparator<ManualStall>() {

        public int compare(ManualStall stall1, ManualStall stall2) {
            return stall1.compareTo(stall2);
        }
    };

}
