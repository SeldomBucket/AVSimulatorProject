package aim4.gui.parampanel.merge;

import aim4.gui.component.LabeledSlider;
import aim4.sim.setup.merge.MergeSimSetup;
import aim4.sim.setup.merge.S2SSimSetup;
import aim4.sim.simulator.merge.MergingProtocol;
import javafx.scene.control.Labeled;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Callum on 02/03/2017.
 */
public class S2SMergeParamPanel extends MergeParamPanel {
    //COMBOBOX OPTIONS//
    final static String AIM_PROTOCOL_TITLE = "AIM Protocol";

    //GUI ELEMENTS
    /**Combo box indicating the protocol to use**/
    private JComboBox protocolComboBox;
    /**The JPanel containing all of the option sliders**/
    private JPanel optionPane;
    /**Dictates the rate of traffic flow**/
    private LabeledSlider trafficRateSlider;
    /**Dictates the speed limit for the target lane**/
    private LabeledSlider targetLaneSpeedSlider;
    /**Dictates the speed limit for the merge lane**/
    private LabeledSlider mergeLaneSpeedSlider;
    /**Dictates the distance between the target lane start and the merge point**/
    private LabeledSlider targetLeadInDistanceSlider;
    /**Dictates the distance between the target lane end and the merge point**/
    private LabeledSlider targetLeadOutDistanceSlider;
    /**Dictates the length of the merging road as it leads into the merge point**/
    private LabeledSlider mergeLeadInDistanceSlider;
    /**Dictates the angle between the target lane and the merging lane**/
    private LabeledSlider mergingAngleSlider;

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
        targetLaneSpeedSlider =
                new LabeledSlider(0.0, 80.0,
                        40.0,
                        10.0, 5.0,
                        "Target Lane Speed Limit: %.0f metres/second",
                        "%.0f");
        mergeLaneSpeedSlider =
                new LabeledSlider(0.0, 80.0,
                        40.0,
                        10.0, 5.0,
                        "Merge Lane Speed Limit: %.0f metres/second",
                        "%.0f");
        targetLeadInDistanceSlider =
                new LabeledSlider(50.0, 300.0,
                        150.0,
                        50.0, 10.0,
                        "Target Lane lead in distance: %.0f metres",
                        "%.0f");
        targetLeadOutDistanceSlider =
                new LabeledSlider(50.0, 300.0,
                        150.0,
                        50.0, 10.0,
                        "Target Lane lead out distance: %.0f metres",
                        "%.0f");
        mergeLeadInDistanceSlider =
                new LabeledSlider(50.0, 300.0,
                        150.0,
                        50.0, 10.0,
                        "Merging Lane distance: %.0f metres",
                        "%.0f");
        mergingAngleSlider =
                new LabeledSlider(0.0, 90.0,
                        45.0,
                        10.0, 1.0,
                        "Merging Angle: %.0f degrees",
                        "%.0f");

        //Create option pane
        optionPane = new JPanel();
        optionPane.setLayout(new BoxLayout(optionPane, BoxLayout.PAGE_AXIS));
        optionPane.add(trafficRateSlider);
        optionPane.add(targetLaneSpeedSlider);
        optionPane.add(mergeLaneSpeedSlider);
        optionPane.add(targetLeadInDistanceSlider);
        optionPane.add(targetLeadOutDistanceSlider);
        optionPane.add(mergeLeadInDistanceSlider);
        optionPane.add(mergingAngleSlider);

        //Put them together
        setLayout(new BorderLayout());
        add(comboBoxPane, BorderLayout.PAGE_START);
        add(optionPane, BorderLayout.CENTER);
    }

    @Override
    public MergeSimSetup getSimSetup() {


        return new S2SSimSetup(
                trafficRateSlider.getValue(), getSelectedProtocol(),
                targetLaneSpeedSlider.getValue(),       mergeLaneSpeedSlider.getValue(),
                targetLeadInDistanceSlider.getValue(),  targetLeadOutDistanceSlider.getValue(),
                mergeLeadInDistanceSlider.getValue(),   mergingAngleSlider.getValue()
        );
    }

    private MergingProtocol getSelectedProtocol(){
        switch (protocolComboBox.getSelectedIndex()) {
            case 0: return MergingProtocol.AIM;
            default: throw new RuntimeException("Protocol Combo Box went out of range");
        }
    }
}
