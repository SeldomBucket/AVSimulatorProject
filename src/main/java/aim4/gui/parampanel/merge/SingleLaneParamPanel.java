package aim4.gui.parampanel.merge;

import aim4.gui.component.LabeledSlider;
import aim4.sim.setup.merge.MergeSimSetup;
import aim4.sim.setup.merge.S2SSimSetup;
import aim4.sim.setup.merge.SingleLaneSimSetup;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Callum on 26/03/2017.
 */
public class SingleLaneParamPanel extends MergeParamPanel {
    //SIM SETUP//
    private SingleLaneSimSetup simSetup;

    //GUI ELEMENTS//
    /**The JPanel containing all of the option sliders**/
    private JPanel optionPane;
    /**Dictates the rate of traffic flow**/
    private LabeledSlider trafficRateSlider;
    /**Dictates the speed limit for the target lane**/
    private LabeledSlider speedLimitSlider;
    /**Dictates the length of the lane**/
    private LabeledSlider laneLengthSlider;

    public SingleLaneParamPanel() {
        //Create sliders
        trafficRateSlider =
                new LabeledSlider(0.0, 2500.0,
                        S2SSimSetup.DEFAULT_TRAFFIC_LEVEL * 3600.0,
                        500.0, 100.0,
                        "Traffic Level: %.0f vehicles/hour/lane",
                        "%.0f");
        speedLimitSlider =
                new LabeledSlider(0.0, 80.0,
                        S2SSimSetup.DEFAULT_TARGET_LANE_SPEED_LIMIT,
                        10.0, 5.0,
                        "Lane Speed Limit: %.0f metres/second",
                        "%.0f");
        laneLengthSlider =
                new LabeledSlider(50.0, 300.0,
                        S2SSimSetup.DEFAULT_TARGET_LEAD_IN_DISTANCE,
                        50.0, 10.0,
                        "Lane has a length of: %.0f metres",
                        "%.0f");

        //Create option pane
        optionPane = new JPanel();
        optionPane.setLayout(new BoxLayout(optionPane, BoxLayout.PAGE_AXIS));
        optionPane.add(trafficRateSlider);
        optionPane.add(speedLimitSlider);
        optionPane.add(laneLengthSlider);

        //Put them all together
        setLayout(new BorderLayout());
        add(optionPane, BorderLayout.CENTER);
    }

    @Override
    public MergeSimSetup getSimSetup() {
        return new SingleLaneSimSetup(
                trafficRateSlider.getValue(),
                speedLimitSlider.getValue(),
                laneLengthSlider.getValue()
        );
    }
}
