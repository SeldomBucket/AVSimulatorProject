package aim4.gui.parampanel.cpm.components;

import javax.swing.*;
import java.awt.*;

/**
 * A panel where the user can input how many simulations to run.
 */
public class CPMNumberOfSimulationsInput extends JPanel{


    protected JLabel numberOfSimulationsLabel;
    protected JSpinner numberOfSimulationsSpinner;
    protected JLabel fileLocationLabel;
    protected JTextField fileLocationField;

    public CPMNumberOfSimulationsInput() {
        numberOfSimulationsLabel = new JLabel("Number of simulations to run: ");
        this.add(numberOfSimulationsLabel);

        numberOfSimulationsSpinner = new JSpinner();
        numberOfSimulationsSpinner.setValue(new Integer(1));
        numberOfSimulationsSpinner.setPreferredSize(new Dimension(50, 30));
        this.add(numberOfSimulationsSpinner);

        fileLocationLabel = new JLabel("Save data in location: ");
        this.add(fileLocationLabel);

        fileLocationField = new JTextField(10);
        this.add(fileLocationField);
    }

    public Integer getNumberOfSimulations() {
        return (Integer)numberOfSimulationsSpinner.getValue();
    }

    public String getFileLocation() { return fileLocationField.getText(); }
}
