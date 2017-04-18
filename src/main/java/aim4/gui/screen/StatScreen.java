package aim4.gui.screen;

import javax.swing.*;
import java.awt.*;

/**
 * Screen used to display statistics while the simulation is running.
 */
public abstract class StatScreen extends JPanel implements SimScreen {
    public abstract void start();

    /**
     * Used to print the StatScreen data to a file.
     * Any extending class should implement this.
     */
    public abstract void printData();
}
