package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;


import java.io.Serializable;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridCell;

/**
* Listener for the selections in the grid table
*/
public class GridTableSelectionListener implements ListSelectionListener, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8172742854182886418L;
	JTable table;
	ToolPanel toolbar;

	/**
	 * Constructor
	 * @param table grid table
	 * @param toolbar toolpanel to set the reward
	 */
	public GridTableSelectionListener(JTable table, ToolPanel toolbar) {
		super();
		this.table = table;
		this.toolbar = toolbar;
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent arg0) {
		// Ignore extra messages.
		if (arg0.getValueIsAdjusting())
			return;
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();
		if (row != -1 && col != -1) {
			GridCell cell = (GridCell) table.getModel().getValueAt(row, col);
			for (int i = 0; i < 4; i++) {
				toolbar.setRewards(i, cell.getReward(i));
			}
			toolbar.isTerminalState(cell.isTerminalState());
		}
	}

}

