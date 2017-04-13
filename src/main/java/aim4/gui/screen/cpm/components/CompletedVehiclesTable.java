package aim4.gui.screen.cpm.components;

import aim4.gui.screen.merge.components.MapKeyTableModel;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator.*;
import aim4.vehicle.VehicleSimModel;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;
import aim4.vehicle.merge.MergeVehicleSimModel;

import javax.swing.*;
import java.util.List;
import java.util.Map;

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
        List<CPMBasicAutoVehicle> vehiclesOnMap = sim.getMap().getVehicles();
        for(int vin : vehiclesOnMap){
            if (model.)
            model.addOrUpdateRow(vin, new Object[]{
                    vin,
                    vehicle.getSpec().getName(),
                    vehicle.getPosition().getX(),
                    vehicle.getPosition().getY()
            });
        }
    }

}
