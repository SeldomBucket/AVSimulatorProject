package aim4.map.mixedcpm.maps;

import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.parking.*;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Test map which is just a standard manual car park (for testing the spaces)
 */
public class StaticMap extends MixedCPMBasicMap {

    IStatusMonitor statusMonitor;

    public StaticMap(double height, double width, double laneWidth, double speedLimit, double initTime){
        super(laneWidth,speedLimit,initTime);

        this.dimensions = new Rectangle2D.Double(0.0,0.0,width + BORDER*2,height + BORDER*2);

        this.dataCollectionLines = new ArrayList<>();

        initializeTopAndBottomRoads();

        this.spawnPoints = new ArrayList<>();

        this.spawnPoints.add(makeSpawnPoint(initTime, topRoad.getOnlyLane()));

        this.manualParkingArea = new ManualParkingArea(topRoad, bottomRoad, this, new Rectangle2D.Double(BORDER, BORDER, width, height));

        generateMap();

        statusMonitor = new StaticStatusMonitor(manualParkingArea);
    }

    @Override
    public IStatusMonitor getStatusMonitor() {
        return statusMonitor;
    }

    private void generateMap(){
        // Using essex2009parking as a reference :
        // https://www.essex.gov.uk/Environment%20Planning/Development-in-Essex/Documents/Parking_Standards.pdf
        //      5.5m x 2.9m stalls
        //      Assuming Shopping, recreation and leisure as car park type (page 20)
        //      for calculation of disabled spaces:
        //          < 200 spaces:  3 bays or 6% of total capacity, whichever is greater
        //         >= 200 spaces:  4 bays plus 4% of total capacity

        StallSpec standardStallSpec = new StallSpec(1.9, 4.5, StallType.Standard);
        StallSpec disabledStallSpec = new StallSpec(1.9,4.5, StallType.Disabled);

        ArrayList<ManualStall> stalls = new ArrayList<>();
        ManualStall newStall = null;
        do{
            newStall = manualParkingArea.findSpace(standardStallSpec);

            if (newStall == null){
                break;
            }

            stalls.add(newStall);
        }while (true);

        int totalCapacity = stalls.size();
        int noOfDisabledSpaces;

        if (totalCapacity < 200){
            noOfDisabledSpaces = Math.round(Math.max(3, (int)(totalCapacity*0.06)));
        }else{
            noOfDisabledSpaces = 4 + (int)(totalCapacity*0.04);
        }

        for (ManualStall stall : stalls){
            stall.delete();
        }

        stalls.clear();
        manualParkingArea.update();

        // Create the correct number of disabled spaces
        for (int i = 0; i < noOfDisabledSpaces; i++){
            stalls.add(manualParkingArea.findSpace(disabledStallSpec));
        }

        // Fill the rest with standard stalls
        do{
            newStall = manualParkingArea.findSpace(standardStallSpec);
            stalls.add(newStall);
        }while (newStall != null);

        newStall = null;
    }

}
