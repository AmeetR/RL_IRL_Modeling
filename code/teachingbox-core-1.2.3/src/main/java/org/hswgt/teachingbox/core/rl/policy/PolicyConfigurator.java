package org.hswgt.teachingbox.core.rl.policy;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JLabel;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;

/**
 * A human trainer policy for 2D gridworlds
 * 
 * @author tokicm
 */
public class PolicyConfigurator extends GreedyPolicy {

	// Logger
	private final static Logger log4j = Logger.getLogger("PolicyConfigurator");

	/**
	 * The actions
	 */
	public static final Action LEFT = new Action(new double[] { -1, 0 });
	public static final Action RIGHT = new Action(new double[] { +1, 0 });
	public static final Action UP = new Action(new double[] { 0, +1 });
	public static final Action DOWN = new Action(new double[] { 0, -1 });
	public static final Action INVALID = new Action(new double[] { -100, -100 });

	private GridworldActionGUI actionGUI = new GridworldActionGUI();

	EpsilonGreedyPolicy ePolicy;
	SoftmaxActionSelection sPolicy;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6055779820293005149L;

	/**
	 * The Constructor
	 * 
	 * @param Q The Q-Function
	 * @param as The Action-Set
	 */
	public PolicyConfigurator(QFunction Q, ActionSet as) {
		super(Q, as);
		this.actionGUI.disableButtons();
		this.actionGUI.setVisible(true);

		this.ePolicy = new EpsilonGreedyPolicy(Q, as, 0.0);
		this.sPolicy = new SoftmaxActionSelection(Q, as, 1.0);
	}

	/**
	 * returns an action given by the human trainer
	 * @param s The state
	 */
	public Action getAction(State s) {

		Action returnAction = INVALID;
		boolean actionFound = false;
		boolean actionSelected = false;
		String robotAction = "";

		log4j.debug("waiting for action input ...");
		this.actionGUI.setVisible(true);
		this.actionGUI.enableButtons();

		while (!actionFound) {

			// log4j.debug("Current policy: " + this.actionGUI.robotPolicy);

			this.actionGUI.enableButtons();
			this.actionGUI.setVisible(true);

			if (this.actionGUI.robotPolicy.equals("human")) {

				robotAction = actionGUI.getSelectedAction();

				if (robotAction.equals("UP")) {
					returnAction = UP;
					log4j.debug("UP");
					actionSelected = true;
				} else if (robotAction.equals("DOWN")) {
					returnAction = DOWN;
					log4j.debug("DOWN");
					actionSelected = true;
				} else if (robotAction.equals("LEFT")) {
					returnAction = LEFT;
					log4j.debug("LEFT");
					actionSelected = true;
				} else if (robotAction.equals("RIGHT")) {
					returnAction = RIGHT;
					log4j.debug("RIGHT");
					actionSelected = true;
				} else if (robotAction.equals("GREEDY")) {
					returnAction = this.getBestAction(s);
					log4j.debug("GREEDY");
					actionSelected = true;
				} else {

					actionSelected = false;
				}

			} else if (this.actionGUI.robotPolicy.equals("egreedy")) {

				log4j.debug("selecting eGreedy action");
				ePolicy.setEpsilon(actionGUI.getExplorationParameter());
				returnAction = ePolicy.getAction(s);
				actionSelected = true;

			} else if (this.actionGUI.robotPolicy.equals("softmax")) {

				log4j.debug("selecting softmax action");
				sPolicy.setTemperature(actionGUI.getExplorationParameter());
				returnAction = sPolicy.getAction(s);
				actionSelected = true;
			}

			/*
			 * System.out.println("Action (w=UP | s=DOWN | a=LEFT | d=RIGHT | g=GREEDY): "
			 * ); try { System.in.read(inputChar); } catch (IOException e) {
			 * e.printStackTrace(); }
			 * 
			 * if (inputChar[0] == 'a') { returnAction = LEFT;
			 * log4j.debug("LEFT"); } else if (inputChar[0] == 'd') {
			 * returnAction = RIGHT; log4j.debug("RIGHT"); } else if
			 * (inputChar[0] == 'w') { returnAction = UP; log4j.debug("UP"); }
			 * else if (inputChar[0] == 's') { returnAction = DOWN;
			 * log4j.debug("DOWN"); } else if (inputChar[0] == 'g') {
			 * returnAction = this.getBestAction(s); log4j.debug("GREEDY"); }
			 */

			final ActionSet valid = actionSet.getValidActions(s);

			for (int a = 0; a < valid.size(); a++) {
				if (valid.get(a).equals(returnAction)) {
					actionFound = true;
					this.actionGUI.disableButtons();
					log4j.debug("valid action found");
					return returnAction.copy();
				}
			}

			if (actionSelected && !actionFound) {

				System.out
						.println("invalid action! (action is not in ActionSet)");
			}

			// System.out.println ("ERROR: Invalid action");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.getBestAction(s);
	}

	/**
	 * Helper Class for the GUI-Button and Keyboard ActionListener
	 * 
	 * @author tokicm
	 * 
	 */
	public class GridworldActionGUI extends JFrame implements KeyListener {

		private static final long serialVersionUID = 6694245245955109628L;

		JButton up;
		JButton down;
		JButton left;
		JButton right;
		JButton greedy;

		JRadioButton egreedyPolicy;
		JRadioButton softmaxPolicy;
		JRadioButton humanPolicy;
		JLabel paramLabel = new JLabel("");

		JTextField expParameterTextField;
		String robotAction = "";
		String robotPolicy = "human";

		/**
		 * The constructor
		 */
		public GridworldActionGUI() {

			Container contentPane = getContentPane();
			contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

			this.addKeyListener(this);
			contentPane.addKeyListener(this);
			
			this.setTitle("Policy configurator");

			up = new JButton("UP");
			down = new JButton("DOWN");
			left = new JButton("LEFT");
			right = new JButton("RIGHT");
			greedy = new JButton("GREEDY");

			egreedyPolicy = new JRadioButton("E-Greedy");
			egreedyPolicy.setSelected(false);
			softmaxPolicy = new JRadioButton("Softmax");
			softmaxPolicy.setSelected(false);
			humanPolicy = new JRadioButton("Human Trainer");
			humanPolicy.setSelected(true);

			expParameterTextField = new JTextField(8);
			expParameterTextField.setText("0.1");
			expParameterTextField.setEnabled(false);

			this.enableButtons();

			contentPane.add(up);
			contentPane.add(down);
			contentPane.add(left);
			contentPane.add(right);
			contentPane.add(greedy);
			contentPane.add(humanPolicy);
			contentPane.add(new JLabel(" - "));
			contentPane.add(egreedyPolicy);
			contentPane.add(softmaxPolicy);
			contentPane.add(paramLabel);
			contentPane.add(expParameterTextField);

			// Focus only to JFrame
			contentPane.setFocusable(false);
			up.setFocusable(false);
			down.setFocusable(false);
			left.setFocusable(false);
			right.setFocusable(false);
			greedy.setFocusable(false);
			egreedyPolicy.setFocusable(false);
			softmaxPolicy.setFocusable(false);
			humanPolicy.setFocusable(false);

			this.setFocusable(true);

			/*
			 * up.addKeyListener(this); down.addKeyListener(this);
			 * left.addKeyListener(this); right.addKeyListener(this);
			 */

			this.pack();

			up.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setAction("UP");
				}
			});
			down.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setAction("DOWN");
				}
			});
			left.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setAction("LEFT");
				}
			});
			right.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setAction("RIGHT");
				}
			});
			greedy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setAction("GREEDY");
				}
			});

			egreedyPolicy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					softmaxPolicy.setSelected(false);
					humanPolicy.setSelected(false);

					up.setEnabled(false);
					down.setEnabled(false);
					left.setEnabled(false);
					right.setEnabled(false);
					greedy.setEnabled(false);

					paramLabel.setText(" Epsilon: ");
					paramLabel.setEnabled(true);
					expParameterTextField.setEnabled(true);

					setPolicy("egreedy");
				}
			});

			softmaxPolicy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					egreedyPolicy.setSelected(false);
					humanPolicy.setSelected(false);

					up.setEnabled(false);
					down.setEnabled(false);
					left.setEnabled(false);
					right.setEnabled(false);
					greedy.setEnabled(false);

					paramLabel.setText(" Temperature: ");
					paramLabel.setEnabled(true);
					expParameterTextField.setEnabled(true);

					setPolicy("softmax");
				}
			});
			humanPolicy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					softmaxPolicy.setSelected(false);
					egreedyPolicy.setSelected(false);

					up.setEnabled(true);
					down.setEnabled(true);
					left.setEnabled(true);
					right.setEnabled(true);
					greedy.setEnabled(true);

					paramLabel.setEnabled(false);
					expParameterTextField.setEnabled(false);

					setPolicy("human");
				}
			});

			this.disableButtons();
		}

		/**
		 * sets the robot action
		 * 
		 * @param action The action
		 */
		private void setAction(String action) {
			this.robotAction = action;
			System.out.println("Action " + action + " fired!");
		}

		private void setPolicy(String policy) {
			this.robotPolicy = policy;
			System.out.println("Policy " + policy + " fired!");
			this.pack();
		}

		/**
		 * enables the input buttons
		 */
		public void enableButtons() {

			if (this.robotAction.equals("human")) {

				this.up.setEnabled(true);
				this.down.setEnabled(true);
				this.left.setEnabled(true);
				this.right.setEnabled(true);
				this.greedy.setEnabled(true);
			}
		}

		/**
		 * disables the input buttons
		 */
		public void disableButtons() {
			this.up.setEnabled(false);
			this.down.setEnabled(false);
			this.left.setEnabled(false);
			this.right.setEnabled(false);
			this.greedy.setEnabled(false);
		}

		/**
		 * returns the selected action
		 * 
		 * @return The selected action
		 */
		public String getSelectedAction() {

			String tmpAction = this.robotAction;

			if (!tmpAction.equals("")) {
				this.robotAction = "";
			}
			return tmpAction;
		}

		/**
		 * returns the exploration parameter
		 * 
		 * @return The exploration parameter
		 */
		public double getExplorationParameter() {

			double parameter = 0.0;

			try {
				parameter = Double.parseDouble(this.expParameterTextField
						.getText());
			} catch (NumberFormatException e) {
				System.out.println("PARAMETER ERROR: setting param=0.0");
				parameter = 0.0;
			}
			return parameter;
		}

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		public void keyPressed(KeyEvent kEvent) {

			// System.out.println ("Key pressed: " + kEvent.getKeyChar() + " ("
			// + kEvent.getKeyCode() + ")");
			System.out.println("Key pressed: " + kEvent.getKeyChar());

			if (kEvent.getKeyCode() == KeyEvent.VK_UP) {
				this.robotAction = "UP";
			} else if (kEvent.getKeyCode() == KeyEvent.VK_DOWN) {
				this.robotAction = "DOWN";
			} else if (kEvent.getKeyCode() == KeyEvent.VK_LEFT) {
				this.robotAction = "LEFT";
			} else if (kEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
				this.robotAction = "RIGHT";
			} else if (kEvent.getKeyChar() == 'g') {
				this.robotAction = "GREEDY";
			} else if (kEvent.getKeyChar() == 'e') {
				System.exit(0);
			}

		}

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
		}

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
		 */
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
		}
	}
}