package aim4.gui.parampanel.cpm.components;

import aim4.gui.parampanel.cpm.CPMAutoDriverParamPanel;
import aim4.sim.setup.cpm.BasicCPMSimSetup;

import javax.swing.*;
import java.awt.*;

/**
 * A label which shows an updated value for the area of the car park.
 */
public class CPMMapAreaLabel extends JLabel {

    /** The string to put before the value. */
    private final String prefix;
    private double value;


    public CPMMapAreaLabel(String prefix, BasicCPMSimSetup simSetup){
        super(prefix);
        this.prefix = prefix;
        this.value = calculateArea(simSetup);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        setText(prefix + String.format("%.2f", value));
    }

    private double calculateArea(BasicCPMSimSetup simSetup){
        double parkingAreaLength = (2*simSetup.getAccessLength()) + (2*simSetup.getLaneWidth()) + simSetup.getParkingLength();

        // Add the area of the parking area (w*h) (+1 to account for the top WEST road)
        double totalArea = parkingAreaLength*((simSetup.getNumberOfParkingLanes()+1)*simSetup.getLaneWidth());

        // Add the West road, but only up to the
        // length of the parking area

        return totalArea;
    }

    public void updateAreaValue(CPMAutoDriverParamPanel paramPanel) {
        double parkingAreaLength = (2*paramPanel.getAccessLength()) +
                (2*paramPanel.getLaneWidth()) + paramPanel.getParkingLength();

        // Add the area of the parking area (w*h) (+1 to account for the top WEST road)
        double totalArea = parkingAreaLength*((paramPanel.getNumberOfParkingLanes()+1)*paramPanel.getLaneWidth());

        // Add the West road, but only up to the
        // length of the parking area
        setText(prefix + String.format("%.2f", totalArea));
    }
}
