package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.Serializable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridCell;
import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridModel;


/**
* Cell of the GridTable. 
* Contains a panel with the rewards. This panel is set to the center. The north, south, west an east of the outer panel are used 
* to show the wall elements.
*/
public class GridTableCell extends JPanel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4486641355841789008L;
	protected JTextField up, down, left, right;
	protected JLabel wUp, wLeft, wRight, wDown;
	private JPanel innerPanel;
	protected GridCell cell;
	protected Color wallColor = new Color(139, 0, 0);
	
	/**
	 * Constructor
	 */
	public GridTableCell() {
		this.setLayout(new BorderLayout());
		this.setOpaque(true);
		
		
		innerPanel = new JPanel();
		innerPanel.setOpaque(true);
		innerPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		up = new JTextField();
		up.setHorizontalAlignment(JTextField.CENTER);
		up.setBorder(null);
		c.gridx = 1;
		c.gridy = 0;
		innerPanel.add(up, c);

		down = new JTextField();
		down.setHorizontalAlignment(JTextField.CENTER);
		down.setBorder(null);
		c.gridx = 1;
		c.gridy = 2;
		innerPanel.add(down, c);

		left = new JTextField();
		left.setHorizontalAlignment(JTextField.CENTER);
		left.setBorder(null);
		c.gridx = 0;
		c.gridy = 1;
		innerPanel.add(left, c);

		right = new JTextField();
		right.setHorizontalAlignment(JTextField.CENTER);
		right.setBorder(null);
		c.gridx = 2;
		c.gridy = 1;
		innerPanel.add(right, c);
		this.add(innerPanel, BorderLayout.CENTER);

		wUp = new JLabel(" ");
		wUp.setOpaque(true);
		wUp.setBackground(Color.WHITE);
		this.add(wUp, BorderLayout.NORTH);

		wDown = new JLabel(" ");
		wDown.setOpaque(true);
		wDown.setBackground(Color.WHITE);
		this.add(wDown, BorderLayout.SOUTH);

		wLeft = new JLabel("  ");
		wLeft.setOpaque(true);
		wLeft.setBackground(Color.WHITE);
		this.add(wLeft, BorderLayout.WEST);

		wRight = new JLabel("  ");
		wRight.setOpaque(true);
		wRight.setBackground(Color.WHITE);
		this.add(wRight, BorderLayout.EAST);
	}

	/**
	 * Show the rewards of a GridCell in the reward Panel 
	 * @param cell GridCell contains rewards
	 * @see GridCell#GridCell(int, int)
	 */
	public void setValue(GridCell cell) {
		this.cell = cell;
		this.up.setText(String.valueOf(cell.getReward(GridModel.UP)));
		this.down.setText(String.valueOf(cell.getReward(GridModel.DOWN)));
		this.left.setText(String.valueOf(cell.getReward(GridModel.LEFT)));
		this.right.setText(String.valueOf(cell.getReward(GridModel.RIGHT)));
	}

	/**
	 * Set the wall in a GridTableCell. If a Element has no wall the parameter color is used.
	 * @param color Color to use if an element has no wall
	 */
	public void setFieldBackground(Color color) {

		try {
			if (cell.getWall(GridModel.UP)) {
				wUp.setBackground(wallColor);
				wUp.validate();
			} else {
				wUp.setBackground(color);
				wUp.validate();
			}
			if (cell.getWall(GridModel.DOWN)) {
				wDown.setBackground(wallColor);
				wDown.validate();
			} else {
				wDown.setBackground(color);
				wDown.validate();
			}
			if (cell.getWall(GridModel.LEFT)) {
				wLeft.setBackground(wallColor);
				wLeft.validate();
			} else {
				wLeft.setBackground(color);
				wLeft.validate();
			}
			if (cell.getWall(GridModel.RIGHT)) {
				wRight.setBackground(wallColor);
				wRight.validate();
			} else {
				wRight.setBackground(color);
				wRight.validate();
			}
		} catch (Exception ex) {

		}
		this.innerPanel.setBackground(color);
		this.innerPanel.validate();
		this.up.setBackground(color);
		this.up.validate();
		this.down.setBackground(color);
		this.down.validate();
		this.left.setBackground(color);
		this.left.validate();
		this.right.setBackground(color);
		this.right.validate();

	}
	
	/**
	 * Set the Font for the reward-fields
	 * @param font new Font of the fields
	 */
	public void setRewardFont(Font font){
		this.up.setFont(font);
		this.down.setFont(font);
		this.left.setFont(font);
		this.right.setFont(font);
	}
	
}
