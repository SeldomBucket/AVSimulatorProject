package aim4.gui.screen.merge.components;

import aim4.sim.simulator.merge.CoreMergeSimulator;
import aim4.sim.simulator.merge.MergeSimulator;
import aim4.vehicle.merge.MergeVehicleSimModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by Callum on 26/03/2017.
 */
public class ActiveVehicleList extends JPanel implements MergeStatScreenComponent {
    private MapKeyTableModel model;
    private JTable table;
    private JScrollPane scrollPane;

    public ActiveVehicleList() {
        this.model = new MapKeyTableModel(new String[]{
                "VIN",
                "Velocity",
                "X Position",
                "Y Position",
                "State"
        });
        this.table = new JTable(model);
        scrollPane = new JScrollPane(table);
        scrollPane.setVisible(true);

        this.setLayout(new GridLayout(0, 1));
        this.add(scrollPane);
    }

    @Override
    public void update(MergeSimulator sim, List<CoreMergeSimulator.CoreMergeSimStepResult> results) {
        Map<Integer, MergeVehicleSimModel> vinToVehicles = sim.getVinToVehicles();
        for(int vin : vinToVehicles.keySet()){
            MergeVehicleSimModel vehicle = vinToVehicles.get(vin);
            model.addOrUpdateRow(vin, new Object[]{
                    vin,
                    vehicle.getVelocity(),
                    vehicle.getPosition().getX(),
                    vehicle.getPosition().getY(),
                    vehicle.getDriver().getStateString()
            });
        }
        for(CoreMergeSimulator.CoreMergeSimStepResult stepResult : results) {
            for (int vin : stepResult.getCompletedVehicles().keySet()) {
                try {
                    model.removeRow(vin);
                } catch(NoSuchElementException e) {
                    /*TODO: Handle more elegantly. Usually happens in TurboMode where there was not enough time to add the vehicle to the table with before the vehicle finished! */
                }
            }
        }
    }
}
