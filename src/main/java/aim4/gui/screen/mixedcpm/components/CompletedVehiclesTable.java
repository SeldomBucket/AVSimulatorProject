package aim4.gui.screen.mixedcpm.components;

import aim4.gui.screen.merge.components.MapKeyTableModel;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator.*;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * A table displaying information for the vehicles that have completed the simulation.
 */
public class CompletedVehiclesTable extends JPanel implements MixedCPMStatScreenComponent {
    private MapKeyTableModel model; // TODO CPM Move this class out of merge
    private JTable table;
    private JScrollPane scrollPane;
    private JLabel title;

    public CompletedVehiclesTable() {
        this.model = new MapKeyTableModel( new String[]{
                "VIN",
                "Spec",
                "Entry time",
                "Parking time",
                "Exit time"
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.title = new JLabel("Completed Vehicles Table:");
        this.title.setOpaque(true);
        this.table = new JTable(model);
        scrollPane = new JScrollPane(table);
        scrollPane.setVisible(true);
        this.add(title);
        this.add(scrollPane);
    }

    @Override
    public void update(MixedCPMAutoDriverSimulator sim,
                       List<MixedCPMAutoDriverSimStepResult> results) {
        for(MixedCPMAutoDriverSimStepResult stepResult : results) {
            for (MixedCPMBasicManualVehicle vehicle : stepResult.getCompletedVehicles()) {
                model.addOrUpdateRow(vehicle.getVIN(), new Object[]{
                        vehicle.getVIN(),
                        vehicle.getSpec().getName(),
                        vehicle.getEntryTime(),
                        vehicle.getParkingTime(),
                        vehicle.getExitTime()
                });
            }
        }
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }

}
