package aim4.gui.component;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * A group of radio buttons with a label.
 */
public class MixedCPMSpawnSpecRadioButtons extends JPanel implements ChangeListener {

    /** The group for the radio buttons. */
    ButtonGroup group;
    /** The label of the group. */
    JLabel label;
    /** A text field for the user to input the location of the csv file.*/
    JTextField fileLocationField;

    public MixedCPMSpawnSpecRadioButtons(){

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

        JRadioButton useCsvFileButton = new JRadioButton("Use CSV file ", false);
        useCsvFileButton.setActionCommand("CSV");
        this.add(useCsvFileButton);
        fileLocationField = new JTextField(10);
        this.add(fileLocationField);

        group = new ButtonGroup();

        group.add(singleSpecButton);
        group.add(randomSpecButton);
        group.add(useCsvFileButton);

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
