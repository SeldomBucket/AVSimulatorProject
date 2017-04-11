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

public class CPMUseCSVFileRadioButtons extends JPanel implements ChangeListener {

    /** The group for the radio buttons. */
    ButtonGroup group;
    /** The label of the group. */
    JLabel label;
    /** A text field for the user to input the location of the csv file.*/
    JTextField fileLocationField;

    public CPMUseCSVFileRadioButtons(){

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        label = new JLabel();
        label.setText("Use a CSV file to specify spawn times and parking times:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);

        JRadioButton randomSpecButton = new JRadioButton("Do not use CSV file", false);
        randomSpecButton.setActionCommand("FALSE");
        randomSpecButton.setSelected(true);
        this.add(randomSpecButton);

        JRadioButton singleSpecButton = new JRadioButton("Use CSV file ", false);
        singleSpecButton.setActionCommand("TRUE");
        this.add(singleSpecButton);
        fileLocationField = new JTextField(10);
        this.add(fileLocationField);

        group = new ButtonGroup();
        group.add(singleSpecButton);
        group.add(randomSpecButton);

        this.setVisible(true);

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
