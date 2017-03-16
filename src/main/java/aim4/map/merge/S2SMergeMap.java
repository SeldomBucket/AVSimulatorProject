package aim4.map.merge;

import aim4.map.Road;
import aim4.map.aim.AIMSpawnPoint;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by Callum on 08/03/2017.
 */
public class S2SMergeMap extends MergeMap {

    /**
     * Creates the map for a Single-to-Single lane merge.
     * @param initTime The starting time for the map.
     * @param targetLaneSpeedLimit The speed limit of the target lane*
     * @param mergeLaneSpeedLimit The speed limit of the merging lane
     * @param targetLeadInDistance The distance between the target lane start and the merge point
     * @param targetLeadOutDistance The distance between the end of the target lane and the merge point
     * @param mergeLeadInDistance The length of the merging road as it leads into the merge point
     * @param mergeAngle The angle of approach for the merging road
     */
    public S2SMergeMap(double initTime,
                       double targetLaneSpeedLimit, double mergeLaneSpeedLimit,
                       double targetLeadInDistance, double targetLeadOutDistance,
                       double mergeLeadInDistance, double mergeAngle){
        //PRELIMINARY CALCULATIONS//
        /*
        We need to determine the length of the merging zone, as the lane comes in at an angle.
        The understanding for this calculation is as follows:
        1. Consider a right angled triangle at the merge point.
            - The hypotenuse is the length of the merge point.
            - The adjacent side is the upper edge of the merge lane.
            - The opposite side is the width of the lane. The side spans from where the lower edge of the merging lane
            meets meets the target lane, to the upper edge of the merging lane.
        2. The merging angle is the interior angle of this right angled triangle. So we can use h = o/sin(theta)
         */

        double mergeAngleRad = Math.toRadians(mergeAngle);
        double mergeZoneLength = LANE_WIDTH / Math.sin(mergeAngleRad);

        /*
        We need to find lengths that let us calculate the X and Y co-ordinates of the sides of the merging lane at it's
        entrance. We need to do this for two reasons. Firstly we need to set our data collection line between these two
        points. Secondly we need to add a small adjustment to the height and width of the merging lane rectangle
        calculated below to adjust for the width of the lane.
         1. Consider a right angle triangle.
            - The hypotenuse is on the line crossing the beginning of the merging lane. It runs from the lower point on
            the merging lane entrance to the middle of the lane.
            - The adjacent runs parallel to the target lane from the lower merging point.
            - The opposite is a line going from the middle of the merging lane entrance, down to the adjacent side.
         2. The interior angle of this triangle is 90 - the merging angle, because of the Z rule of angles.
         3. We can also consider a similar triangle with the same dimensions, except the hypotenuse runs from the middle
          of the merging lane entrance to the upper edge point of the merging lane entrance.
         4. We can use these triangles to calculate adjustment lengths from the middle of the merging entrance.
            - The X adjustment towards the upper edge point is given by a = h cos(90 - theta)
            - The Y adjustment towards the upper edge point is given by o = h sin(90 - theta)
            The adjustment lengths towards the lower edge point are the same but we taken them off the X and Y
            co-oridnates of the middle point, instead of adding them.
         */

        double mergeEntranceXAdjustment = (LANE_WIDTH/2) * Math.cos(90 - mergeAngleRad);
        double mergeEntranceYAdjustment = (LANE_WIDTH/2) * Math.sin(90 - mergeAngleRad);

        /*
        Now we need to determine the height and width of the merging lane, were it to be drawn in a box.
        We are using the middle of the lane to do our calculations.
        1. Consider a right angled triangle.
            - The hypotenuse is the merging lane centre line.
            - The adjacent side is the target lane.
            - The opposite side is drawn from the start point of the merging lane to the target lane straight down.
        2. The merging angle is the interior angle of this right angled triangle. We can use h * sin(theta) = o to work
         out the height.
        3. We can use h * cos(theta) = a to work out the length of the adjacent side.
         */

        double mergeHeight = mergeLeadInDistance * Math.sin(mergeAngleRad) + mergeEntranceYAdjustment;
        double mergeBaseWidth = mergeLeadInDistance * Math.cos(mergeAngleRad) +
                mergeEntranceXAdjustment + mergeZoneLength/2;

        /*
        We want the merging road to meet the target road in the middle of the lane, where the target road line runs.
        1. We need to calculate the length of the adjacent of one more right angled
        triangle.
            - The hypotenuse runs from the centre of the merging zone to the  target lane centre, continuing on the same
             angle as the merging lane.
            - The opposite runs from the top of the target lane to the centre.
            - The adjacent runs along the middle of the target lane.
        2. We know the length of the opposite side, half of the lane width. We need to find the adjacent. We can use
         o / tan(theta) = a to find the length.
         */
        double mergeLaneZoneToEndXAdjustment = (LANE_WIDTH/2) / Math.tan(mergeAngleRad);

        //DIMENSIONS
        /*
        We don't know if the target lane is going to be shorter than the base of the merging lane rectangle so we can't
        set our lengths quite yet.
        */
        double targetLaneLength = targetLeadInDistance + mergeZoneLength + targetLeadOutDistance;
        double width = 0;
        if(targetLaneLength > (mergeBaseWidth + targetLeadOutDistance))
            width = targetLaneLength;
        else
            width = mergeBaseWidth + targetLeadOutDistance;

        //With these two measurements we can set our dimensions.
        double height = LOWER_BUFFER + LANE_WIDTH + mergeHeight;
        setDimensions(new Rectangle2D.Double(0,0,width,height));

        //TARGET ROAD//
        /*First we're going to establish some basic lengths.*/
        double targetLaneCentreY = LOWER_BUFFER + LANE_WIDTH/2;
        double targetLaneLowerY = LOWER_BUFFER;
        double targetLaneUpperY = LOWER_BUFFER + LANE_WIDTH;

        double targetLaneStartX = width - targetLaneLength;
        double targetLaneEndX = width;

        /*Then we create the road and it's lane*/
        Road targetRoad = new Road("Target Road", this);
        Point2D targetLaneStart = new Point2D.Double(targetLaneStartX, targetLaneCentreY);
        Point2D targetLaneEnd = new Point2D.Double(targetLaneEndX, targetLaneCentreY);
        Line2D targetLaneLine = new Line2D.Double(
                targetLaneStart.getX(),
                targetLaneStart.getY(),
                targetLaneEnd.getX(),
                targetLaneEnd.getY()
        );
        Lane targetLane = new LineSegmentLane(
                targetLaneLine, LANE_WIDTH, targetLaneSpeedLimit);
        int targetLaneId = getLaneRegistry().register(targetLane);
        targetLane.setId(targetLaneId);
        targetRoad.addTheRightMostLane(targetLane);
        addLaneToRoad(targetLane, targetRoad);

        //MERGING ROAD//
        double mergeZoneCentreX = width - targetLeadOutDistance - (mergeZoneLength/2);
        double mergeLaneEndX = mergeZoneCentreX + mergeLaneZoneToEndXAdjustment;
        double mergeLaneEndY = targetLaneCentreY;
        double mergeLaneStartX = width - targetLeadOutDistance - mergeBaseWidth + mergeEntranceXAdjustment;
        double mergeLaneStartY = height - mergeEntranceYAdjustment;

        Road mergeRoad = new Road("Merging Road", this);
        Point2D mergeLaneStart = new Point2D.Double(mergeLaneStartX, mergeLaneStartY);
        Point2D mergeLaneEnd = new Point2D.Double(mergeLaneEndX, mergeLaneEndY);
        Line2D mergeLaneLine = new Line2D.Double(
                mergeLaneStart.getX(),
                mergeLaneStart.getY(),
                mergeLaneEnd.getX(),
                mergeLaneEnd.getY()
        );
        Lane mergeLane = new LineSegmentLane(
                mergeLaneLine, LANE_WIDTH, mergeLaneSpeedLimit);
        int mergeLaneId = getLaneRegistry().register(mergeLane);
        mergeLane.setId(mergeLaneId);
        mergeRoad.addTheRightMostLane(mergeLane);
        addLaneToRoad(mergeLane, mergeRoad);

        //ADD DATA COLLECTION LINES
        addDataCollectionLine("Target Lane Entrance",
                new Point2D.Double(targetLaneStartX, targetLaneUpperY),
                new Point2D.Double(targetLaneStartX, targetLaneLowerY),
                true);
        addDataCollectionLine("Target Lane Zone Entrance",
                new Point2D.Double(targetLaneStartX + targetLeadInDistance, targetLaneUpperY),
                new Point2D.Double(targetLaneStartX + targetLeadInDistance, targetLaneLowerY),
                true);
        addDataCollectionLine("Target Lane Exit",
                new Point2D.Double(width, targetLaneUpperY),
                new Point2D.Double(width, targetLaneLowerY),
                true);
        addDataCollectionLine("Merging Lane Entrance",
                new Point2D.Double(mergeLaneStartX + mergeEntranceXAdjustment,
                        mergeLaneStartY + mergeEntranceYAdjustment),
                new Point2D.Double(mergeLaneStartX - mergeEntranceXAdjustment,
                        mergeLaneStartY - mergeEntranceYAdjustment),
                true);
        addDataCollectionLine("Merge Lane Zone Entrance",
                new Point2D.Double(mergeZoneCentreX + mergeZoneLength/2, targetLaneUpperY),
                new Point2D.Double(mergeZoneCentreX - mergeZoneLength/2, targetLaneUpperY),
                true);
        addDataCollectionLine("Merge Zone Exit",
                new Point2D.Double(width - targetLeadOutDistance, targetLaneUpperY),
                new Point2D.Double(width - targetLeadOutDistance, targetLaneLowerY),
                true);

        //ADD ROADS TO roads.
        addRoad(mergeRoad);
        addRoad(targetRoad);

        //CREATE SPAWN POINTS
        MergeSpawnPoint targetSpawn = makeSpawnPoint(targetRoad.getLanes().get(0), 0.0, 0.0);
        MergeSpawnPoint mergeSpawn = makeSpawnPoint(mergeRoad.getLanes().get(0), 0.0, 0.0);
        addSpawnPoint(targetSpawn);
        addSpawnPoint(mergeSpawn);
    }


}
