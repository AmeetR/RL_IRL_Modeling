package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.Serializable;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridCell;
import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridModel;

/**
* Panel include the GridTable.
*/
public class TablePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5089085904642045141L;
	GridTable table;
	JScrollPane scrollPane;
	JPopupMenu popupMenu;
	JMenu wallMenu;
	MenuAction up, down, left, right, reset;
	SpreadsheetRowHeader rowHeader;
	
	/**
	 * Constructor
	 * @param parent GridWorldGUI
	 */
	public TablePanel(GridWorldGUI parent) {
		popupMenu = new JPopupMenu();
		wallMenu = new JMenu("Set Wall");
		up = new MenuAction("Up", "Set wall at the upper of the cell");
		wallMenu.add(up);
		down = new MenuAction("Down", "Set wall at the lower of the cell");
		wallMenu.add(down);
		left = new MenuAction("Left", "Set wall at the left of the cell");
		wallMenu.add(left);
		right = new MenuAction("Right", "Set wall at the right of the cell");
		wallMenu.add(right);
		reset = new MenuAction("Reset Wall", "Remove every wall in the selection");
		
		popupMenu.add(wallMenu);
		popupMenu.add(reset);
		this.setLayout(new BorderLayout());
		table = new GridTable(parent);
		table.addMouseListener(new GridTableMouseAdapter(popupMenu, table));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		rowHeader = new SpreadsheetRowHeader(table);
		scrollPane = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setRowHeaderView(rowHeader);
		this.add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Returns the row header table of the grid table
	 * @return row header table
	 */
	public SpreadsheetRowHeader getRowHeader() {
		return rowHeader;
	}

	/**
	 * Returns the grid table
	 * @return grid table
	 */
	public GridTable getTable() {
		return table;
	}

	/**
	 * Action for the Menu
	 *
	 */
	class MenuAction extends AbstractAction implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 7448891631161089588L;

		/**
		 * Constructor
		 * @param text Text of the Action. shown in the JMenuItem
		 * @param icon Icon of the JMenuItem
		 * @param description description of the JMenuItem
		 */
		public MenuAction(String text, Icon icon, String description) {
			super(text, icon);
			putValue(SHORT_DESCRIPTION, description);
		}

		/**
		 * Constructor
		 * @param text Text of the Action. shown in the JMenuItem
		 * @param description description of the JMenuItem
		 */
		public MenuAction(String text, String description) {
			super(text);
			putValue(SHORT_DESCRIPTION, description);
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			try {
				int[] cols = table.getSelectedColumns();
				int[] rows = table.getSelectedRows();
				if (cols.length != 0 && rows.length != 0) {
					for (int col = 0; col < cols.length; col++) {
						for (int row = 0; row < rows.length; row++) {
							GridCell cell = GridModel.getInstance().getCell(cols[col], rows[row]);
							if (e.getActionCommand().equals("Up")) {
								cell.setWall(GridModel.UP);
							}
							if (e.getActionCommand().equals("Down")) {
								cell.setWall(GridModel.DOWN);
							}
							if (e.getActionCommand().equals("Left")) {
								cell.setWall(GridModel.LEFT);
							}
							if (e.getActionCommand().equals("Right")) {
								cell.setWall(GridModel.RIGHT);
							}
							if (e.getActionCommand().equals("Reset Wall")){
								cell.resetWall();
							}
							table.getModel().fireTableCellUpdated(rows[row], cols[col]);
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}

