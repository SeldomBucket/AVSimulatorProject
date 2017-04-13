package aim4.gui.screen.cpm.components;

import aim4.gui.screen.merge.components.MapKeyTableModel;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator.*;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import javax.swing.*;
import java.util.List;

/**
 * Created by Becci on 13-Apr-17.
 */
public class CompletedVehiclesTable extends JPanel implements CPMStatScreenComponent {
    private MapKeyTableModel model; // TODO CPM Move this class out of merge
    private JTable table;
    private JScrollPane scrollPane;

    public CompletedVehiclesTable() {
        this.model = new MapKeyTableModel( new String[]{
                "VIN",
                "Spec",
                "Entry time",
                "Parking time",
                "Exit time",
                "Retrieval time",
                "Number of re-entries",
                "Completed",
                "Distance travelled"
        });

        this.table = new JTable(model);
        scrollPane = new JScrollPane(table);
        scrollPane.setVisible(true);
        this.add(scrollPane);
    }

    @Override
    public void update(CPMAutoDriverSimulator sim, List<CPMAutoDriverSimStepResult> results) {
        /*for(CPMAutoDriverSimStepResult stepResult : results) {
            for (int vin : stepResult.getCompletedVehicles().keySet()) {
                MergeVehicleSimModel vehicle = stepResult.getCompletedVehicles().get(vin);
                model.addOrUpdateRow(vin, new Object[]{
                        vehicle.getVIN(),
                        vehicle.getVelocity(),
                        vehicle.getPosition().getX(),
                        vehicle.getPosition().getY(),
                        vehicle.getMaxAcceleration(),
                        vehicle.getMaxDeceleration(),
                        sim.calculateDelay(vehicle)
                });
            }
        }*/
    }

}
