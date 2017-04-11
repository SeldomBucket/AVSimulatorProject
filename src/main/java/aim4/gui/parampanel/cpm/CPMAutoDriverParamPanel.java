package aim4.gui.parampanel.cpm;

import aim4.gui.component.CPMUseCSVFileRadioButtons;
import aim4.gui.component.LabeledSlider;
import aim4.gui.component.CPMSpawnSpecRadioButtons;
import aim4.map.cpm.CPMMapUtil.*;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import javafx.util.Pair;

import javax.swing.*;

/**
 * The autonomous driver only simulation parameter panel for CPM.
 */
public class CPMAutoDriverParamPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    LabeledSlider speedLimitSlider;
    LabeledSlider laneWidthSlider;
    LabeledSlider numberOfParkingLanesSlider;
    LabeledSlider parkingLengthSlider;
    LabeledSlider accessLengthSlider;
    LabeledSlider trafficRateSlider;
    CPMSpawnSpecRadioButtons spawnSpecRadioButtons;
    CPMUseCSVFileRadioButtons useCSVFileRadioButtons;

    /**
     * Create the autonomous driver only simulation parameter panel.
     *
     * @param simSetup  the simulation setup
     */

    public CPMAutoDriverParamPanel(BasicCPMSimSetup simSetup) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // create the components

        speedLimitSlider =
                new LabeledSlider(1.0, 10.0,
                        simSetup.getSpeedLimit(),
                        1.0, 0.25,
                        "Speed Limit: %.2f meters/second",
                        "%.2f");
        add(speedLimitSlider);

        laneWidthSlider =
                new LabeledSlider(1.0, 5.0,
                        simSetup.getLaneWidth(),
                        1.0, 0.5,
                        "Width of Lanes (parking lanes and roads): %.1f meters",
                        "%.1f");
        add(laneWidthSlider);

        numberOfParkingLanesSlider =
                new LabeledSlider(1, 10,
                        simSetup.getNumberOfParkingLanes(),
                        1.0, 1.0,
                        "Number of Parking Lanes: %.0f",
                        "%.0f");
        add(numberOfParkingLanesSlider);

        parkingLengthSlider =
                new LabeledSlider(0.0, 100.0,
                        simSetup.getParkingLength(),
                        10.0, 5.0,
                        "Length of Parking: %.0f meters",
                        "%.0f");
        add(parkingLengthSlider);

        // TODO CPM What is minimum length for this?
        accessLengthSlider =
                new LabeledSlider(0.0, 5.0,
                        simSetup.getAccessLength(),
                        1.0, 1.0,
                        "Length of Parking Lane Access: %.0f meters",
                        "%.0f");
        add(accessLengthSlider);

        trafficRateSlider =
                new LabeledSlider(0.0, 2500.0,
                        simSetup.getTrafficLevel() * 3600.0,
                        500.0, 100.0,
                        "Traffic Level: %.0f vehicles/hour/lane",
                        "%.0f");
        add(trafficRateSlider);

        spawnSpecRadioButtons = new CPMSpawnSpecRadioButtons();
        add(spawnSpecRadioButtons);

        useCSVFileRadioButtons = new CPMUseCSVFileRadioButtons();
        add(useCSVFileRadioButtons);

    }


    public double getSpeedLimit() {
        return speedLimitSlider.getValue();
    }

    public double getLaneWidth() {
        return laneWidthSlider.getValue();
    }

    public double getParkingLength() {
        return parkingLengthSlider.getValue();
    }

    public double getAccessLength() {
        return accessLengthSlider.getValue();
    }

    public int getNumberOfParkingLanes() {
        return (int)numberOfParkingLanesSlider.getValue();
    }

    public double getTrafficRate() {
        return trafficRateSlider.getValue()/ 3600.0;
    }

    public SpawnSpecType getSpawnSpecType() {
        return SpawnSpecType.valueOf(spawnSpecRadioButtons.getSelected().getActionCommand());
    }

    /**
     * Get the details for using a CSV file for spawn times and parking times.
     * @return a pair (boolean, string), where the boolean is true if a CSV file is
     * to be used, and the string gives the location of the CSV file. If false,
     * the string is empty.
     */
    public Pair<Boolean, String> getUseCSVFileDetails() {
        String selectedButtonValue = useCSVFileRadioButtons.getSelected().getActionCommand();
        boolean useCSV = false;
        String fileLocation = "";
        if (selectedButtonValue == "TRUE") {
            useCSV = true;
            fileLocation = useCSVFileRadioButtons.getFileLocation();
        }
        Pair<Boolean, String> useCSVFilePair = new Pair<Boolean, String>(useCSV, fileLocation);
        return useCSVFilePair;
    }
}
