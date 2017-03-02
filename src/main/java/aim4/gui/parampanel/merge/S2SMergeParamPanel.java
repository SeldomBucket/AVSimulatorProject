package aim4.gui.parampanel.merge;

import aim4.gui.component.LabeledSlider;
import aim4.sim.setup.merge.MergeSimSetup;
import aim4.sim.setup.merge.S2SSimSetup;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Callum on 02/03/2017.
 */
public class S2SMergeParamPanel extends MergeParamPanel {
    //COMBOBOX OPTIONS//
    final static String AIM_PROTOCOL_TITLE = "AIM Protocol";

    //GUI ELEMENTS
    private JComboBox protocolComboBox;

    private JPanel optionPane;
    private LabeledSlider trafficRateSlider;

    public S2SMergeParamPanel() {
        //Setup the protocolComboBox;
        JPanel comboBoxPane = new JPanel();
        comboBoxPane.setBackground(Color.WHITE);

        String comboBoxItems[] =
                {
                        AIM_PROTOCOL_TITLE
                };
        protocolComboBox = new JComboBox(comboBoxItems);
        protocolComboBox.setEditable(false);
        comboBoxPane.add(protocolComboBox);

        //Create option components
        trafficRateSlider =
                new LabeledSlider(0.0, 2500.0,
                        800.0,
                        500.0, 100.0,
                        "Traffic Level: %.0f vehicles/hour/lane",
                        "%.0f");

        //Create option pane
        optionPane = new JPanel();
        optionPane.setLayout(new BoxLayout(optionPane, BoxLayout.PAGE_AXIS));
        optionPane.add(trafficRateSlider);

        //Put them together
        setLayout(new BorderLayout());
        add(comboBoxPane, BorderLayout.PAGE_START);
        add(optionPane, BorderLayout.CENTER);
    }

    @Override
    public MergeSimSetup getSimSetup() {
        return new S2SSimSetup();
    }
}
