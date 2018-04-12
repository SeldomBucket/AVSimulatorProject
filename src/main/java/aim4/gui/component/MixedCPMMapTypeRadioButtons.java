package aim4.gui.component;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class MixedCPMMapTypeRadioButtons extends JPanel implements ChangeListener {


    /** The group for the radio buttons. */
    ButtonGroup group;
    /** The label of the group. */
    JLabel label;

    public MixedCPMMapTypeRadioButtons(){

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        label = new JLabel();
        label.setText("Choose map type:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);

        JRadioButton adjustableMixedButton = new JRadioButton("Adjustable Car Park for Mixed Automated and Manual Vehicles", false);
        adjustableMixedButton.setActionCommand("ADJUSTABLE_MIXED");
        adjustableMixedButton.setSelected(true);
        this.add(adjustableMixedButton);

        JRadioButton adjustableManualButton = new JRadioButton("Adjustable Car Park for Manual Cars Only", false);
        adjustableManualButton.setActionCommand("ADJUSTABLE_MANUAL");
        adjustableManualButton.setSelected(true);
        this.add(adjustableManualButton);

        JRadioButton staticButton = new JRadioButton("Static Car Park", false);
        staticButton.setActionCommand("STATIC");
        this.add(staticButton);

        group = new ButtonGroup();
        group.add(adjustableMixedButton);
        group.add(adjustableManualButton);
        group.add(staticButton);

        this.setVisible(true);

    }

    public ButtonModel getSelected(){
        return group.getSelection();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }
}
