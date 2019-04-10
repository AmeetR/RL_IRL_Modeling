
package org.hswgt.teachingbox.core.rl.viz.pole;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.CartPoleEnvironment;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver;


public class CartPole2dWindow extends JPanel implements KeyListener, ExperimentObserver
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
	protected double theta = 0.0;
	protected double cartx = 0.0;
	protected Action action = CartPoleEnvironment.NONE;
	
	/** distance from centered cart pole position to border */
	protected double halfRange = 1.0;

	// the midpoint of the window
	Point mid;

	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int NONE = 2;
	//protected int control = NONE;
	protected int control = LEFT;
	
	/** wait msDelay after each action (used for slow-motion visualization) */
	private int msDelay = 0;
	private JFrame frame;
        
	private void init (String title) {
		
		width = (int) (halfRange * 200 * 2) + 200;
		height = 300;
		mid = new Point(width / 2, height / 2);
		
		frame = new JFrame(title);
		frame.add(this);
		frame.addKeyListener(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setBackground(white);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
	}

	public CartPole2dWindow(String title) 
	{
			init (title);
	}

	public CartPole2dWindow(String title, int msDelay) 
	{
			init (title);
			this.msDelay = msDelay;
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

		// pole
		Point poleEnd = new Point((mid.x + (int) cartx + new Double(rad
				* Math.sin(theta)).intValue()), mid.y
				- new Double(rad * Math.cos(theta)).intValue());
		g2d.drawLine(mid.x + (int) cartx, mid.y, poleEnd.x, poleEnd.y);
		g2d.drawOval(mid.x + (int) cartx - 5, mid.y - 5, 10, 10);
		// g2d.drawOval(poleEnd.x - 3, poleEnd.y - 3, 6, 6);

		// ugly hack due to java2d filloval bug
		g2d.drawOval(poleEnd.x - 3, poleEnd.y - 3, 6, 6);
		g2d.fillOval(poleEnd.x - 3, poleEnd.y - 3, 5, 5);
		g2d.fillOval(poleEnd.x - 2, poleEnd.y - 2, 5, 5);
		
		// cart
		g2d.drawRect(mid.x + (int) cartx - 20, mid.y - 10, 40, 20);
		if (action.equals(CartPoleEnvironment.LEFT)) {
			g2d.setColor(red);
			g2d.fillOval(mid.x + (int) cartx - 18, mid.y + 10, 6, 6);
			g2d.setColor(black);
		} else {
			g2d.fillOval(mid.x + (int) cartx - 18, mid.y + 10, 6, 6);			
		}
		
		g2d.fillOval(poleEnd.x - 3, poleEnd.y - 3, 6, 6);
		
		if (action.equals(CartPoleEnvironment.RIGHT)) {
			g2d.setColor(red);
			g2d.fillOval(mid.x + (int) cartx + 12, mid.y + 10, 6, 6);
			g2d.setColor(black);
		} else {
			g2d.fillOval(mid.x + (int) cartx + 12, mid.y + 10, 6, 6);
						
		}
		
		//g2d.drawOval(poleEnd.x - 3, poleEnd.y - 3, 6, 6);
		// platform
		// lower floor
		int xl = mid.x - (int) (halfRange * 200) - 20;
		int xr = mid.x + (int) (halfRange * 200) + 20;
		g2d.drawLine(xl, mid.y + 16, xr, mid.y + 16);
		// borders
		g2d.drawLine(xl, mid.y + 16, xl, mid.y - 10);
		g2d.drawLine(xr, mid.y + 16, xr, mid.y - 10);
		// upper floor
		g2d.drawLine(xl, mid.y - 10, 0, mid.y - 10);
		g2d.drawLine(xr, mid.y - 10, width, mid.y - 10);
	}

	/**
	 * Set the state (we need only theta and cartx for drawing).
	 * @param state The state of the CartPoleEnvironment.
	 */
	public void setState(State state) {
		this.theta = state.get(0);
		this.cartx = (state.get(2) * 200)/2.4; // 1px => 1cm
		//this.cartx = (state.get(2) * 200); // 1px => 1cm
		repaint();
		
		
		try {
			Thread.sleep(this.msDelay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Get the control id, which can be changed with the arrow-keys ()
	 * 
	 * @return The control (key) id
	 */
	public int getControl() {
		return control;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (keyCode == KeyEvent.VK_LEFT) {
			control = LEFT;
			action = CartPoleEnvironment.LEFT;
		}
		if (keyCode == KeyEvent.VK_RIGHT) {
			control = RIGHT;
			action = CartPoleEnvironment.RIGHT;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		control = NONE;
		action = CartPoleEnvironment.NONE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent arg0) {
	}

        public void close() {
            frame.dispose();
	}

	// EXPERIMENT OBSERVER METHODS
	
	/* (non-Javadoc)
	 * @see org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver#update(org.hswgt.teachingbox.core.rl.env.State, org.hswgt.teachingbox.core.rl.env.Action, org.hswgt.teachingbox.core.rl.env.State, org.hswgt.teachingbox.core.rl.env.Action, double, boolean)
	 */
	public void update(State s, Action a, State sn, Action an, double r, boolean terminalState) {
		setState(s);
		this.action = a;
	}

	/* (non-Javadoc)
	 * @see org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver#updateExperimentStart()
	 */
	public void updateExperimentStart() {
	}

	/* (non-Javadoc)
	 * @see org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver#updateExperimentStop()
	 */
	public void updateExperimentStop() {
            this.close();
	}
	/* (non-Javadoc)
	 * @see org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver#updateNewEpisode(org.hswgt.teachingbox.core.rl.env.State)
	 */
	public void updateNewEpisode(State initialState) {
	}

	// main
	public static void main(String[] args) {
		new CartPole2dWindow("cart pole window test");
	}	
}