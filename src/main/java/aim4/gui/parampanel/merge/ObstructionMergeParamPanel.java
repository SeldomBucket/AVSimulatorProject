package aim4.gui.parampanel.merge;

import aim4.gui.component.LabeledSlider;
import aim4.sim.setup.merge.MergeSimSetup;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Callum on 02/03/2017.
 */
public class ObstructionMergeParamPanel extends MergeParamPanel {
    //COMBOBOX OPTIONS//
    final static String CENTRALISED_TITLE = "Centralised";

    //GUI ELEMENTS
    private JComboBox protocolComboBox;

    private JPanel optionPane;
    private BoxLayout optionsLayout;
    private LabeledSlider trafficRateSlider;

    public ObstructionMergeParamPanel() {
        //Setup the protocolComboBox;
        JPanel comboBoxPane = new JPanel();
        comboBoxPane.setBackground(Color.WHITE);

        String comboBoxItems[] =
                {
                        CENTRALISED_TITLE
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
        return null;
    }
}
