package aim4.gui.parampanel.cpm.components;

import aim4.gui.component.LabeledSlider;
import aim4.gui.parampanel.cpm.CPMBasicParamPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A labeled slider that updates the area of the car park when the value of the slider is changed.
 */
public class CPMLabeledSlider extends LabeledSlider {

    /**
     * The param panel this slider belongs to.
     */
    private CPMBasicParamPanel paramPanel;

    public CPMLabeledSlider(double minValue, double maxValue, double defaultValue,
                            double majorTick, double minorTick, String labelFormat,
                            String tickLabelFormat, ChangeListener changeListener,
                            CPMBasicParamPanel paramPanel) {
        super(minValue, maxValue, defaultValue, majorTick, minorTick, labelFormat,
                tickLabelFormat, changeListener);
        this.paramPanel = paramPanel;
    }

    public CPMLabeledSlider(double minValue, double maxValue, double defaultValue,
                            double majorTick, double minorTick, String labelFormat,
                            String tickLabelFormat, CPMBasicParamPanel paramPanel) {
        super(minValue, maxValue, defaultValue, majorTick, minorTick, labelFormat, tickLabelFormat);
        this.paramPanel = paramPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        setLabel(source.getValue());

        // update the area of the car park
        paramPanel.getMapAreaLabel().updateAreaValue(paramPanel);

        if (changeListener != null) {
            changeListener.stateChanged(new ChangeEvent(this));
        }
    }
}
