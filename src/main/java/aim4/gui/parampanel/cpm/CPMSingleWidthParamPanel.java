package aim4.gui.parampanel.cpm;

import aim4.gui.component.LabeledSlider;
import aim4.gui.parampanel.cpm.components.CPMLabeledSlider;
import aim4.sim.setup.cpm.BasicCPMSimSetup;

import javax.swing.border.EmptyBorder;

/**
 * The autonomous driver only simulation parameter panel for CPM.
 */
public class CPMSingleWidthParamPanel extends CPMBasicParamPanel {

    LabeledSlider laneWidthSlider;
    LabeledSlider numberOfParkingLanesSlider;


    /**
     * Create the autonomous driver only simulation parameter panel.
     *
     * @param simSetup the simulation setup
     */

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
        laneWidthSlider =
                new CPMLabeledSlider(1.0, 3.0,
                        simSetup.getLaneWidth(),
                        1.0, 0.5,
                        "Width of Lanes (parking lanes and roads): %.2f meters",
                        "%.0f",
                        this);

        numberOfParkingLanesSlider =
                new CPMLabeledSlider(0, 50,
                        simSetup.getNumberOfParkingLanes(),
                        10.0, 1.0,
                        "Number of Parking Lanes: %.0f",
                        "%.0f",
                        this);
        numberOfParkingLanesSlider.setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    @Override
    public void addComponentsToPanel() {
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
        double parkingAreaLength = (2*accessLengthSlider.getValue()) +
                (2*laneWidthSlider.getValue()) + parkingLengthSlider.getValue();

        // Add the area of the parking area (w*h) (+1 to account for the top WEST road)
        double totalArea = parkingAreaLength*((numberOfParkingLanesSlider.getValue()+1)*laneWidthSlider.getValue());
        return totalArea;
    }
}
