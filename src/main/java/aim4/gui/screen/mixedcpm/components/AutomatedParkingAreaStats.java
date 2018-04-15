package aim4.gui.screen.mixedcpm.components;

import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator.*;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;
import aim4.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.List;
import java.util.Map;

/**
 * A section displaying stats about the current state of the car park.
 */
public class AutomatedParkingAreaStats extends JPanel implements MixedCPMStatScreenComponent{

    private JLabel numberOfCarsDeniedEntryLabel;
    private JLabel numberOfCarsAllowedEntryLabel;
    private JLabel numberOfCompletedVehiclesLabel;
    private JLabel maxVehiclesInCarParkLabel;
    private JLabel numOfVehiclesInCarParkLabel;
    private JLabel maxVehiclesParkedInCarParkLabel;
    private JLabel numOfVehiclesParkedInCarParkLabel;

    public AutomatedParkingAreaStats() {
        JLabel title = new JLabel("Automated Parking Area Stats");
        Font font = title.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        title.setFont(font.deriveFont(attributes));

        numberOfCarsDeniedEntryLabel = new JLabel("No of automated vehicles denied entry: ");
        numberOfCarsDeniedEntryLabel.setOpaque(true);

        numberOfCarsAllowedEntryLabel = new JLabel("No of automated vehicles allowed entry: ");
        numberOfCarsAllowedEntryLabel.setOpaque(true);

        numberOfCompletedVehiclesLabel = new JLabel("No of automated Auto vehicles: ");
        numberOfCompletedVehiclesLabel.setOpaque(true);

        numOfVehiclesInCarParkLabel = new JLabel("No of automated vehicles in car park: ");
        numOfVehiclesInCarParkLabel.setOpaque(true);

        maxVehiclesInCarParkLabel = new JLabel("Max no of automated vehicles in car park: ");
        maxVehiclesInCarParkLabel.setOpaque(true);

        numOfVehiclesParkedInCarParkLabel = new JLabel("No of automated vehicles parked: ");
        numOfVehiclesParkedInCarParkLabel.setOpaque(true);

        maxVehiclesParkedInCarParkLabel = new JLabel("Max no of automated vehicles parked: ");
        maxVehiclesParkedInCarParkLabel.setOpaque(true);


        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(title);

        this.add(numberOfCarsAllowedEntryLabel);
        this.add(numberOfCarsDeniedEntryLabel);
        this.add(numberOfCompletedVehiclesLabel);
        this.add(numOfVehiclesInCarParkLabel);
        this.add(maxVehiclesInCarParkLabel);
        this.add(numOfVehiclesParkedInCarParkLabel);
        this.add(maxVehiclesParkedInCarParkLabel);
    }

    @Override
    public void update(MixedCPMAutoDriverSimulator sim, List<MixedCPMAutoDriverSimStepResult> resultToProcess) {
        int numberOfDeniedEntries = sim.getMap().getStatusMonitor().getNumberOfDeniedAutoEntries();
        int numberOfAllowedEntries = sim.getMap().getStatusMonitor().getNumberOfAllowedAutoEntries();
        int numberOfCompletedVehicles = sim.getMap().getStatusMonitor().getNumberOfCompletedAutoVehicles();
        int maxVehicles = sim.getMap().getStatusMonitor().getMostNumberOfAutoVehicles();
        int numOfVehicles = sim.getMap().getStatusMonitor().getAutoVehicles().size();
        int numOfParkedVehicles = sim.getMap().getStatusMonitor().getNoOfParkedAutoVehicles();
        int maxParkedVehicles = sim.getMap().getStatusMonitor().getMostNumberOfParkedAutoVehicles();

        updateLabel(numberOfAllowedEntries, numberOfCarsAllowedEntryLabel);
        updateLabel(numberOfDeniedEntries, numberOfCarsDeniedEntryLabel);
        updateLabel(numberOfCompletedVehicles, numberOfCompletedVehiclesLabel);
        updateLabel(maxVehicles, maxVehiclesInCarParkLabel);
        updateLabel(numOfVehicles, numOfVehiclesInCarParkLabel);
        updateLabel(maxParkedVehicles, maxVehiclesParkedInCarParkLabel);
        updateLabel(numOfParkedVehicles, numOfVehiclesParkedInCarParkLabel);
    }

    private void updateLabel(int newValue, JLabel label){
        String labelText = label.getText();
        // Split the text so we can remove the old value and reuse the label
        String[] labelSplit = labelText.split(":");
        String labelOnly = labelSplit [0];
        label.setText(labelOnly + ": " + newValue);
    }
}
