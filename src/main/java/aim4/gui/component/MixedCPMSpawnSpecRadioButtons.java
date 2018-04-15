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
    JSpinner spawnNumberSpinner;

    public MixedCPMSpawnSpecRadioButtons(){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        label = new JLabel();
        label.setText("Choose vehicle specification spawn type:");
        label.setForeground(Color.RED);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);

        JLabel spinnerLabel = new JLabel();
        spinnerLabel.setText("The spinner specifies the number of vehicles to spawn when a Finite spawntype is selected:");
        spinnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(spinnerLabel);

        spawnNumberSpinner = new JSpinner(new SpinnerNumberModel());
        spawnNumberSpinner.setMaximumSize(new Dimension(50, 100));
        spawnNumberSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        spawnNumberSpinner.setValue(100);
        this.add(spawnNumberSpinner);

        Box radioButtonBox = Box.createVerticalBox();
        radioButtonBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(radioButtonBox);

        JRadioButton singleSpecButton = new JRadioButton("Finite Single (Spawns COUPE)", false);
        singleSpecButton.setActionCommand("FINITE_SINGLE");
        singleSpecButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        singleSpecButton.setSelected(true);
        radioButtonBox.add(singleSpecButton);

        JRadioButton randomSpecButton = new JRadioButton("Finite Random", false);
        randomSpecButton.setActionCommand("FINITE_RANDOM");
        randomSpecButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        radioButtonBox.add(randomSpecButton);

        JRadioButton singleInfiniteSpecButton = new JRadioButton("Infinite Single (Spawns COUPE)", false);
        singleInfiniteSpecButton.setActionCommand("SINGLE");
        singleInfiniteSpecButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        radioButtonBox.add(singleInfiniteSpecButton);

        JRadioButton randomInfiniteSpecButton = new JRadioButton("Infinite Random", false);
        randomInfiniteSpecButton.setActionCommand("RANDOM");
        randomInfiniteSpecButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        radioButtonBox.add(randomInfiniteSpecButton);

        JRadioButton useCsvFileButton = new JRadioButton("From CSV file (type filename in box below)", false);
        useCsvFileButton.setActionCommand("CSV");
        useCsvFileButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        radioButtonBox.add(useCsvFileButton);

        fileLocationField = new JTextField(10);
        fileLocationField.setMaximumSize(new Dimension(500, 100));
        fileLocationField.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(fileLocationField);

        group = new ButtonGroup();

        group.add(singleSpecButton);
        group.add(randomSpecButton);
        group.add(singleInfiniteSpecButton);
        group.add(randomInfiniteSpecButton);
        group.add(useCsvFileButton);

        this.setVisible(true);

    }

    public ButtonModel getSelected(){
        return group.getSelection();
    }

    public String getFileLocation() {
        return fileLocationField.getText();
    }

    public int getNumberOfVehiclesToSpawn(){
        int returnInt = (int)spawnNumberSpinner.getValue();
        if (returnInt<0){
            return 0;
        }
        return returnInt;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }
}
