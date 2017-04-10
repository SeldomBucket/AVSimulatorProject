package aim4.gui.screen.merge.components;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by Callum on 10/04/2017.
 */
public class MapKeyTableModel extends AbstractTableModel {
    private LinkedList<Object[]> rows;
    private Map<Object, Object[]> rowMap;
    private String[] columnHeadings;

    public MapKeyTableModel(String[] columnHeadings) {
        this.columnHeadings = columnHeadings;

        rows = new LinkedList<Object[]>();
        rowMap = new HashMap<Object, Object[]>();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columnHeadings.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rows.get(rowIndex)[columnIndex];
    }

    @Override
    public String getColumnName(int col) {
        return columnHeadings[col];
    }

    public void removeRow(Object key) {
        int index = rows.indexOf(rowMap.get(key));
        boolean successful = rows.remove(rowMap.get(key));
        if(!successful)
            throw new NoSuchElementException("Attempted to remove a row that did not exist");
        fireTableRowsDeleted(index, index);
    }

    public void addOrUpdateRow(Object key, Object[] row) {
        if(rowMap.containsKey(key)){
            int index = rows.indexOf(rowMap.get(key) /*Old row*/);
            rows.set(index, row);
            rowMap.put(key, row);
            fireTableRowsUpdated(index, index);
        } else {
            rows.add(row);
            rowMap.put(key, rows.getLast());
            fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
        }
    }
}
