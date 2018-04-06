package aim4.gui.component;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Create radio buttons which indicate whether a csv file should be used or not.
 * If so, then vehicles will be spawned only on the entry times given, and will
 * spawn with the specified parking times. If not, then vehicles will be
 * spawned according to the traffic level set, and the parking time will be
 * randomly generated.
 */
// TODO CPM would be good to separate into 2 groups, one for entry times and one for parking times,
// so could do one or the other rather than both.

public class MixedCPMUseCSVFileRadioButtons extends JPanel implements ChangeListener {

    /** The group for the radio buttons. */
    ButtonGroup group;
    /** The label of the group. */
    JLabel label;
    /** A text field for the user to input the location of the csv file.*/
    JTextField fileLocationField;

    public MixedCPMUseCSVFileRadioButtons(){

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        label = new JLabel();
        label.setText("Use a CSV file to specify spawn times and parking times:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);

        JRadioButton doNotUseCsvFileButton = new JRadioButton("Do not use CSV file ", false);
        doNotUseCsvFileButton.setActionCommand("FALSE");
        doNotUseCsvFileButton.setSelected(true);
        this.add(doNotUseCsvFileButton);

        JRadioButton useCsvFileButton = new JRadioButton("Use CSV file ", false);
        useCsvFileButton.setActionCommand("TRUE");
        this.add(useCsvFileButton);
        fileLocationField = new JTextField(10);
        this.add(fileLocationField);

        group = new ButtonGroup();
        group.add(useCsvFileButton);
        group.add(doNotUseCsvFileButton);

        this.setVisible(true);

        /*

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        label = new JLabel();
        label.setText("Choose vehicle specification spawn type:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);

        JRadioButton singleSpecButton = new JRadioButton("Single", false);
        singleSpecButton.setActionCommand("SINGLE");
        singleSpecButton.setSelected(true);
        this.add(singleSpecButton);
        JRadioButton randomSpecButton = new JRadioButton("Random", false);
        randomSpecButton.setActionCommand("RANDOM");
        this.add(randomSpecButton);

        group = new ButtonGroup();
        group.add(singleSpecButton);
        group.add(randomSpecButton);

        this.setVisible(true);

        */

    }

    public ButtonModel getSelected(){
        return group.getSelection();
    }

    public String getFileLocation() {
        return fileLocationField.getText();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }
}
