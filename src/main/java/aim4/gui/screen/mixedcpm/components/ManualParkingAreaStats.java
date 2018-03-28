package aim4.gui.screen.mixedcpm.components;

import aim4.gui.screen.merge.components.MapKeyTableModel;
import aim4.map.cpm.CPMCarParkWithStatus;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator.*;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.List;
import java.util.Map;

/**
 * A section displaying stats about the current state of the car park.
 */
public class ManualParkingAreaStats extends JPanel implements MixedCPMStatScreenComponent{

    private JLabel numberOfCarsDeniedEntryLabel;
    private JLabel maxVehiclesInCarParkLabel;
    private JLabel numOfVehiclesInCarParkLabel;
    private JLabel maxVehiclesParkedInCarParkLabel;
    private JLabel numOfVehiclesParkedInCarParkLabel;
    private JLabel carParkAreaLabel;

    public ManualParkingAreaStats() {
        JLabel title = new JLabel("Manual Parking Area Stats");
        Font font = title.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        title.setFont(font.deriveFont(attributes));

        numberOfCarsDeniedEntryLabel = new JLabel("Vehicles denied entry: ");
        numberOfCarsDeniedEntryLabel.setOpaque(true);

        maxVehiclesInCarParkLabel = new JLabel("Max vehicles been in manual parking area: ");
        maxVehiclesInCarParkLabel.setOpaque(true);

        numOfVehiclesInCarParkLabel = new JLabel("Vehicles currently in the manual parking area: ");
        numOfVehiclesInCarParkLabel.setOpaque(true);

        maxVehiclesParkedInCarParkLabel = new JLabel("Max vehicles parked in manual parking area: ");
        maxVehiclesParkedInCarParkLabel.setOpaque(true);

        numOfVehiclesParkedInCarParkLabel = new JLabel("Vehicles currently parked in the manual parking area: ");
        numOfVehiclesParkedInCarParkLabel.setOpaque(true);

        carParkAreaLabel = new JLabel("Manual parking area (square metres): ");
        carParkAreaLabel.setOpaque(true);



        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(title);
        this.add(numberOfCarsDeniedEntryLabel);
        this.add(maxVehiclesInCarParkLabel);
        this.add(numOfVehiclesInCarParkLabel);
        this.add(maxVehiclesParkedInCarParkLabel);
        this.add(numOfVehiclesParkedInCarParkLabel);
        this.add(carParkAreaLabel);
    }

    @Override
    public void update(MixedCPMAutoDriverSimulator sim, List<MixedCPMAutoDriverSimStepResult> resultToProcess) {
        int numberOfDeniedEntries = sim.getMap().getStatusMonitor().getNumberOfDeniedEntries();
        int maxVehicles = sim.getMap().getStatusMonitor().getMostNumberOfVehicles();
        int numOfVehicles = sim.getMap().getStatusMonitor().getVehicles().size();
        int numOfParkedVehicles = sim.getMap().getStatusMonitor().getNoOfParkedVehicles();
        int maxParkedVehicles = sim.getMap().getStatusMonitor().getMostNumberOfParkedVehicles();
        int carParkArea = (int)Math.ceil(((MixedCPMBasicMap)sim.getMap()).getTotalCarParkArea());
        updateLabel(numberOfDeniedEntries, numberOfCarsDeniedEntryLabel);
        updateLabel(maxVehicles, maxVehiclesInCarParkLabel);
        updateLabel(numOfVehicles, numOfVehiclesInCarParkLabel);
        updateLabel(maxParkedVehicles, maxVehiclesParkedInCarParkLabel);
        updateLabel(numOfParkedVehicles, numOfVehiclesParkedInCarParkLabel);
        updateLabel(carParkArea, carParkAreaLabel);
    }

    private void updateLabel(int newValue, JLabel label){
        String labelText = label.getText();
        // Split the text so we can remove the old value and reuse the label
        String[] labelSplit = labelText.split(":");
        String labelOnly = labelSplit [0];
        label.setText(labelOnly + ": " + newValue);
    }
}
