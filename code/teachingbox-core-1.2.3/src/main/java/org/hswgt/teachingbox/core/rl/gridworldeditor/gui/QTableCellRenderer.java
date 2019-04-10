package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridCell;
import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridModel;

public class QTableCellRenderer extends QTableCell implements TableCellRenderer, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3303756752318485910L;

	private Color colorFocus = new Color(224, 254, 173);

	private Color colorNormal = UIManager.getColor("TextField.background");

	private Color colorActive;

	private Font font;

	/**
	 * Constructor
	 */
	public QTableCellRenderer() {
		setOpaque(true);
		this.font = new Font("Dialog", Font.PLAIN, 12);
		colorActive = GridWorldPropertyManager.getColor();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof GridCell) {
			GridCell cell = (GridCell) value;
			this.setValue(cell);
		}
		if (isSelected) {
			this.setBackground(colorFocus);
			this.setFieldBackground(colorFocus);
		} else {
			this.setBackground(colorNormal);
			this.setFieldBackground(colorNormal);

		}
		if (GridModel.getInstance().getActCol() == column && GridModel.getInstance().getActRow() == row) {
			Border test = BorderFactory.createLineBorder(colorActive, 2);
			this.setBorder(test);
		} else {
			this.setBorder(null);
		}
		this.setRewardFont(this.font);
		return this;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setFont(java.awt.Font)
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * Set the active color to show if the cell is active
	 * @param color new active color
	 */
	public void setActiveColor(Color color) {
		this.colorActive = color;
	}
}
