package aim4.map.cpm.parking;

import aim4.map.lane.LineSegmentLane;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * A lane where vehicles can park.
 */
public class ParkingLane extends LineSegmentLane {
    /**The point on the lane where the parking section begins.*/
    private Point2D parkingStartPoint;
    /**The point on the lane where the parking section ends.*/
    private Point2D parkingEndPoint;
    /**The length of the access section of the lane, used to enter and exit the parking lane.*/
    private double accessLength;
    /**The width of the vertical roads the parking lanes will overlap with. */
    private double overlappingRoadWidth;

    public ParkingLane(Line2D line, double laneWidth, double accessLength,
                       double overlappingRoadWidth, double speedLimit) {
        super(line, laneWidth, speedLimit);
        this.accessLength = accessLength;
        this.overlappingRoadWidth = overlappingRoadWidth;

        // Calculate the start and end end of the parking section of this lane
        this.parkingStartPoint = new Point2D.Double(
                this.getStartPoint().getX() + overlappingRoadWidth + accessLength,
                this.getStartPoint().getY());

        this.parkingEndPoint = new Point2D.Double(
                this.getEndPoint().getX() - overlappingRoadWidth - accessLength,
                this.getEndPoint().getY());
    }

    public ParkingLane(Point2D p1, Point2D p2, double laneWidth, double accessLength,
                       double overlappingRoadWidth, double speedLimit) {
        this(new Line2D.Double(p1, p2), laneWidth, accessLength, overlappingRoadWidth, speedLimit);
    }

    public ParkingLane(double x1, double y1, double x2, double y2, double laneWidth,
                       double accessLength, double overlappingRoadWidth, double speedLimit) {
        this(new Line2D.Double(x1, y1, x2, y2), laneWidth, accessLength, overlappingRoadWidth, speedLimit);
    }

    public Point2D getParkingStartPoint() { return parkingStartPoint; }

    public Point2D getParkingEndPoint() { return parkingEndPoint;}
    
}
