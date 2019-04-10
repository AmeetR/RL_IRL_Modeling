package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;

import java.text.NumberFormat;

import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridCell;
import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridModel;

public class QTableCell extends GridTableCell{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2650916562379515494L;

	public QTableCell(){
		super();
	}

	@Override
	public void setValue(GridCell cell) {
		this.cell = cell;
		NumberFormat n = NumberFormat.getInstance();
		n.setMaximumFractionDigits(4);
		this.up.setText(n.format(cell.getQValue(GridModel.UP)));
		this.down.setText(n.format(cell.getQValue(GridModel.DOWN)));
		this.left.setText(n.format(cell.getQValue(GridModel.LEFT)));
		this.right.setText(n.format(cell.getQValue(GridModel.RIGHT)));
	}
	
	
}
