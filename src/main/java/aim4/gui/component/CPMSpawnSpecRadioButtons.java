package aim4.gui.component;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * A group of radio buttons with a label.
 */
public class CPMSpawnSpecRadioButtons extends JPanel implements ChangeListener {

    /** The group for the radio buttons. */
    ButtonGroup group;
    /** The label of the slider. */
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

        class VoteActionListener implements ActionListener {
            public void actionPerformed(ActionEvent ex) {
                String choice = group.getSelection().getActionCommand();
                System.out.println("ACTION Candidate Selected: " + choice);
            }
        }

        class VoteItemListener implements ItemListener {
            public void itemStateChanged(ItemEvent ex) {
                String item = ((AbstractButton) ex.getItemSelectable()).getActionCommand();
                boolean selected = (ex.getStateChange() == ItemEvent.SELECTED);
                System.out.println("ITEM Candidate Selected: " + selected + " Selection: " + item);
            }
        }

        ActionListener al = new VoteActionListener();
        singleSpecButton.addActionListener(al);
        randomSpecButton.addActionListener(al);

        ItemListener il = new VoteItemListener();
        singleSpecButton.addItemListener(il);
        randomSpecButton.addItemListener(il);

        this.setVisible(true);

    }

    public ButtonModel getSelected(){
        return group.getSelection();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }
}
