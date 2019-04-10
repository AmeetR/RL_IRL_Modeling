package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.io.Serializable;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
* Row header table. This table looks like a row header for the grid table.
*/
public class SpreadsheetRowHeader extends JTable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1383541735339634189L;
	TableCellRenderer render = new RowHeaderRenderer();

	/**
	 * Constructor
	 * 
	 * @param table
	 *            GridTable
	 */
	public SpreadsheetRowHeader(JTable table) {
		super(new RowHeaderModel(table));
		configure(table);

	}

	/**
	 * Configure the table
	 * 
	 * @param table
	 *            GridTable
	 */
	protected void configure(JTable table) {
		setRowHeight(table.getRowHeight());
		setIntercellSpacing(new Dimension(0, 0));
		setShowHorizontalLines(false);
		setShowVerticalLines(false);
	}

	/**
	 * Set the heigth of one row in the header table
	 * 
	 * @param row The row
	 * @param height The height
	 */
	public void setHeight(int row, int height) {
		setRowHeight(row, height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JTable#getPreferredScrollableViewportSize()
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(32, super.getPreferredSize().height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JTable#getDefaultRenderer(java.lang.Class)
	 */
	public TableCellRenderer getDefaultRenderer(Class c) {
		return render;
	}

	/**
	 * Model for the row header table
	 * 
	 * 
	 */
	static class RowHeaderModel extends AbstractTableModel implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3854277130135724146L;
		JTable table;

		/**
		 * Constructor
		 * 
		 * @param tableToMirror
		 *            table to mirror
		 */
		protected RowHeaderModel(JTable tableToMirror) {
			table = tableToMirror;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			return table.getModel().getRowCount();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() {
			return 1;
		}

		public Object getValueAt(int row, int column) {
			return String.valueOf(row);
		}
	}

	/**
	 * Renderer for the row header table
	 * 
	 * @author tobiby
	 * 
	 */
	static class RowHeaderRenderer extends DefaultTableCellRenderer implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8760816124552091152L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent
		 * (javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelect, boolean hasFocus, int row,
				int column) {

			setBackground(UIManager.getColor("TableHeader.background"));
			setForeground(UIManager.getColor("TableHeader.foreground"));
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			setFont(UIManager.getFont("TableHeader.font"));
			setHorizontalTextPosition(CENTER);
			setValue(value);
			return this;
		}
	}
}
