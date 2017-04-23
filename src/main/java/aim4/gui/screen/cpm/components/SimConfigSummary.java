package aim4.gui.screen.cpm.components;

import aim4.gui.setuppanel.CPMSimSetupPanel;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;

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

    // Labels
    JLabel laneWidthLabel;
    JLabel numberOfParkingLanesLabel;
    JLabel lengthOfParkingLabel;
    JLabel useCvsLabel;
    JLabel trafficLevelLabel;

    public SimConfigSummary(CPMSimSetupPanel setupPanel) {
        JLabel title = new JLabel("Config Summary");
        Font font = title.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        title.setFont(font.deriveFont(attributes));

        setup = (BasicCPMSimSetup)setupPanel.getSimSetup();

        laneWidthLabel = new JLabel("Lane width: " + setup.getLaneWidth());
        laneWidthLabel.setOpaque(true);

        numberOfParkingLanesLabel = new JLabel("Number of parking lanes: " + setup.getNumberOfParkingLanes());
        numberOfParkingLanesLabel.setOpaque(true);

        lengthOfParkingLabel = new JLabel("Parking length: " + setup.getParkingLength());
        lengthOfParkingLabel.setOpaque(true);

        useCvsLabel = new JLabel("Using CSV file: " + setup.getUseCSVFile().getKey());
        useCvsLabel.setOpaque(true);

        trafficLevelLabel = new JLabel("Traffic level: " + (setup.getTrafficLevel()*3600));
        useCvsLabel.setOpaque(true);

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(title);
        this.add(laneWidthLabel);
        this.add(numberOfParkingLanesLabel);
        this.add(lengthOfParkingLabel);
        this.add(useCvsLabel);
        if (!setup.getUseCSVFile().getKey()) {
            this.add(trafficLevelLabel);
        }

    }

    @Override
    public void update(CPMAutoDriverSimulator sim, java.util.List<CPMAutoDriverSimulator.CPMAutoDriverSimStepResult> resultToProcess) {

    }

    @Override
    public List<String> getAllLabelsText(){
        List<String> labelsText = new ArrayList<String>();
        labelsText.add(laneWidthLabel.getText());
        labelsText.add(numberOfParkingLanesLabel.getText());
        labelsText.add(lengthOfParkingLabel.getText());
        labelsText.add(useCvsLabel.getText());
        if (!setup.getUseCSVFile().getKey()) {
            labelsText.add(trafficLevelLabel.getText());
        }
        return labelsText;
    }
}
