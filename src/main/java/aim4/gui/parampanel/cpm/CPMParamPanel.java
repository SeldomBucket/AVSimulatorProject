package aim4.gui.parampanel.cpm;

import aim4.sim.setup.cpm.BasicCPMSimSetup;
import aim4.sim.setup.cpm.CPMAutoDriverSimSetup;

import javax.swing.*;

/**
 * Created by Becci on 22-Apr-17.
 */
public interface CPMParamPanel {
    void createAdditionalComponents(BasicCPMSimSetup simSetup);
    void addComponentsToPanel();
    double calculateCarParkArea();
}
