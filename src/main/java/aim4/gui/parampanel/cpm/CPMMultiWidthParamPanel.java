package aim4.gui.parampanel.cpm;

import aim4.gui.parampanel.cpm.components.CPMMultiParkingLaneWidthsConfig;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import javafx.util.Pair;

import java.util.List;

/**
 * The parameter panel for CPM simulations where the widths of
 * lanes in the car park can vary.
 */
public class CPMMultiWidthParamPanel extends CPMBasicParamPanel {

    CPMMultiParkingLaneWidthsConfig variedParkingLaneWidthsConfig;

    public CPMMultiWidthParamPanel(BasicCPMSimSetup simSetup) {
        super(simSetup);

        createAdditionalComponents(simSetup);
        addComponentsToPanel();
    }

    @Override
    public void createAdditionalComponents(BasicCPMSimSetup simSetup) {
        variedParkingLaneWidthsConfig = new CPMMultiParkingLaneWidthsConfig(simSetup, this);
    }

    public List<Pair<Integer, Double>> getParkingLaneSets() {
        return variedParkingLaneWidthsConfig.getParkingLaneSetValues();
    }

    @Override
    public void addComponentsToPanel() {
        add(numberOfSimulationsInput);
        add(mapAreaLabel);
        add(variedParkingLaneWidthsConfig);
        add(parkingLengthSlider);
        add(accessLengthSlider);
        add(trafficRateSlider);
        add(spawnSpecRadioButtons);
        add(useCSVFileRadioButtons);
        add(useSpecificSimTimeRadioButtons);
    }

    @Override
    public double calculateCarParkArea() {
        double parkingAreaLength = (2 * accessLengthSlider.getValue()) // either side of the parking length
                + (2 * getMaxLaneWidth()) // roads either side of the parking area
                + parkingLengthSlider.getValue();

        double parkingAreaHeight = calculateParkingAreaHeight();
        parkingAreaHeight += getMaxLaneWidth();

        // Add the area of the parking area (l*h) (+ getMaxLaneWidth() to account for top WEST road)
        double totalArea = parkingAreaLength * parkingAreaHeight;
        return totalArea;
    }

    private double getMaxLaneWidth() {
        List<Pair<Integer, Double>> parkingLaneSetValues = variedParkingLaneWidthsConfig.getParkingLaneSetValues();

        double maxLaneWidth = 0.0;
        for (Pair<Integer, Double> pair : parkingLaneSetValues) {
            if (pair.getValue() > maxLaneWidth){
                maxLaneWidth = pair.getValue();
            }
        }
        return maxLaneWidth;
    }

    private double calculateParkingAreaHeight() {
        List<Pair<Integer, Double>> parkingLaneSetValues = variedParkingLaneWidthsConfig.getParkingLaneSetValues();
        double height = 0.0;
        for (Pair<Integer, Double> pair : parkingLaneSetValues) {
            height += pair.getKey() * pair.getValue();
        }
        return height;
    }
}
