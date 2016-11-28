package aim4.gui.setuppanel;

import aim4.sim.setup.SimSetup;
import aim4.sim.setup.aim.AIMSimSetup;

import javax.swing.*;

/**
 * Created by Callum on 15/11/2016.
 */
public abstract class SimSetupPanel extends JPanel {
    public abstract SimSetup getSimSetup();
}
