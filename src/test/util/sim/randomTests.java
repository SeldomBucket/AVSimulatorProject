package util.sim;

import org.junit.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by Callum on 10/04/2017.
 */

public class randomTests {
    @Test
    public void testDefaultTableModel() throws Exception {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("X");
        model.addColumn("Y");
        model.addColumn("Z");
        JTable table = new JTable(model);

        model.addRow(new Object[]{1,2,3});
        model.addRow(new Object[]{4,5,6});
        model.addRow(new Object[]{7,8,9});
        model.addRow(new Object[]{10,11,12});

        assertEquals(model.getRowCount(), 4);
        assertEquals(model.getValueAt(0,0), 1);
        assertEquals(model.getValueAt(1,0), 4);
        assertEquals(model.getValueAt(2,0), 7);
        assertEquals(model.getValueAt(3,0), 10);

        model.removeRow(1);

        assertEquals(model.getRowCount(), 3);
        assertEquals(model.getValueAt(0,0), 1);
        assertEquals(model.getValueAt(2,0), 7);
        assertEquals(model.getValueAt(3,0), 10);
    }


}
