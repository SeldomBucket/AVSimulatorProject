package aim4.gui.screen;

import javax.swing.*;
import java.awt.*;

/**
 * Screen used to display statistics while the simulation is running.
 */
public abstract class StatScreen extends JPanel implements SimScreen {
    public abstract void start();
}
