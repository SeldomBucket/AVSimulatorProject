package aim4.gui.viewer;

import aim4.config.Debug;
import aim4.gui.StatusPanelContainer;
import aim4.gui.Viewer;
import aim4.gui.frame.VehicleInfoFrame;
import aim4.gui.setuppanel.SimSetupPanel;
import aim4.im.IntersectionManager;
import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.sim.AutoDriverOnlySimulator;
import aim4.sim.Simulator;
import aim4.sim.setup.BasicSimSetup;
import aim4.sim.setup.SimSetup;
import aim4.vehicle.VehicleSimView;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * Created by Callum on 09/11/2016.
 */
public class AIMSimViewer extends SimViewer {
    public AIMSimViewer(StatusPanelContainer statusPanel, Viewer viewer){
        super(statusPanel, viewer, new BasicSimSetup(1, // columns
                1, // rows
                4, // lane width
                25.0, // speed limit
                3, // lanes per road
                1, // median size
                150, // distance between
                0.28, // traffic level
                1.0 // stop distance before intersection
        ), new SimSetupPanel(new BasicSimSetup(1, // columns
                1, // rows
                4, // lane width
                25.0, // speed limit
                3, // lanes per road
                1, // median size
                150, // distance between
                0.28, // traffic level
                1.0 // stop distance before intersection
        )));
    }

    /** The frame for showing a vehicle information */
    private VehicleInfoFrame vehicleInfoFrame;

    @Override
    protected Simulator.SimStepResult runSimulationStep() {
        Simulator.SimStepResult simStepResult = super.runSimulationStep();
        if (simStepResult instanceof AutoDriverOnlySimulator.AutoDriverOnlySimStepResult) {
            AutoDriverOnlySimulator.AutoDriverOnlySimStepResult simStepResult2 =
                    (AutoDriverOnlySimulator.AutoDriverOnlySimStepResult) simStepResult;
            for (int vin : simStepResult2.getCompletedVINs()) {
                Debug.removeVehicleColor(vin);
            }
        }
        return simStepResult;
    }

    // ///////////////////////////////
    // MouseListener interface
    // ///////////////////////////////
    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO: may be move this function to canvas.
        // right click
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (sim != null) {
                Point2D leftClickPoint = canvas.getMapPosition(e.getX(), e.getY());
                // See if we hit any vehicles
                for (VehicleSimView vehicle : sim.getActiveVehicles()) {
                    if (vehicle.getShape().contains(leftClickPoint)) {
                        if (Debug.getTargetVIN() != vehicle.getVIN()) {
                            Debug.setTargetVIN(vehicle.getVIN());
                            if (vehicleInfoFrame == null) {
                                vehicleInfoFrame = new VehicleInfoFrame(this);
                            }
                            if (!vehicleInfoFrame.isVisible()) {
                                vehicleInfoFrame.setVisible(true);
                                this.requestFocusInWindow();
                                this.requestFocus();
                            }
                            vehicleInfoFrame.setVehicle(vehicle);
                        } else {
                            Debug.removeTargetVIN();
                            vehicleInfoFrame.setVehicle(null);
                        }
                        canvas.update();
                        return;  // just exit
                    }
                }
                // see if we hit any intersection
                for (IntersectionManager im : sim.getMap().getIntersectionManagers()) {
                    if (im.getIntersection().getArea().contains(leftClickPoint)) {
                        if (Debug.getTargetIMid() != im.getId()) {
                            Debug.setTargetIMid(im.getId());
                        } else {
                            Debug.removeTargetIMid();
                        }
                        canvas.cleanUp();  // TODO: ugly code, one more reason to move this
                        // function to canvas
                        canvas.update();
                        return;  // just exit
                    }
                }
                // hit nothing, just unselect the vehicle and intersection manager.
                Debug.removeTargetVIN();
                if (vehicleInfoFrame != null) {
                    vehicleInfoFrame.setVehicle(null);
                }
                Debug.removeTargetIMid();
                canvas.cleanUp();
                canvas.update();
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            if (sim != null) {
                Point2D rightClickPoint = canvas.getMapPosition(e.getX(), e.getY());
                System.err.printf("Right click at (%.0f, %.0f)\n",
                        rightClickPoint.getX(), rightClickPoint.getY());
                // print the lane id
                for (Road r : sim.getMap().getRoads()) {
                    for (Lane l : r.getLanes()) {
                        if (l.getShape().contains(rightClickPoint)) {
                            System.err.printf("Right click on lane %d\n", l.getId());
                        }
                    }
                }
            }
        } // else ignore other event
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent e) {
    }


}
