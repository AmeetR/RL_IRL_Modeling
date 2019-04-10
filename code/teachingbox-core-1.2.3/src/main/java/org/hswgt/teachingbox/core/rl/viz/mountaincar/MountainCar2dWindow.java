package org.hswgt.teachingbox.core.rl.viz.mountaincar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.hswgt.teachingbox.core.rl.agent.AgentObserver;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.MountainCarEnv;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * This class visualizes the Mountain-Car in a 2D window.
 * 
 * @author Michel Tokic
 * 
 */
public class MountainCar2dWindow extends JPanel implements AgentObserver
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8130726907586576113L;
	
	protected Color white = new Color(255, 255, 255);
	protected Color black = new Color(0, 0, 0);
	protected Color red = new Color (255, 0, 0);
	protected int width = 0;
	protected int height = 0;
	protected int rad = 100;
	protected double carx = 0.0;
	protected Action action = MountainCarEnv.FORWARD;
	int step = 0, lastSteps=0;
	
	/** distance from centered cart pole position to border */
	protected double halfRange = 1.0;

	// the midpoint of the window
	Point mid;

	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int NONE = 2;
	
	//protected int control = NONE;
	protected int control = LEFT;
	
	/** wait delayTime milliseconds after each action (used for slow-motion visualization) */
	protected int delayTime = 10;
	private JFrame frame;

	// draw mountains
	final double minPosX = -1.2;
	final double maxPosX = 0.6;
	final double minPosY = 0.7;
	final double maxPosY = -0.8;
	final int steps = 30;
	final double xDelta = (maxPosX - minPosX) / steps;
	
	int mountainX[] = new int[steps];
	int mountainY[] = new int[steps];
	

	
	private void init (String title) {
		
		width = (int) (halfRange * 200 * 2) + 200;
		height = 300;
		mid = new Point(width / 2, height / 2);
		
		frame = new JFrame(title);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setBackground(white);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		for (int i=0; i<steps; i++) {
			// convert to pixels
			mountainX[i] = xToPixel(	   minPosX + (i*xDelta));
			mountainY[i] = yToPixel(mcPosY(minPosX + (i*xDelta)));			
		}
	}

	public MountainCar2dWindow(String title) 
	{
			init (title);
	}

	public MountainCar2dWindow(String title, int delayTime) 
	{
			init (title);
			this.delayTime = delayTime;
	}
	
	// returns the y-position for a given x-value
	private double mcPosY (double position) {	
		return 0.5*Math.sin(3.0*position);
	}
	
	// returns the x position in pixel for a given car position
	private int xToPixel (double xPos) {
		
		double buf = (xPos-minPosX) * ((double)width / (maxPosX-minPosX));
		return (int)buf;
	}
	
	// returns the y position in pixel for a given car position
	private int yToPixel (double yPos) {	
		double buf = (yPos-minPosY) * ((double)height/ (maxPosY-minPosY));
		return (int)buf;	
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.clearRect(0, 0, width, height);
		g2d.setColor(black);

		// draw mountains
		g2d.drawPolyline (mountainX, mountainY, steps);

		// compute position of components
		double l=0.08;
		double r=0.03;
		double alpha = Math.atan(1.5 * Math.cos(3*carx));
		double deltay = l * Math.sin(alpha);
		double deltax = deltay / Math.tan(alpha);
		double xWheelA = carx - 0.5 * deltax - r * Math.sin(alpha);
		double yWheelA = 0.5 * Math.sin(3.0 * (carx - 0.5 * deltax)) + r*Math.cos(alpha);
		double xWheelB = xWheelA + deltax;
		double yWheelB = yWheelA + deltay;
		
		// draw wheels
		g2d.drawOval(xToPixel(xWheelA), yToPixel(yWheelA), 8, 8);
		g2d.drawOval(xToPixel(xWheelB), yToPixel(yWheelB), 8, 8);

		// draw body
		double centerx = (xWheelA + ((xWheelB-xWheelA) / 2.0)) - r * Math.sin(alpha);
		double centery = (yWheelA + ((yWheelB-yWheelA) / 2.0)) + r * Math.cos(alpha);
		g2d.fillOval(xToPixel(centerx), yToPixel(centery), 8, 8);
		
		// add text
		g2d.drawString ("step=" + step + ", lastRunSteps=" + lastSteps, 10, 10);
	}

    public void close() {
            frame.dispose();
	}

	// AgentObserver
    @Override
	public void update(State s, Action a, State sn, Action an, double r, boolean terminalState) {
		
		this.carx = s.get(0);
		repaint();
		
		try {
			Thread.sleep(this.delayTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		this.action = a;
		this.step++;
	}

	@Override
	public void updateNewEpisode(State initialState) {
		//System.out.println ("updateNewEpisode: lastSteps=" + lastSteps + ", step=" + step);
		this.lastSteps = step;
		this.step=0;
	}

	// main
	public static void main(String[] args) {
		MountainCar2dWindow window = new MountainCar2dWindow("mountaincar window test");
		window.updateUI();
	}	
}