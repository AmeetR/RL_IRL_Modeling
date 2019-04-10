package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridModel;

public class QTable extends JTable implements Observer, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8011122680224304931L;
	private GridTableModel model;
	private QValuePanel parent;
	
	/**
	 * Constructor
	 * 
	 * @param parent
	 *            GridWorldGUI
	 */
	public QTable(QValuePanel parent) {
		this.parent = parent;
		model = new GridTableModel();
		this.setDefaultRenderer(Object.class, new QTableCellRenderer());
		this.setModel(model);
		this.setGridColor(Color.GRAY);
		this.setRowHeight(100);
		for (int c=0; c<this.getColumnCount(); c++) {
			this.getColumnModel().getColumn(c).setPreferredWidth(120);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JTable#getModel()
	 */
	public GridTableModel getModel() {
		return model;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable arg0, Object arg1) {
		this.getModel().fireTableCellUpdated(GridModel.getInstance().getActRow(), GridModel.getInstance().getActCol());
	}
	
	/**
	 * Set the color of the active cell in the table
	 * @param color new Color of the cell
	 */
	public void setActiveColor(Color color){
		for(int col = 0; col < this.getColumnCount(); col++){
			for(int row = 0; row < this.getRowCount(); row++){
				if(this.getCellRenderer(row, col) instanceof GridTableCellRenderer){
					QTableCellRenderer rend = (QTableCellRenderer)this.getCellRenderer(row, col);
					rend.setActiveColor(color);
				}
			}
		}
	}
	
	/**
	 * Set the font of the rewards in the table
	 * @param font Font for the rewards
	 */
	public void setRewardFont(Font font){
		for(int col = 0; col < this.getColumnCount(); col++){
			for(int row = 0; row < this.getRowCount(); row++){
				if(this.getCellRenderer(row, col) instanceof GridTableCellRenderer){
					QTableCellRenderer rend = (QTableCellRenderer)this.getCellRenderer(row, col);
					rend.setFont(font);
				}
			}
		}
		this.getModel().fireTableDataChanged();
		packRows();
		packCols();
	}
	
    /**
     * The height of each row is set to the preferred height of the tallest cell in that row.
     */
    public void packRows() {
        packRows(this, 0, this.getRowCount(), 0, parent.getRowHeader());
    }
    
    /**
     * The width of each column is set to the preferred height of the tallest cell in that column.
     */
    public void packCols() {
        packCols(this, 0, this.getColumnCount(), 0);
    }
    
    /**
     * For each row greater than start and less than end, the height of a row is set to the preferred height of the tallest cell in that row.
     * @param table The table
     * @param start the minimum row
     * @param end the maximum row
     * @param margin the margin
     * @param rowHeader thw row header
     */
    public void packRows(JTable table, int start, int end, int margin, SpreadsheetRowHeader rowHeader) {
        for (int r=0; r<table.getRowCount(); r++) {
            // Get the preferred height
            int h = getPreferredRowHeight(table, r, margin);
    
            // Now set the row height using the preferred height
            if (table.getRowHeight(r) != h) {
                table.setRowHeight(r, h);
                rowHeader.setHeight(r, h);
            }
        }
    }
    
    /**
     *  Returns the preferred height of a row.
     * @param table
     * @param rowIndex
     * @param margin
     * @return The result is equal to the tallest cell in the row.
     */
    private int getPreferredRowHeight(JTable table, int rowIndex, int margin) {
        // Get the current default height for all rows
        int height = table.getRowHeight();
    
        // Determine highest cell in the row
        for (int c=0; c<table.getColumnCount(); c++) {
            TableCellRenderer renderer = table.getCellRenderer(rowIndex, c);
            Component comp = table.prepareRenderer(renderer, rowIndex, c);
            int h = comp.getPreferredSize().height + 10 + 2*margin;
            height = Math.max(height, h);
        }
        return height;
    }

    /**
     * Returns the preferred width of a col.
     * @param table The JTable object
     * @param colIndex The column id
     * @param margin The margin
     * @return The result is equal to the tallest cell in the column.
     */
    private int getPreferredColWidth(JTable table, int colIndex, int margin){
    	 // Get the current default height for all rows
        int width = 0;
    
        // Determine widest cell in the row
        for (int r=0; r<table.getRowCount(); r++) {
            TableCellRenderer renderer = table.getCellRenderer(r, colIndex);
            Component comp = table.prepareRenderer(renderer, r, colIndex);
            int w = comp.getPreferredSize().width + 25 + 2*margin;
            width = Math.max(width, w);
        }
        return width;
    }
    
    /**
     * For each row greater than start and less than end, the width of a column is set to the preferred width of the tallest cell in that column.
     * @param table The table object
     * @param start the start row
     * @param end the end row
     * @param margin the margin
     */
    public void packCols(JTable table, int start, int end, int margin) {
        for (int c=0; c<table.getColumnCount(); c++) {
            // Get the preferred height
            int w = getPreferredColWidth(table, c, margin);
    
            // Now set the row height using the preferred height
            if (table.getColumnModel().getColumn(c).getPreferredWidth() != w) {
                table.getColumnModel().getColumn(c).setPreferredWidth(w);
            }
        }
    }
    
    public void setColSize(int size){
    	for (int c=0; c<this.getColumnCount(); c++) {
            // Now set the row height using the preferred height
            if (this.getColumnModel().getColumn(c).getPreferredWidth() != size) {
                this.getColumnModel().getColumn(c).setPreferredWidth(size);
            }
        }
    }
    
    public int getColSize(){
    	int max = 0;
    	for (int c=0; c<this.getColumnCount(); c++) {
            // Now set the row height using the preferred height
            if (this.getColumnModel().getColumn(c).getPreferredWidth() > max) {
                max = this.getColumnModel().getColumn(c).getPreferredWidth();
            }
        }
    	return max;
    }
    
    public int getRowSize(){
    	int max = 0;
    	for(int r = 1; r < this.getRowCount(); r++){
    		if(this.getRowHeight(r)>max){
    			max = this.getRowHeight(r);
    		}
    	}
    	return max;
    }
}
