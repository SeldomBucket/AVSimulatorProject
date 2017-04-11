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
    /** The label of the group. */
    JLabel label;

    public CPMSpawnSpecRadioButtons(){

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

    }

    public ButtonModel getSelected(){
        return group.getSelection();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }
}
