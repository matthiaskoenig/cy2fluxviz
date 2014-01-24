package cyfluxviz.gui;

import javax.swing.table.DefaultTableModel;

import cyfluxviz.FluxDis;
import cyfluxviz.FluxDisCollection;

@SuppressWarnings("serial")
public class FluxDisTableModel extends DefaultTableModel{
	private static final String[] columnNames = {"Id", "Name", "Network"};
	
	
	public FluxDisTableModel(){
		super(columnNames, 0);	// call DefaultTableModel constructor
	}
	
	public int getFluxDisId(int rowNum){
		return (Integer) getValueAt(rowNum, 0);
	}
		
	public void updateTableModel(){
		clearTable();
		for (FluxDis fd : FluxDisCollection.getInstance().getFluxDistributions()){
			Object[] row = getTableRowFromFluxDistribution(fd);
			addRow(row);
		}
	}
	
	public Object[] getTableRowFromFluxDistribution(FluxDis fd){
		Object[] row = new Object[columnNames.length];
		row[0] = fd.getId();
		row[1] = fd.getName();
		row[2] = fd.getNetworkId();
		return row;
	}

	public void clearTable(){
		if (getRowCount() != 0) {
			setRowCount(0);
		}
	}
	
	/** Overwrite to make non editable. */
    public boolean isCellEditable(int row, int column) {
       //all cells false
       return false;
    }
	
	/** Overwrite for proper sorting on types in column- */
	public Class<?> getColumnClass(int colNum) {
		 switch (colNum) {
         case 0:
             return Integer.class;
         default:
             return String.class;
		 }
	}
	
	public String[] getColumnNames(){
		return columnNames;
	}
	
}
