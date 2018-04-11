package aim4.gui.parampanel.mixedcpm;

import aim4.gui.component.LabeledSlider;
import aim4.gui.component.MixedCPMLogFileRadioButtons;
import aim4.gui.component.MixedCPMMapTypeRadioButtons;
import aim4.gui.component.MixedCPMSpawnSpecRadioButtons;
import aim4.map.mixedcpm.MixedCPMMapUtil.*;
import aim4.sim.setup.mixedcpm.BasicMixedCPMSimSetup;

import javax.swing.*;

/**
 * The manual driver only simulation parameter panel for CPM.
 */
public class MixedCPMManualDriverParamPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    LabeledSlider carParkWidthSlider;
    LabeledSlider carParkHeightSlider;
    LabeledSlider laneWidthSlider;
    LabeledSlider trafficRateSlider;
    MixedCPMLogFileRadioButtons logFileRadioButtons;
    MixedCPMSpawnSpecRadioButtons spawnSpecRadioButtons;
    MixedCPMMapTypeRadioButtons mapTypeRadioButtons;

    /**
     * Create the autonomous driver only simulation parameter panel.
     *
     * @param simSetup  the simulation setup
     */

    public MixedCPMManualDriverParamPanel(BasicMixedCPMSimSetup simSetup) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // create the components

        carParkWidthSlider =
                new LabeledSlider(0.0, 100.0,
                        simSetup.getCarParkWidth(),
                        10.0, 1.0,
                        "Width of car park: %.1f meters",
                        "%.1f");
        add(carParkWidthSlider);

        carParkHeightSlider =
                new LabeledSlider(0.0, 100.0,
                        simSetup.getCarParkHeight(),
                        10.0, 1.0,
                        "Height of car park: %.1f meters",
                        "%.1f");
        add(carParkHeightSlider);

        laneWidthSlider =
                new LabeledSlider(1.0, 10.0,
                        simSetup.getLaneWidth(),
                        1.0, 0.5,
                        "Width of Lanes (parking lanes and roads): %.1f meters \r\n (not used in static car park type)",
                        "%.1f");
        add(laneWidthSlider);


        trafficRateSlider =
                new LabeledSlider(0.0, 2500.0,
                        simSetup.getTrafficLevel() * 3600.0,
                        500.0, 100.0,
                        "Traffic Level: %.0f vehicles/hour \r\n (not used when spawning vehicles from a csv file)",
                        "%.0f");
        add(trafficRateSlider);

        logFileRadioButtons = new MixedCPMLogFileRadioButtons();
        add(logFileRadioButtons);

        mapTypeRadioButtons = new MixedCPMMapTypeRadioButtons();
        add(mapTypeRadioButtons);

        spawnSpecRadioButtons = new MixedCPMSpawnSpecRadioButtons();
        add(spawnSpecRadioButtons);


    }

    public double getCarParkWidth() {
        return carParkWidthSlider.getValue();
    }

    public double getCarParkHeight() {
        return carParkHeightSlider.getValue();
    }

    public double getLaneWidth() {
        return laneWidthSlider.getValue();
    }


    public double getTrafficRate() {
        return trafficRateSlider.getValue()/ 3600.0;
    }

    public SpawnSpecType getSpawnSpecType() {
        return SpawnSpecType.valueOf(spawnSpecRadioButtons.getSelected().getActionCommand());
    }

    public MapType getMapType(){
        return MapType.valueOf(mapTypeRadioButtons.getSelected().getActionCommand());
    }

    public boolean getUseCsv(){
        return spawnSpecRadioButtons.getSelected().getActionCommand().equals("CSV");
    }

    public String getCsvFilename(){
        return spawnSpecRadioButtons.getFileLocation();
    }

    public boolean getUseLogFile(){
        return logFileRadioButtons.getSelected();
    }

}
