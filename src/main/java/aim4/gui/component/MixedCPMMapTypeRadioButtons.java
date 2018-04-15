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
        label.setText("Choose Map Type:");
        label.setForeground(Color.RED);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);


        Box radioButtonBox = Box.createVerticalBox();
        radioButtonBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(radioButtonBox);

        JRadioButton adjustableMixedButton = new JRadioButton("Adjustable Car Park", true);
        adjustableMixedButton.setActionCommand("ADJUSTABLE_MIXED");
        adjustableMixedButton.setSelected(true);
        adjustableMixedButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        radioButtonBox.add(adjustableMixedButton);

        JRadioButton adjustableManualButton = new JRadioButton("Adjustable Car Park for Manual Cars Only", false);
        adjustableManualButton.setActionCommand("ADJUSTABLE_MANUAL");
        //radioButtonBox.add(adjustableManualButton);

        JRadioButton staticButton = new JRadioButton("Static Car Park", false);
        staticButton.setActionCommand("STATIC");
        staticButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        radioButtonBox.add(staticButton);

        group = new ButtonGroup();
        group.add(adjustableMixedButton);
        //group.add(adjustableManualButton);
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
