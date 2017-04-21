package aim4.gui.parampanel.cpm;

import aim4.gui.component.CPMSimTimeRadioButtons;
import aim4.gui.component.CPMUseCSVFileRadioButtons;
import aim4.gui.component.LabeledSlider;
import aim4.gui.component.CPMSpawnSpecConfig;
import aim4.map.cpm.CPMMapUtil.*;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import aim4.util.Util;
import javafx.util.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * The autonomous driver only simulation parameter panel for CPM.
 */
public class CPMAutoDriverParamPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    LabeledSlider laneWidthSlider;
    LabeledSlider numberOfParkingLanesSlider;
    LabeledSlider parkingLengthSlider;
    LabeledSlider accessLengthSlider;
    LabeledSlider trafficRateSlider;
    CPMSpawnSpecConfig spawnSpecRadioButtons;
    CPMUseCSVFileRadioButtons useCSVFileRadioButtons;
    CPMSimTimeRadioButtons useSpecificSimTimeRadioButtons;
    private CPMMapAreaLabel mapAreaLabel;

    /**
     * Create the autonomous driver only simulation parameter panel.
     *
     * @param simSetup  the simulation setup
     */

    public CPMAutoDriverParamPanel(BasicCPMSimSetup simSetup) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // create the components
        mapAreaLabel = new CPMMapAreaLabel("Total area of car park (square metres): ", simSetup);
        mapAreaLabel.setOpaque(true);
        mapAreaLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(mapAreaLabel);

        laneWidthSlider =
                new CPMLabeledSlider(1.0, 3.0,
                        simSetup.getLaneWidth(),
                        1.0, 0.5,
                        "Width of Lanes (parking lanes and roads): %.2f meters",
                        "%.0f",
                        this);
        add(laneWidthSlider);

        numberOfParkingLanesSlider =
                new CPMLabeledSlider(0, 50,
                        simSetup.getNumberOfParkingLanes(),
                        10.0, 1.0,
                        "Number of Parking Lanes: %.0f",
                        "%.0f",
                        this);
        numberOfParkingLanesSlider.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(numberOfParkingLanesSlider);

        parkingLengthSlider =
                new CPMLabeledSlider(0.0, 60.0,
                        simSetup.getParkingLength(),
                        5.0, 0.5,
                        "Length of Parking: %.1f meters",
                        "%.0f",
                        this);
        parkingLengthSlider.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(parkingLengthSlider);

        // TODO CPM What is minimum length for this?
        accessLengthSlider =
                new CPMLabeledSlider(0.0, 5.0,
                        simSetup.getAccessLength(),
                        1.0, 0.25,
                        "Length of Parking Lane Access: %.0f meters",
                        "%.0f",
                        this);
        accessLengthSlider.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(accessLengthSlider);

        trafficRateSlider =
                new CPMLabeledSlider(0.0, 2500.0,
                        simSetup.getTrafficLevel() * 3600.0,
                        500.0, 100.0,
                        "Traffic Level: %.0f vehicles/hour/lane",
                        "%.0f",
                        this);
        trafficRateSlider.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(trafficRateSlider);

        JLabel spawnSpecLabel = new JLabel("Choose vehicle specification spawn type:");
        spawnSpecLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(spawnSpecLabel);
        spawnSpecRadioButtons = new CPMSpawnSpecConfig();
        spawnSpecRadioButtons.setBorder(new EmptyBorder(0, 5, 5, 5));
        add(spawnSpecRadioButtons);

        JLabel useCsvLabel = new JLabel("Use a CSV file to specify spawn times and parking times:");
        useCsvLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(useCsvLabel);
        useCSVFileRadioButtons = new CPMUseCSVFileRadioButtons();
        useCSVFileRadioButtons.setBorder(new EmptyBorder(0, 5, 5, 5));
        add(useCSVFileRadioButtons);

        JLabel simTimeLabel = new JLabel("Run the simulation for:");
        simTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(simTimeLabel);
        useSpecificSimTimeRadioButtons = new CPMSimTimeRadioButtons();
        useSpecificSimTimeRadioButtons.setBorder(new EmptyBorder(0, 5, 5, 5));
        add(useSpecificSimTimeRadioButtons);

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

    public List<Double> getMixedSpawnDistribution(){ return spawnSpecRadioButtons.getVehicleSpecDistribution(); }

    public CPMMapAreaLabel getMapAreaLabel() { return mapAreaLabel; }

    public SpawnSpecType getSpawnSpecType() {
        return SpawnSpecType.valueOf(spawnSpecRadioButtons.getSelected().getActionCommand());
    }

    public String getSingleSpawnSpecName() { return spawnSpecRadioButtons.getSelectedSingleSpec(); }

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

    public Pair<Boolean, Double> getUseSpecificSimTimeDetails() {
        String selectedButtonValue = useSpecificSimTimeRadioButtons.getSelected().getActionCommand();
        boolean useSpecificSimTime = false;
        Double simTime = -1.0;
        if (selectedButtonValue == "TRUE") {
            useSpecificSimTime = true;
            String simTimeString = useSpecificSimTimeRadioButtons.getHours() + ":" +
                    useSpecificSimTimeRadioButtons.getMinutes() + ":" +
                    useSpecificSimTimeRadioButtons.getSeconds();
            simTime = Util.convertTimeStringToSeconds(simTimeString);
        }
        Pair<Boolean, Double> useSpecificSimTimePair = new Pair<Boolean, Double>(useSpecificSimTime, simTime);
        return useSpecificSimTimePair;
    }
}
