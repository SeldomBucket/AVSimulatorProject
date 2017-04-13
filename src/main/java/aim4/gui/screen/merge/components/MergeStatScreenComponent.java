package aim4.gui.screen.merge.components;

import aim4.sim.simulator.merge.CoreMergeSimulator;
import aim4.sim.simulator.merge.MergeSimulator;

import javax.swing.*;
import java.util.List;

/**
 * Created by Callum on 26/03/2017.
 */
/*
To use this interface you MUST also extend JComponent or one of JComponent's subclasses
 */
public interface MergeStatScreenComponent {
    public void update(MergeSimulator sim, List<CoreMergeSimulator.CoreMergeSimStepResult> resultToProcess);
}
