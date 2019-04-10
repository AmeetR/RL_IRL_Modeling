package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;


import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridModel;

/**
* Panel to set the size of the grid world. After changing the size the GridModel will be cleared.
*/
public class SizePanel extends JToolBar implements ActionListener, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -874654482863753759L;

	JTextField rowInput;

	JTextField colInput;

	JButton setSize;
	
	GridTableModel tableModel;
	
	TablePanel tablePanel;
	
	/**
	 * Constructor
	 * @param tablePanel TablePanel
	 */
	public SizePanel(TablePanel tablePanel) {
		this.tableModel = tablePanel.getTable().getModel();
		this.tablePanel = tablePanel;
		this.setLayout(new GridLayout(3, 2));
		this.add(new JLabel("Columns: "));
		colInput = new JTextField();
		this.add(colInput);
		this.add(new JLabel("Rows: "));
		rowInput = new JTextField();
		this.add(rowInput);
		this.add(new JLabel(""));
		setSize = new JButton("SetSize");
		setSize.addActionListener(this);
		this.add(setSize);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		int row, col;
		try {
			row = Integer.parseInt(this.rowInput.getText());
			col = Integer.parseInt(this.colInput.getText());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Bitte gültige Werte eingeben!!! ");
			return;
		}
		try {
			GridModel.getInstance().setSize(col, row);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		tableModel.fireTableStructureChanged();
		tablePanel.getTable().packCols();
		tablePanel.getTable().packRows();
	}
}
