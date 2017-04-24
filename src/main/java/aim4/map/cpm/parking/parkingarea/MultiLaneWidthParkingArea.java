package aim4.map.cpm.parking.parkingarea;


import aim4.map.Road;
import aim4.map.cpm.CPMMap;
import aim4.map.cpm.parking.ParkingLane;
import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Becci on 24-Apr-17.
 */
public class MultiLaneWidthParkingArea extends BasicParkingArea {

    private List<Pair<Integer, Double>> parkingLaneSets;

    public MultiLaneWidthParkingArea(Point2D startPoint, CPMMap map,
                                     double parkingLength, double accessLength,
                                     List<Pair<Integer, Double>> parkingLaneSets) {
        super(startPoint, map, parkingLength, accessLength);
        this.parkingLaneSets = parkingLaneSets;

        addParkingLanes();
    }

    public MultiLaneWidthParkingArea(int startPointX, int startPointY, CPMMap map,
                                     double parkingLength, double accessLength,
                                     List<Pair<Integer, Double>> parkingLaneSets) {
        this(new Point(startPointX, startPointY), map,
                parkingLength, accessLength, parkingLaneSets);
    }

    private void addParkingLanes() {
        // Calculate points for first lane from the start point
        double laneStartPointX = startPoint.getX();
        double laneEndPointX = laneStartPointX + totalLength;
        double firstLaneWidth = parkingLaneSets.get(0).getValue();
        double lanePointY = startPoint.getY() - (firstLaneWidth/2);

        for (int pairIndex = 0 ; pairIndex < parkingLaneSets.size() ; pairIndex++) {
            Pair<Integer, Double> pair = parkingLaneSets.get(pairIndex);
            int numberOfParkingLanes = pair.getKey();
            double laneWidth = pair.getValue();

            for (int i = 0 ; i < numberOfParkingLanes ; i++){

                // Create a road for the parking lane to belong to
                Road road = new Road("Parking road " + i, map);
                ParkingLane parkingLane = new ParkingLane(laneStartPointX,
                        lanePointY,
                        laneEndPointX,
                        lanePointY,
                        laneWidth,
                        accessLength,
                        overlappingRoadWidth,
                        map.getMaximumSpeedLimit(),
                        road);

                // Register the lane and add it to the road
                int laneId = parkingLaneRegistry.register(parkingLane);
                parkingLane.setId(laneId);
                road.addTheRightMostLane(parkingLane);
                parkingLanes.add(parkingLane);
                roads.add(road);

                // If its the first lane of the first parking lane set, set as the entry road.
                if (i == 0 && pair == parkingLaneSets.get(0)) {
                    entryRoad = road;
                }

                // If its the last lane of the last parking lane set, set it as the last road.
                if (i+1 == numberOfParkingLanes &&
                        pair == parkingLaneSets.get(parkingLaneSets.size()-1)){
                    // Then this is the last road
                    lastRoad = road;
                    break;
                }

                // Set up points for next lane
                // If last lane of non-last set, use lane width of next set
                if (i+1 == numberOfParkingLanes &&
                        pair != parkingLaneSets.get(parkingLaneSets.size()-1)) {
                    double nextLaneWidth = parkingLaneSets.get(pairIndex+1).getValue();
                    lanePointY = lanePointY - nextLaneWidth;
                }
                // If non-last lane of set, use the current lane width
                else if (i+1 != numberOfParkingLanes) {
                    lanePointY = lanePointY - laneWidth;
                }
                // If last lane of last set, don't bother
            }
        }
        assert(parkingLanes.size() == totalNumberOfParkingLanes());
        assert(roads.size() == totalNumberOfParkingLanes());
    }

    private int totalNumberOfParkingLanes() {
        int numberOfParkingLanes = 0;
        for (Pair<Integer,Double> pair : parkingLaneSets) {
            numberOfParkingLanes += pair.getKey();
        }
        return numberOfParkingLanes;
    }

    @Override
    public ArrayList<Road> calculateListOfRoads() {
        return null;
    }
}
