package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;

import java.awt.BorderLayout;
import java.io.Serializable;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridModel;

/**
*
* Panel with the speed slider to change the sleep time between two steps.
*/
public class SpeedPanel extends JToolBar implements ChangeListener, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1320603674309022741L;
	public static final int SPEED_MIN = 0;
    public static final int SPEED_MAX = 500;

    JSlider speed;
	/**
	 * Constructor
	 */
	public SpeedPanel(){
		this.setLayout(new BorderLayout());
		this.add(new JLabel("Speed:"), BorderLayout.NORTH);
		speed = new JSlider(JSlider.HORIZONTAL,
                SPEED_MIN, SPEED_MAX, SPEED_MAX - GridModel.getInstance().getSleepTime());
		speed.addChangeListener(this);
		//Create the label table
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put( new Integer( 0 ), new JLabel("Slow") );
		labelTable.put( new Integer( SPEED_MAX ), new JLabel("Fast") );
		speed.setLabelTable(labelTable);
		speed.setPaintLabels(true);
		this.add(speed, BorderLayout.CENTER);
	}
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
        	GridModel.getInstance().setSleepTime(SPEED_MAX - (int)source.getValue());
        }
	}
}
