package aim4.gui.parampanel.cpm;

import aim4.gui.component.LabeledSlider;
import aim4.gui.parampanel.cpm.components.CPMLabeledSlider;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import aim4.sim.setup.cpm.CPMSingleWidthSimSetup;

import javax.swing.border.EmptyBorder;

/**
 * The parameter panel for CPM simulations where all lanes in the car park have the same width.
 */
public class CPMSingleWidthParamPanel extends CPMBasicParamPanel {

    LabeledSlider laneWidthSlider;
    LabeledSlider numberOfParkingLanesSlider;

    public CPMSingleWidthParamPanel(BasicCPMSimSetup simSetup) {
        super(simSetup);

        createAdditionalComponents(simSetup);
        addComponentsToPanel();
    }

    public double getLaneWidth() {
        return laneWidthSlider.getValue();
    }

    public int getNumberOfParkingLanes() {
        return (int) numberOfParkingLanesSlider.getValue();
    }


    @Override
    public void createAdditionalComponents(BasicCPMSimSetup simSetup) {
        assert simSetup instanceof CPMSingleWidthSimSetup;
        laneWidthSlider =
                new CPMLabeledSlider(1.0, 3.0,
                        ((CPMSingleWidthSimSetup)simSetup).getLaneWidth(),
                        1.0, 0.5,
                        "Width of Lanes (parking lanes and roads): %.1f meters",
                        "%.0f",
                        this);
        laneWidthSlider.setBorder(new EmptyBorder(3, 3, 3, 3));

        numberOfParkingLanesSlider =
                new CPMLabeledSlider(0, 50,
                        ((CPMSingleWidthSimSetup)simSetup).getNumberOfParkingLanes(),
                        10.0, 1.0,
                        "Number of Parking Lanes: %.0f",
                        "%.0f",
                        this);
        numberOfParkingLanesSlider.setBorder(new EmptyBorder(3, 3, 3, 3));
    }

    @Override
    public void addComponentsToPanel() {
        add(numberOfSimulationsInput);
        add(mapAreaLabel);
        add(laneWidthSlider);
        add(numberOfParkingLanesSlider);
        add(parkingLengthSlider);
        add(accessLengthSlider);
        add(trafficRateSlider);
        add(spawnSpecRadioButtons);
        add(useCSVFileRadioButtons);
        add(useSpecificSimTimeRadioButtons);
    }

    @Override
    public double calculateCarParkArea() {
        double parkingAreaLength = (2 * accessLengthSlider.getValue()) +
                (2 * laneWidthSlider.getValue()) + parkingLengthSlider.getValue();

        // Add the area of the parking area (w*h) (+1 to account for the top WEST road)
        double totalArea = parkingAreaLength * ((numberOfParkingLanesSlider.getValue() + 1) * laneWidthSlider.getValue());
        return totalArea;
    }
}
