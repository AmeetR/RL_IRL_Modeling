package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridModel;

/**
 * ToolPane. Include the Inputfields for the rewards.
 */
public class ToolPanel extends JToolBar implements ActionListener, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8567511121383750380L;

	JPanel tools;

	CTextField up, down, left, right;

	JButton send, resize;
	
	JCheckBox terminalState;

	GridTable table;

	GridWorldGUI frame;

	/**
	 * Constructor
	 * 
	 * @param title
	 *            Title of the Toolbar
	 * @param frame
	 *            parent frame in which the toolbar is shown
	 */
	public ToolPanel(String title, GridWorldGUI frame) {
		super(title);
		this.frame = frame;
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		up = new CTextField("Up", 5, 7);
		up.setTexttip("Up");
		up.setHorizontalAlignment(JTextField.CENTER);
		up.setMaximumSize(new Dimension(50, 20));
		up.setMinimumSize(new Dimension(50, 20));
		up.setPreferredSize(new Dimension(50, 20));
		// c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		this.add(up, c);
		down = new CTextField("Down", 5, 7);
		down.setTexttip("Down");
		down.setHorizontalAlignment(JTextField.CENTER);
		down.setMaximumSize(new Dimension(50, 20));
		down.setMinimumSize(new Dimension(50, 20));
		down.setPreferredSize(new Dimension(50, 20));
		// c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 2;
		this.add(down, c);
		left = new CTextField("Left", 5, 7);
		left.setTexttip("Left");
		left.setHorizontalAlignment(JTextField.CENTER);
		left.setMaximumSize(new Dimension(50, 20));
		left.setMinimumSize(new Dimension(50, 20));
		left.setPreferredSize(new Dimension(50, 20));

		c.gridx = 0;
		c.gridy = 1;
		this.add(left, c);
		right = new CTextField("Right", 5, 7);
		right.setTexttip("Right");
		right.setHorizontalAlignment(JTextField.CENTER);
		right.setMaximumSize(new Dimension(50, 20));
		right.setMinimumSize(new Dimension(50, 20));
		right.setPreferredSize(new Dimension(50, 20));
		c.gridx = 2;
		c.gridy = 1;
		this.add(right, c);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		this.add(new JLabel("Terminal State"),c);
		terminalState = new JCheckBox();
		c.gridx = 2;
		c.gridy = 3;
		this.add(terminalState,c);
		send = new JButton("Set");
		send.addActionListener(this);
		send.setMaximumSize(new Dimension(100, 25));
		send.setMinimumSize(new Dimension(100, 25));
		send.setPreferredSize(new Dimension(100, 25));
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 4;
		c.ipady = 5;
		c.insets = new Insets(5, 5, 5, 5);
		this.add(send, c);

		resize = new JButton("adjust columns");
		resize.setActionCommand("resize");
		resize.addActionListener(this);
		resize.setMaximumSize(new Dimension(100, 25));
		resize.setMinimumSize(new Dimension(100, 25));
		resize.setPreferredSize(new Dimension(100, 25));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 6;
		c.ipady = 5;
		c.insets = new Insets(5, 5, 5, 5);
		this.add(resize, c);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		this.table = frame.getTablePanel().getTable();
		if (arg0.getActionCommand().equals("resize")) {
			table.packCols();
			table.packRows();
			frame.getQTablePanel().getQTable().packCols();
			frame.getQTablePanel().getQTable().packRows();
		} else {
			int selRows[] = table.getSelectedRows();
			int selCols[] = table.getSelectedColumns();

			for (int row = 0; row < selRows.length; row++) {
				for (int x = 0; x < selCols.length; x++) {
					GridModel.getInstance().getCell(selCols[x], selRows[row])
							.setReward(Double.parseDouble(this.up.getText()),
									GridModel.UP);
					GridModel.getInstance().getCell(selCols[x], selRows[row])
							.setReward(Double.parseDouble(this.down.getText()),
									GridModel.DOWN);
					GridModel.getInstance().getCell(selCols[x], selRows[row])
							.setReward(Double.parseDouble(this.left.getText()),
									GridModel.LEFT);
					GridModel.getInstance().getCell(selCols[x], selRows[row])
							.setReward(
									Double.parseDouble(this.right.getText()),
									GridModel.RIGHT);
					if(this.terminalState.isSelected()){
						GridModel.getInstance().getCell(selCols[x], selRows[row]).setTerminalState(true);
					}else{
						GridModel.getInstance().getCell(selCols[x], selRows[row]).setTerminalState(false);
					}
				}
			}
			table.getModel().fireTableDataChanged();
			table.packRows();
		}
	}

	/**
	 * Set the reward in the textfield of the respective direction
	 * 
	 * @param direction
	 *            direction where the reward is
	 * @param value
	 *            reward
	 */
	public void setRewards(int direction, double value) {
		if (direction == GridModel.UP) {
			up.setText(Double.toString(value));
		}
		if (direction == GridModel.DOWN) {
			down.setText(Double.toString(value));
		}
		if (direction == GridModel.LEFT) {
			left.setText(Double.toString(value));
		}
		if (direction == GridModel.RIGHT) {
			right.setText(Double.toString(value));
		}
	}
	
	public void isTerminalState(boolean state){
		System.out.println("isTerminalState");
		this.terminalState.setSelected(state);
	}

}
