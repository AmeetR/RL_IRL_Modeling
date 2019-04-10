package org.hswgt.teachingbox.core.rl.policy;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.learner.ErrorObserver;
import org.hswgt.teachingbox.core.rl.learner.stepsize.StepSizeCalculator;
import org.hswgt.teachingbox.core.rl.tabular.HashQFunction;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;

/**
 * Implementation of the VDBE-Policy from:
 *  
 * M. Tokic. Adaptive \varepsilon-greedy exploration in reinforcement learning based on 
 * value differences. In R. Dillmann, J. Beyerer, U. Hanebeck, and T. Schultz, 
 * editors, KI 2010: Advances in Artificial Intelligence, volume 6359 of 
 * Lecture Notes in Artificial Intelligence, pages 203-210. Springer Berlin / 
 * Heidelberg, 2010.
 * 
 * @author tokicm
 *
 */
public class TabularVDBEPolicy extends EpsilonGreedyPolicy implements ErrorObserver {

	/**
	 * the serial version id
	 */
	private static final long serialVersionUID = -4876716931413579643L;
	
	/**
	 * VDBE Activation-Type 
	 */
	public static final int VDBE_ACTIVATION_BOLTZMANN = 1;  /* from the paper */
	public static final int VDBE_ACTIVATION_EXPONENTIAL = 2; 
	private int vdbeType = VDBE_ACTIVATION_BOLTZMANN;

	
	protected Logger log4j = Logger.getLogger(this.getClass().getSimpleName());

	/**
	 * The inverse sensitivity (default value 1.0)
	 */
	protected double sigma= 1.0;

	/** The Q-function object (used for greedy exploration) */
	protected QFunction Q = null;

	/** The action set */
	final ActionSet as;
	
	/** The Hash-Function for memorizing epsilon(s) */
	protected HashQFunction epsilon;

	/** The action-index for the epsilon(s) function */
	protected Action epsilonAction = new Action(new double[] { 0 });
	
	/**
	 * the constructor for tabular approximation of epsilon
	 * 
	 * @param Q The Q function
	 * @param as The @ActionSet
	 * @param sigma The sigma parameter
	 */
	public TabularVDBEPolicy(QFunction Q, ActionSet as, double sigma) {
		// super(Q, as, 0);

		// call constructor from GreedyPolicy
		super(Q, as);
		
		this.Q = Q;
		this.as = as;
		this.sigma = sigma;
		
		epsilon = new HashQFunction(1);		

		// default function type is VDBEv1 unless it is set explicitly
		this.vdbeType = VDBE_ACTIVATION_BOLTZMANN;
	}


	/**
	 * sets the VDBE function type
	 * 
	 * @param vdbeType the function type
	 */
	public void setVdbeType(int vdbeType) {
		this.vdbeType = vdbeType;
	}

	/**
	 * @return the sigma
	 */
	public double getSigma() {
		return sigma;
	}

	/**
	 * @param sigma the sigma to set
	 */
	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	/**
	 * returns the VDBE function type
	 * 
	 * @return the function type
	 */
	public int getVdbeType() {
		return this.vdbeType;
	}

	/**
	 * this method computes the new exploratory probability, based td-error and alpha
	 * @param tderror The td error
	 * @param reward The reward
	 * @param alpha The learning rate
	 * @param gamma The discounting parameter
	 * @param s The @State
	 * @param a The @Action
	 * @param isTerminalState indication if s is terminal state
	 */
	public void learnerUpdate(double tderror, double reward, StepSizeCalculator alpha,
			double gamma, State s, Action a, boolean isTerminalState) {
		
		double valueDifference = tderror * alpha.getAlpha(s, a);
		double activation = 0;
		double eValue = Math.exp(-1 * Math.abs(valueDifference)/ this.sigma);
		double oldEpsilon = this.epsilon.getValue(s,epsilonAction);
		
		//System.out.println ("alpha=" + alpha.getAlpha(s,a) + ", tderror=" + tderror + ", vd=" + valueDifference);
		

		if (this.vdbeType == VDBE_ACTIVATION_EXPONENTIAL) {

			activation  = (1.0 - eValue);

		// return VDBE_TYPE_BOLTZMANN Function type by default
		} else {

			activation  = (1.0 - eValue) / (1.0 + eValue);
		}
		
		// learning rate delta = 1 / amountOfPossibleActions(s)
		double delta = 1.0 / actionSet.getValidActions(s).size();
		
		// memorize new epsilon
		double newEpsilon = (delta * activation) +  ((1-delta)*oldEpsilon);
		this.epsilon.setValue(s, epsilonAction, newEpsilon);
		
		//System.out.println ("DEBUG: state=" + s + ", tderror=" + tderror + ", reward=" + reward + ", alpha=" + alpha.getAlpha(s, a) + ", gamma=" + gamma +
		//					", oldEpsilon=" + oldEpsilon + ", newEpsilon=" + newEpsilon + ", delta=" + delta);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hswgt.teachingbox.core.rl.experiment.ParameterObserver#getParameter
	 * (org.hswgt.teachingbox.core.rl.env.State,
	 * org.hswgt.teachingbox.core.rl.env.Action, java.lang.String)
	 */
	public double getParameter(State s, Action a, String parameter) {

		// the current exploration rate
		if (parameter.equals("epsilon") || parameter.equals("expParameter")) {
			return this.epsilon.getValue(s, epsilonAction);

		// the current sigma
		} else if (parameter.equals("temperature") || parameter.equals("sigma")) {
			return this.sigma;

		// the current alpha
		} else if (parameter.equals("alpha")) {
			return this.epsilon.getValue(s, epsilonAction);

		// zero otherwise
		} else {
			return 0;
		}
	}
	
	/**
	 * Returns an action for a given state
	 * With probability of epsilon a random action, and with probability (1-epsilon)
	 * the estimated optimal action
	 * @param s The @State in which an action should be chosen
	 * @return The @Action to take in @State s
	 */
	public Action getAction(final State s)
	{
		return getAction(s, epsilon.getValue(s,epsilonAction));
	}
}
