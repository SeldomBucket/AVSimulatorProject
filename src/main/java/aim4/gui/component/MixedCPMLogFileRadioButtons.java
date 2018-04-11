package aim4.gui.component;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class MixedCPMLogFileRadioButtons extends JPanel implements ChangeListener {


    /** The group for the radio buttons. */
    ButtonGroup group;
    /** The label of the group. */
    JLabel label;

    public MixedCPMLogFileRadioButtons(){

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        label = new JLabel();
        label.setText("Log vehicle spawns and statistics?:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);


        JRadioButton noLogButton = new JRadioButton("DON'T log to file", false);
        noLogButton.setActionCommand("NO_LOG");
        noLogButton.setSelected(true);
        this.add(noLogButton);

        JRadioButton logButton = new JRadioButton("Log to file", false);
        logButton.setActionCommand("LOG");
        logButton.setSelected(false);
        this.add(logButton);

        group = new ButtonGroup();
        group.add(logButton);
        group.add(noLogButton);

        this.setVisible(true);

    }

    public boolean getSelected(){
        return group.getSelection().getActionCommand().equals("LOG");
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }
}
