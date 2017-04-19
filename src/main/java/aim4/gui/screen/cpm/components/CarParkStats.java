package aim4.gui.screen.cpm.components;

import aim4.map.cpm.CPMCarParkWithStatus;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator.*;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A section displaying stats about the current state of the car park.
 */
public class CarParkStats extends JPanel implements CPMStatScreenComponent{

    private JLabel numberOfCarsDeniedEntryLabel;
    private JLabel numberOfCarsNotCateredForLabel;
    private JLabel maxVehiclesInCarParkLabel;
    private JLabel numOfVehiclesInCarParkLabel;
    private JLabel carParkAreaLabel;

    public CarParkStats() {
        JLabel title = new JLabel("Car Park Stats");
        Font font = title.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        title.setFont(font.deriveFont(attributes));

        numberOfCarsDeniedEntryLabel = new JLabel("Vehicles denied entry: ");
        numberOfCarsDeniedEntryLabel.setOpaque(true);

        numberOfCarsNotCateredForLabel = new JLabel("Vehicles not catered for: ");
        numberOfCarsNotCateredForLabel.setOpaque(true);

        maxVehiclesInCarParkLabel = new JLabel("Max vehicles been in car park: ");
        maxVehiclesInCarParkLabel.setOpaque(true);

        numOfVehiclesInCarParkLabel = new JLabel("Vehicles currently in the car park: ");
        numOfVehiclesInCarParkLabel.setOpaque(true);

        carParkAreaLabel = new JLabel("Car park area (square metres): ");
        carParkAreaLabel.setOpaque(true);



        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(title);
        this.add(numberOfCarsDeniedEntryLabel);
        this.add(numberOfCarsNotCateredForLabel);
        this.add(maxVehiclesInCarParkLabel);
        this.add(numOfVehiclesInCarParkLabel);
        this.add(carParkAreaLabel);
    }

    @Override
    public void update(CPMAutoDriverSimulator sim, List<CPMAutoDriverSimStepResult> resultToProcess) {
        int numberOfDeniedEntries = sim.getMap().getStatusMonitor().getNumberOfDeniedEntries();
        int numberOfCarsNotCateredFor = sim.getNumberOfVehiclesNotCateredFor();
        int maxVehiclesInCarPark = sim.getMap().getStatusMonitor().getMostNumberOfVehicles();
        int numOfVehiclesInCarPark = sim.getMap().getStatusMonitor().getVehicles().size();
        int carParkArea = (int)Math.ceil(((CPMCarParkWithStatus)sim.getMap()).getTotalCarParkArea());
        updateLabel(numberOfDeniedEntries, numberOfCarsDeniedEntryLabel);
        updateLabel(numberOfCarsNotCateredFor, numberOfCarsNotCateredForLabel);
        updateLabel(maxVehiclesInCarPark, maxVehiclesInCarParkLabel);
        updateLabel(numOfVehiclesInCarPark, numOfVehiclesInCarParkLabel);
        updateLabel(carParkArea, carParkAreaLabel);
    }

    private void updateLabel(int newValue, JLabel label){
        String labelText = label.getText();
        // Split the text so we can remove the old value and reuse the label
        String[] labelSplit = labelText.split(":");
        String labelOnly = labelSplit [0];
        label.setText(labelOnly + ": " + newValue);
    }

    @Override
    public List<String> getAllLabelsText(){
        List<String> labelsText = new ArrayList<String>();
        labelsText.add(numberOfCarsDeniedEntryLabel.getText());
        labelsText.add(numberOfCarsNotCateredForLabel.getText());
        labelsText.add(maxVehiclesInCarParkLabel.getText());
        labelsText.add(numOfVehiclesInCarParkLabel.getText());
        labelsText.add(carParkAreaLabel.getText());
        return labelsText;
    }
}
