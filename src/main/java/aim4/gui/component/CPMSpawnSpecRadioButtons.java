package aim4.gui.component;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * A group of radio buttons with a label.
 */
public class CPMSpawnSpecRadioButtons extends JPanel implements ChangeListener {

    /** The group for the radio buttons. */
    ButtonGroup group;
    /** The list of vehicle specs that can be spawned on single spawn spec */
    JComboBox specList;

    public CPMSpawnSpecRadioButtons(){

        JRadioButton singleSpecButton = new JRadioButton("Single", false);
        singleSpecButton.setActionCommand("SINGLE");
        singleSpecButton.setSelected(true);
        this.add(singleSpecButton);

        String[] specNames = {"COUPE", "SEDAN", "SUV", "VAN"};

        //Create the combo box, select item at index 4.
        //Indices start at 0, so 4 specifies the pig.
        specList = new JComboBox(specNames);
        specList.setSelectedIndex(0);
        // petList.addActionListener(this);
        this.add(specList);

        JRadioButton randomSpecButton = new JRadioButton("Random", false);
        randomSpecButton.setActionCommand("RANDOM");
        this.add(randomSpecButton);

        group = new ButtonGroup();
        group.add(singleSpecButton);
        group.add(randomSpecButton);

        this.setVisible(true);

    }

    public ButtonModel getSelected(){
        return group.getSelection();
    }

    public String getSelectedSingleSpec(){ return specList.getSelectedItem().toString(); }

    @Override
    public void stateChanged(ChangeEvent e) {
    }
}
