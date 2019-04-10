
package org.hswgt.teachingbox.core.rl.viz.pole;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.PoleSwingupEnvironment;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver;

/**
 * @author Richard Cubek
 *
 */
public class PoleSwingUp2dWindow extends JPanel implements KeyListener, ExperimentObserver
{
	private static final long serialVersionUID = -1046830314435091826L;
	// drawing
	private Color white	= new Color(255, 255, 255);
	private Color black	= new Color(0, 0, 0);
	private int width	= 400;
	private int height	= 400;
	private int rad		= 120;
	private double angle= 0.0;
	// for reading out the action from user arrow-key input if wanted
	static final int NONE 	= 0;
	static final int RIGHT	= 1;
	static final int LEFT	= 2;
	private int control		= NONE;
	protected boolean init	= false;
	protected String title	= "";
	private int nSteps = 0;
	private State currentState = null;
	private double currentReward = 0;
	
	DecimalFormat d = new DecimalFormat("##.##");

	
	JFrame frame;
	long lastTime = 0;
	int paint = 0;
	int delayTime = 10; // delay time to pause window after an action

	/** we define an own ActionSet similar to the original, but with Force = 0 */
	public static final ActionSet ACTION_SET = new ActionSet();
	
	static
	{
	    ACTION_SET.add(new Action(new double[]{0.0}));
	    ACTION_SET.add(new Action(new double[]{-5.0}));
	    ACTION_SET.add(new Action(new double[]{+5.0}));
	}
	
	/**
	 * Constructor.
	 * @param title Title of the window.
	 * @param delayTime The delay time after an action in [ms]
	 */
	public PoleSwingUp2dWindow(String title, int delayTime)
	{
		this.title = title;
		this.delayTime = delayTime;
	}

	/**
	 * Initialize window.
	 */
	public void init()
	{
		frame = new JFrame(title);
		frame.addKeyListener(this);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setBackground(white);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);		
		this.init = true;
	}

	/**
	 * Overrides the classic java2d repaint method.
	 * @param g The graphics object.
	 */
	public void paint(Graphics g) 
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.clearRect(0, 0, width, height);
		g2d.setColor(black);
		Point point = new Point((width / 2 + new Double(rad * Math.sin(angle)).intValue()),
								height / 2 - new Double(rad * Math.cos(angle)).intValue()); 
		g2d.drawLine(width / 2, height / 2, point.x, point.y);
		
		// ugly hack due to java2d filloval bug
		g2d.drawOval(point.x - 3, point.y - 3, 6, 6);
		g2d.fillOval(point.x - 3, point.y - 3, 5, 5);
		g2d.fillOval(point.x - 2, point.y - 2, 5, 5);
		g2d.drawOval(width / 2 - 5, height / 2 - 5, 10, 10);
		
		//Visualize Policy
		if (this.control == LEFT) {
			g2d.drawString("Left", 50, 350);
		} else if (this.control == RIGHT) {
			g2d.drawString("Right", 330, 350);
		} else {
			g2d.drawString("None", 180, 350);			
		}
		
		// draw action
		g2d.drawString("t=" + this.nSteps, 195, 300);
		
		// draw state
		if (this.currentState != null) {
			g2d.drawString(	"position= " + d.format(this.currentState.get(0)), 10, 10);
			g2d.drawString(	" velocity= " + d.format(this.currentState.get(1)), 10, 25);
			g2d.drawString( "  reward= " + d.format(this.currentReward), 10, 40);
		}
	}
	
	/**
	 * Set the pole angle. 
	 * @param angle The pole angle.
	 */
	public void setAngle(double angle)
	{
		this.angle = angle;

		// calcTime = Calendar.getInstance().getTimeInMillis() - lastTime;
		//long sleepTime = new Double(PoleSwingupEnvironment.dt * 1000.0).longValue() - calcTime;
		
		try {
			Thread.currentThread();
			Thread.sleep(delayTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		lastTime = Calendar.getInstance().getTimeInMillis();
		
		if (!init)
			init();
		
		repaint();
	}
	
	/**
	 * Get the key-error control from user input. See constants.
	 * @return The control ID.
	 */
	public int getControl()
	{		
		return this.control;
	}
	
	/**
	 * Get the Action directly. 
	 * @return The action.
	 */
	public Action getAction()
	{		
		return ACTION_SET.get(this.control);
	}


	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		if (keyCode == KeyEvent.VK_LEFT)
		{
			control = LEFT;
		}
		if (keyCode == KeyEvent.VK_RIGHT)
		{
			control = RIGHT;
		}
	}

	public void keyReleased(KeyEvent e) {	
		control = NONE;
	}


	public void keyTyped(KeyEvent e) {
	}

	public void close() {
            frame.dispose();
	}
	
    // EXPERIMENT OBSERVER METHODS
	public void update(State s, Action a, State sn, Action an, double r, boolean terminalState) {
		if (a.equals (PoleSwingupEnvironment.RIGHT))
			this.control = RIGHT;
		else if (a.equals (PoleSwingupEnvironment.LEFT))
			this.control = LEFT;
		else 
			this.control = NONE;

		this.nSteps++;
		setAngle(s.get(0));
		this.currentState = s;
		this.currentReward = r;
	}
	
	public void updateExperimentStart() {
		//frame.setVisible(true);
	}
	public void updateExperimentStop() {
            frame.setVisible(false);
            this.close();
	}

	public void updateNewEpisode(State initialState) {
		this.nSteps = 0;
	}
	
	/**
	 * Test the window.
	 * @param args The command-line arguments
	 * @throws Exception The Exception
	 */
	public static void main (String[] args) throws Exception
	{
		// learned model of Environment
		PoleSwingupEnvironment env = new PoleSwingupEnvironment();
		env.init(new State(new double[]{Math.PI, 0}));
		// visualization/control
		PoleSwingUp2dWindow window = new PoleSwingUp2dWindow("pole-swing-up", 10);

		// run view iterations with the learned model environment
		
		State state = env.getState();
		Action action;
		
		// end the loop by closing the window
		while (true)
		{
			// update window
			window.setAngle(state.get(0));

			// store state, action in t (for t+1)
			action = window.getAction();			
			env.doAction(action); // time step
			state = env.getState();
		}
	}
}
