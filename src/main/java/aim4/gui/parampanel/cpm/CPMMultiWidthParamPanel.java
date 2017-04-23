package aim4.gui.parampanel.cpm;

import aim4.gui.parampanel.cpm.components.CPMMultiParkingLaneWidthsConfig;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import javafx.util.Pair;

import javax.swing.*;
import java.util.List;

/**
 * The parameter panel for CPM simulations where the widths of
 * lanes in the car park can vary.
 */
public class CPMMultiWidthParamPanel extends CPMBasicParamPanel  {

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
        return 0;
    }
}
