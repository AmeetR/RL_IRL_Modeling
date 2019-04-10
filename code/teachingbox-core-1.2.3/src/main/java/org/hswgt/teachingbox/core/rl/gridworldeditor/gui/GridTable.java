package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;

import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridModel;


/**
* JTable that display the Content of the GridModel.
*/
public class GridTable extends JTable implements Serializable, Observer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7671972513801498697L;
	private GridTableModel model;
	private ListSelectionModel listSelectionModel;
	private GridWorldGUI parent;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            GridWorldGUI
	 */
	public GridTable(GridWorldGUI parent) {
		this.parent = parent;
		model = new GridTableModel();
		this.setDefaultRenderer(Object.class, new GridTableCellRenderer());
		this.setModel(model);
		this.setGridColor(Color.GRAY);
		//this.setRowHeight(80);
		listSelectionModel = this.getSelectionModel();
		listSelectionModel
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		listSelectionModel.addListSelectionListener(new GridTableSelectionListener(this,parent.getToolBar()));
		this.setSelectionModel(listSelectionModel);
		this.setCellSelectionEnabled(true);
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
		
		this.packCols();
		this.packRows();
	}
	
	/**
	 * Set the color of the active cell in the table
	 * @param color new Color of the cell
	 */
	public void setActiveColor(Color color){
		for(int col = 0; col < this.getColumnCount(); col++){
			for(int row = 0; row < this.getRowCount(); row++){
				if(this.getCellRenderer(row, col) instanceof GridTableCellRenderer){
					GridTableCellRenderer rend = (GridTableCellRenderer)this.getCellRenderer(row, col);
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
					GridTableCellRenderer rend = (GridTableCellRenderer)this.getCellRenderer(row, col);
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
        packRows(this, 0, parent.getTablePanel().getRowHeader());
    	//ackRows(this, 0, this.getRowCount(), 0);
    }
    
    /**
     * The width of each column is set to the preferred height of the tallest cell in that column.
     */
    public void packCols() {
        packCols(this, 0);
    }
    
    /**
     * The height of a row is set to the preferred height of the tallest cell in that row.
     * @param table The JTable object
     * @param margin The margin
     * @param rowHeader The row header
     */
    public void packRows(JTable table, int margin, SpreadsheetRowHeader rowHeader) {
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
     * @param table
     * @param colIndex
     * @param margin
     * @return The result is equal to the tallest cell in the column.
     */
    private int getPreferredColWidth(JTable table, int colIndex, int margin){
    	 // Get the current default height for all rows
        int width = 0;
    
        // Determine widest cell in the row
        for (int r=0; r<table.getRowCount(); r++) {
            TableCellRenderer renderer = table.getCellRenderer(r,colIndex);
            Component comp = table.prepareRenderer(renderer, r, colIndex);
            int w = comp.getPreferredSize().width + 50 + 2*margin;
            width = Math.max(width, w);
        }
        return width;
    }
    
    /**
     * For each row less than start and greater than end, the width of a column is set to the preferred width of the tallest cell in that column.
     * @param table The JTable object
     * @param margin The margin
     */
    public void packCols(JTable table, int margin) {
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
