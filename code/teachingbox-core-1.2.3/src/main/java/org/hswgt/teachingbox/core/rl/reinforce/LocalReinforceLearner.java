package org.hswgt.teachingbox.core.rl.reinforce;

import java.io.Serializable;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;

/**
 * This class adapts an arbitrary local parameter based
 * on the maximum Q-value in the current state (REINFORCE, Williams, 1992).
 *  
 * @author tokicm
 *
 */
public class LocalReinforceLearner extends BasicReinforceGaussianLearner implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1742182041028595792L;
	
	/**
	 * The Q-function
	 */
	private QFunction Q;
	private double envGamma = 0;


	/** 
	 * Constructor
	 * @param rl The ReinforceLearner 
	 * @param Q The Q-function
	 * @param initMean initial mean of exploration parameter (eg. epsilon, tau, sigma)
	 * @param minMean minimum boundary of mean 
	 * @param maxMean maximum boundary of mean
	 * @param initSD initial standard deviation
	 * @param minSD minimum standard deviation
	 * @param maxSD maximum standard deviation
	 * @param alpha learning rate for mue and sigma (REINFORCE)
	 * @param envGamma learning rate for reward baseline (REINFORCE)
	 */
	public LocalReinforceLearner(ReinforceLearner rl, QFunction Q,  
			double initMean, double minMean, double maxMean, 
			double initSD, 
			double minSD, double maxSD, 
			double alpha, double envGamma) {
		
		super(rl, initMean, minMean, maxMean, 
				initSD, minSD, maxSD, 
				alpha);
		
		this.envGamma = envGamma;
		this.Q = Q;
	}
	
	
	/**
	 * recompute the Q-value of Q(s,a) / update mean and variance   
	 */
	public void update(State s, Action a, State sn, Action an,
			double reward, boolean terminalState) {
		
		double rho = (envGamma*Q.getMaxValue(sn)) + reward;
		updateREINFORCE(rho, s);		
	}
	

	/**
	 * returns the parameter to be observed 
	 */
	public double getParameter(State state, Action action, String parameter) {

		if (parameter.equals(MEAN)) {
			
			return rl.getScaledMetaParameter(MEAN,  meanFunction.getValue(state, PARAMETER_ACTION));

		} else if (parameter.equals (VARIANCE)) {
			
			return rl.getScaledMetaParameter(VARIANCE,  sigmaFunction.getValue(state, PARAMETER_ACTION));
			
		} else if (parameter.equals (POLICY_PARAMETER)) {
			
			return rl.getScaledMetaParameter (POLICY_PARAMETER, parameterFunction.getValue(state, PARAMETER_ACTION));
			
		} else if (parameter.equals (REWARD_BASELINE)) {

			return rl.getScaledMetaParameter( REWARD_BASELINE, baselineFunction.getValue(state, PARAMETER_ACTION));
		}
		
		return 0;
	}

	@Override
	public void updateNewEpisode(State s) {
		// nothing to do
	}
}
