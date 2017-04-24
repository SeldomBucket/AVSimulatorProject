package aim4.gui.screen.cpm.components.simspecific;

import aim4.gui.screen.cpm.components.CPMStatScreenComponent;
import aim4.gui.setuppanel.CPMSimSetupPanel;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import aim4.sim.setup.cpm.CPMMultiWidthSimSetup;
import aim4.sim.setup.cpm.CPMSingleWidthSimSetup;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.*;
import java.util.List;

/**
 * Display a summary of the configuration chosen for this simulation.
 */
public class SimConfigSummary extends JPanel implements CPMStatScreenComponent {

    BasicCPMSimSetup setup;
    boolean isSingleWidthSetup;
    boolean isMultiWidthSetup;

    // Labels
    JLabel laneWidthLabel;
    JLabel parkingLaneSetsLabel;
    JLabel numberOfParkingLanesLabel;
    JLabel lengthOfParkingLabel;
    JLabel useCvsLabel;
    JLabel trafficLevelLabel;

    public SimConfigSummary(CPMSimSetupPanel setupPanel) {
        isSingleWidthSetup = setupPanel.getSimSetup() instanceof CPMSingleWidthSimSetup;
        isMultiWidthSetup = setupPanel.getSimSetup() instanceof CPMMultiWidthSimSetup;

        JLabel title = new JLabel("Config Summary");
        Font font = title.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        title.setFont(font.deriveFont(attributes));

        setup = (BasicCPMSimSetup)setupPanel.getSimSetup();

        if (isSingleWidthSetup) {
            laneWidthLabel = new JLabel("Lane width: "
                    + ((CPMSingleWidthSimSetup) setup).getLaneWidth());
            laneWidthLabel.setOpaque(true);

            numberOfParkingLanesLabel = new JLabel("Number of parking lanes: "
                    + ((CPMSingleWidthSimSetup) setup).getNumberOfParkingLanes());
            numberOfParkingLanesLabel.setOpaque(true);
        } else if (isMultiWidthSetup) {
            parkingLaneSetsLabel = new JLabel("Parking lanes and widths: \n"
                    + getParkingLanesSetsText(((CPMMultiWidthSimSetup) setup).getParkingLaneSets()));
            parkingLaneSetsLabel.setOpaque(true);

            numberOfParkingLanesLabel = new JLabel("Number of parking lanes: "
                    + ((CPMMultiWidthSimSetup) setup).getNumberOfParkingLanes());
            numberOfParkingLanesLabel.setOpaque(true);
        }

        lengthOfParkingLabel = new JLabel("Parking length: " + setup.getParkingLength());
        lengthOfParkingLabel.setOpaque(true);

        useCvsLabel = new JLabel("Using CSV file: " + setup.getUseCSVFile().getKey());
        useCvsLabel.setOpaque(true);

        trafficLevelLabel = new JLabel("Traffic level: " + (setup.getTrafficLevel()*3600));
        useCvsLabel.setOpaque(true);

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(title);
        if (isSingleWidthSetup) {
            this.add(laneWidthLabel);
        } else if (isMultiWidthSetup) {
            this.add(parkingLaneSetsLabel);
        }
        this.add(numberOfParkingLanesLabel);
        this.add(lengthOfParkingLabel);
        this.add(useCvsLabel);
        if (!setup.getUseCSVFile().getKey()) {
            this.add(trafficLevelLabel);
        }

    }

    private String getParkingLanesSetsText(List<Pair<Integer, Double>> parkingLaneSets) {
        String setsString = "";
        for (int i = 0 ; i < parkingLaneSets.size() ; i++) {
            Pair<Integer,Double> pair = parkingLaneSets.get(0);
            String newSet;
            if (i == 0) {
                newSet = String.format("(%dx%.1f)", pair.getKey(), pair.getValue());
            } else {
                newSet = String.format(",(%dx%.1f)", pair.getKey(), pair.getValue());
            }
            setsString += newSet;
        }
        return setsString;
    }

    @Override
    public void update(CPMAutoDriverSimulator sim, java.util.List<CPMAutoDriverSimulator.CPMAutoDriverSimStepResult> resultToProcess) {

    }

    @Override
    public List<String> getAllLabelsText(){
        List<String> labelsText = new ArrayList<String>();
        if (isSingleWidthSetup) {
            labelsText.add(laneWidthLabel.getText());
        } else if (isMultiWidthSetup) {
            labelsText.add(parkingLaneSetsLabel.getText());
        }
        labelsText.add(numberOfParkingLanesLabel.getText());
        labelsText.add(lengthOfParkingLabel.getText());
        labelsText.add(useCvsLabel.getText());
        if (!setup.getUseCSVFile().getKey()) {
            labelsText.add(trafficLevelLabel.getText());
        }
        return labelsText;
    }
}
