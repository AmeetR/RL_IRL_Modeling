package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;


import java.io.Serializable;

import javax.swing.table.AbstractTableModel;

import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridModel;

/**
* DataModel of the gridTable. Get the value out of the GridModel.
*/
public class GridTableModel extends AbstractTableModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4717310976230142373L;

	/**
	 * Constructor
	 */
	public GridTableModel(){	
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return GridModel.getInstance().getSize().width;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return GridModel.getInstance().getSize().height;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int Y, int X) {
		return GridModel.getInstance().getCell(X, Y);
	}
}
