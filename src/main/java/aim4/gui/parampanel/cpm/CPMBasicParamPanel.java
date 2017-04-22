package aim4.gui.parampanel.cpm;

import aim4.gui.component.LabeledSlider;
import aim4.gui.parampanel.cpm.components.*;
import aim4.map.cpm.CPMMapUtil;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import aim4.util.Util;
import javafx.util.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Created by Becci on 22-Apr-17.
 */
public abstract class CPMBasicParamPanel extends JPanel implements CPMParamPanel {

    private static final long serialVersionUID = 1L;

    protected LabeledSlider parkingLengthSlider;
    protected LabeledSlider accessLengthSlider;
    protected LabeledSlider trafficRateSlider;
    protected CPMSpawnSpecConfig spawnSpecRadioButtons;
    protected CPMUseCSVFileRadioButtons useCSVFileRadioButtons;
    protected CPMSimTimeRadioButtons useSpecificSimTimeRadioButtons;
    protected CPMMapAreaLabel mapAreaLabel;

    public CPMBasicParamPanel(BasicCPMSimSetup simSetup) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        // create the components
        mapAreaLabel = new CPMMapAreaLabel("Total area of car park (square metres): ", simSetup);
        mapAreaLabel.setOpaque(true);
        mapAreaLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

        parkingLengthSlider =
                new CPMLabeledSlider(0.0, 60.0,
                        simSetup.getParkingLength(),
                        5.0, 0.5,
                        "Length of Parking: %.1f meters",
                        "%.0f",
                        this);
        parkingLengthSlider.setBorder(new EmptyBorder(5, 5, 5, 5));

        // TODO CPM What is minimum length for this?
        accessLengthSlider =
                new CPMLabeledSlider(0.0, 5.0,
                        simSetup.getAccessLength(),
                        1.0, 0.25,
                        "Length of Parking Lane Access: %.0f meters",
                        "%.0f",
                        this);
        accessLengthSlider.setBorder(new EmptyBorder(5, 5, 5, 5));

        trafficRateSlider =
                new CPMLabeledSlider(0.0, 2500.0,
                        simSetup.getTrafficLevel() * 3600.0,
                        500.0, 100.0,
                        "Traffic Level: %.0f vehicles/hour/lane",
                        "%.0f",
                        this);
        trafficRateSlider.setBorder(new EmptyBorder(5, 5, 5, 5));

        spawnSpecRadioButtons = new CPMSpawnSpecConfig();
        spawnSpecRadioButtons.setBorder(new EmptyBorder(0, 5, 5, 5));

        useCSVFileRadioButtons = new CPMUseCSVFileRadioButtons();
        useCSVFileRadioButtons.setBorder(new EmptyBorder(0, 5, 5, 5));

        useSpecificSimTimeRadioButtons = new CPMSimTimeRadioButtons();
        useSpecificSimTimeRadioButtons.setBorder(new EmptyBorder(0, 5, 5, 5));
    }

    public double getParkingLength() {
        return parkingLengthSlider.getValue();
    }

    public double getAccessLength() {
        return accessLengthSlider.getValue();
    }

    public double getTrafficRate() {
        return trafficRateSlider.getValue()/ 3600.0;
    }

    public List<Double> getMixedSpawnDistribution(){
        if (!spawnSpecRadioButtons.getSelected().getActionCommand().equals("MIXED")) {
            return null;
        }
        return spawnSpecRadioButtons.getVehicleSpecDistribution();
    }

    public CPMMapAreaLabel getMapAreaLabel() { return mapAreaLabel; }

    public CPMMapUtil.SpawnSpecType getSpawnSpecType() {
        return CPMMapUtil.SpawnSpecType.valueOf(spawnSpecRadioButtons.getSelected().getActionCommand());
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
