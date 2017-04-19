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

    public CPMSpawnSpecRadioButtons(){

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

    }

    public ButtonModel getSelected(){
        return group.getSelection();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }
}
