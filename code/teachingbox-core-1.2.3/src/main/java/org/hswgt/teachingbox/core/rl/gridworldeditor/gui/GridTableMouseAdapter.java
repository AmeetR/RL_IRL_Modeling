package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 * Mouselistener of the gridtable. Shows the popupmenu.$
 */
public class GridTableMouseAdapter extends MouseAdapter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3013403021747836532L;
	JPopupMenu popupMenu;
	JTable table;

	/**
	 * Constructor
	 * 
	 * @param popupMenu
	 *            popupmenu thats shown
	 * @param table
	 *            the grid table
	 */
	public GridTableMouseAdapter(JPopupMenu popupMenu, JTable table) {
		this.popupMenu = popupMenu;
		this.table = table;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		showPopup(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		showPopup(e);
	}

	/**
	 * set the popup visible
	 * 
	 * @param e
	 *            MouseEvent to detect the right click
	 */
	private void showPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			Point p = e.getPoint();
			int row = table.rowAtPoint(p);
			int col = table.columnAtPoint(p);
			if (!table.isCellSelected(row, col)) {
				table.getSelectionModel().setSelectionInterval(col, col);
				table.setColumnSelectionInterval(col, col);
				table.setRowSelectionInterval(row, row);
			}
			popupMenu.show(e.getComponent(), e.getX(), e.getY());

		}
	}
}
