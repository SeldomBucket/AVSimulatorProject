package aim4.gui.screen.cpm.components;

import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator.*;

import java.util.List;

/**
 * To use this interface you MUST also extend JComponent or one of JComponent's subclasses
 */
public interface CPMStatScreenComponent {
    public void update(CPMAutoDriverSimulator sim, List<CPMAutoDriverSimStepResult> resultToProcess);
}
