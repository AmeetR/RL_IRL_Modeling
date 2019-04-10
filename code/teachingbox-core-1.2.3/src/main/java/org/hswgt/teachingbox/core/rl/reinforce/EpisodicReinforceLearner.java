package org.hswgt.teachingbox.core.rl.reinforce;

import java.io.Serializable;
import java.util.HashMap;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * This class adapts an arbitrary parameter based
 * on the return per episode (REINFORCE, Williams, 1992).
 * 
 * @author tokicm
 */
public class EpisodicReinforceLearner extends BasicReinforceGaussianLearner implements Serializable {

	private static final long serialVersionUID = 1634877932272261065L;
	
	protected HashMap<String,Boolean> parameterReturned= new HashMap<String,Boolean>();

	protected double R = 0;
	protected State initState;
	protected boolean firstRun = true;

	/** 

	 * @param initMean initial mean of exploration parameter (eg. epsilon, tau, sigma)
	 * @param minMean minimum boundary of mean
	 * @param maxMean maximum boundary of mean
	 * @param initVariance initial variance
	 * @param alpha learning rate for mue and sigma (REINFORCE)
	 */
	/**
	 * Constructor
	 * @param rlearner The ReinforceLearner 
	 * @param initMean initial mean of exploration parameter (eg. epsilon, tau, sigma)
	 * @param minMean minimum boundary of mean 
	 * @param maxMean maximum boundary of mean
	 * @param initSD initial standard deviation
	 * @param minSD minimum standard deviation
	 * @param maxSD maximum standard deviation
	 * @param alpha learning rate for mue and sigma (REINFORCE)
	 */
	public EpisodicReinforceLearner(ReinforceLearner rlearner, 
			double initMean, double minMean, double maxMean, 
			double initSD, 
			double minSD, double maxSD, 
			double alpha) {
		
		super(rlearner, initMean, minMean, maxMean, initSD, minSD, maxSD, alpha);
	}

	/**
	 * returns the parameter to be observed 
	 */
	public double getParameter(State s, Action a, String parameter) {

		// return each parameter only once per episode (assumes plotting cumulative data per episode) 
		if (parameterReturned.get(parameter) == null) {
			
			parameterReturned.put(parameter,  true);

			if (parameter.equals(MEAN)) {
				
				return rl.getScaledMetaParameter(MEAN,  meanFunction.getValue(initState, PARAMETER_ACTION));

			} else if (parameter.equals (VARIANCE)) {
				
				return rl.getScaledMetaParameter(VARIANCE,  sigmaFunction.getValue(initState, PARAMETER_ACTION));
				
			} else if (parameter.equals (POLICY_PARAMETER)) {
				
				return rl.getScaledMetaParameter (POLICY_PARAMETER, parameterFunction.getValue(initState, PARAMETER_ACTION));
				
			} else if (parameter.equals (REWARD_BASELINE)) {

				return rl.getScaledMetaParameter( REWARD_BASELINE, baselineFunction.getValue(initState, PARAMETER_ACTION));
			}

		} 
		
		return 0;
	}
	
	/**
	 * update the return per episode
	 */
	public void update(State s, Action a, State sn, Action an,
			double reward, boolean terminalState) {
		
		// accumulate reward
		R += reward;
	}

	/**
	 * reset return at the beginning of each episode
	 */
	public void updateNewEpisode(State s) {
		
		parameterReturned.clear();	

		// recompute mean and variance at the end of an episode
		if (!firstRun) {
			
			// recompute mean and variance based on the init state of the last episode
			this.updateREINFORCE(R, initState);
			
			// set new parameter (e.g. epislon of an eGreedy-policy) - NEEDS TO BE IMPLEMENTED IN ReinforceLearner-CLASSES
			rl.setParameter(parameterFunction.getValue(initState, PARAMETER_ACTION));

		} else {
			firstRun = false;
		}
		
		// reset return
		this.R = 0;
		
		// memorize initial state
		this.initState = s;
	}


	/**
	 * @return the cumReward
	 */
	public double getCumReward() {
		return R;
	}
}
