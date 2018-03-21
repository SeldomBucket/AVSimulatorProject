package aim4.gui.screen.mixedcpm.components;

import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator.*;

import java.util.List;

/**
 * To use this interface you MUST also extend JComponent or one of JComponent's subclasses
 */
public interface MixedCPMStatScreenComponent {
    public void update(MixedCPMAutoDriverSimulator sim, List<MixedCPMAutoDriverSimStepResult> resultToProcess);
}
