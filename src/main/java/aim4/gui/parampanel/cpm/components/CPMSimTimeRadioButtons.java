package aim4.gui.parampanel.cpm.components;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Create radio buttons where the user can either run the simulation infinitely,
 * or for a specified length of time.
 */
public class CPMSimTimeRadioButtons extends JPanel implements ChangeListener {

    /** The group for the radio buttons. */
    ButtonGroup group;
    /** The label of the group. */
    JLabel label;
    /** A text field for the user to input number of hours.*/
    JTextField hoursField;
    /** A text field for the user to input number of minutes.*/
    JTextField minutesField;
    /** A text field for the user to input number of seconds.*/
    JTextField secondsField;

    public CPMSimTimeRadioButtons(){

        JLabel simTimeLabel = new JLabel("Run the simulation for:");
        simTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(simTimeLabel);

        JRadioButton foreverButton = new JRadioButton("Run forever", false);
        foreverButton.setActionCommand("FALSE");
        foreverButton.setSelected(true);
        this.add(foreverButton);

        JRadioButton specificTimeButton = new JRadioButton("Run for (hh:mm:ss): ", false);
        specificTimeButton.setActionCommand("TRUE");
        this.add(specificTimeButton);

        hoursField = new JTextField(2);
        this.add(hoursField);
        JLabel hoursLabel = new JLabel("hours");
        this.add(hoursLabel);

        minutesField = new JTextField(2);
        this.add(minutesField);
        JLabel minutesLabel = new JLabel("minutes");
        this.add(minutesLabel);

        secondsField = new JTextField(2);
        this.add(secondsField);
        JLabel secondsLabel = new JLabel("seconds");
        this.add(secondsLabel);

        group = new ButtonGroup();
        group.add(foreverButton);
        group.add(specificTimeButton);

        this.setVisible(true);

    }

    public ButtonModel getSelected(){
        return group.getSelection();
    }

    public String getHours() {
        return hoursField.getText();
    }

    public String getMinutes() {
        return minutesField.getText();
    }

    public String getSeconds() {
        return secondsField.getText();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }
}
