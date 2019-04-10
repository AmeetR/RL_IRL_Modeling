
package org.hswgt.teachingbox.core.rl.dp;

import java.util.List;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.datastructures.RewardFunction;
import org.hswgt.teachingbox.core.rl.datastructures.StateSet;
import org.hswgt.teachingbox.core.rl.datastructures.TransitionFunction;
import org.hswgt.teachingbox.core.rl.datastructures.TransitionProbability;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.tabular.TabularValueFunction;

/**
 * @author Richard Cubek
 * This is an implementation of the classic Value Iteration algorithm, how it is introduced in
 * the famous RL-book of Sutton and Barto (http://www.cs.ualberta.ca/%7Esutton/book/the-book.html).
 */
public class TabularValueIteration
{
	/** The discount-rate (default is 0.95) */
	protected double gamma = 0.9;
	/** The value function to learn */
	protected TabularValueFunction V;
	/** The maximal allowed difference of V_t(s) and V_t+1(s) within two consecutive sweeps (convergence measure) */
	protected double theta = 0.1;
	/** Maximum number of sweeps through the state set, if theta is not reached (no limit if set to 0) */
	protected int maxSweeps = 0;
	/** Logger */
	private static final Logger log4j = Logger.getLogger("TabularValueIteration");
    
	/* IN VALUE ITERATION, WE NEED A PERFECT MODEL OF THE WORLD (States, Actions, Reward- and Transition-function) */
	
	/** The StateSet of the Environment */
	protected StateSet stateSet;
	/** The ActionSet of the Environment */
	protected ActionSet actionSet;
	/** In Value Iteration, we need to know the state transition function of the model */
	protected TransitionFunction tf;
	/** In Value Iteration, we need to know the reward function of the model */
	protected RewardFunction rf;	

	/**
	 * Constructor
	 * @param V The tabular value function to learn
	 * @param stateSet The stateSet of the Environment
	 * @param actionSet An ActionSet with all possible actions
	 * @param tf The TransitionFunction
	 * @param rf The RewardFunction
	 */
	public TabularValueIteration(TabularValueFunction V, StateSet stateSet,
                ActionSet actionSet, TransitionFunction tf, RewardFunction rf)
	{
		this.stateSet = stateSet;
		this.actionSet = actionSet;
		this.tf = tf;
		this.rf = rf;
		this.V = V;
	}

	/**
	 * Run Value Iteration.
	 * @return number of iterations if convergence was reached (biggest value difference smaller theta), -1 if maxSweeps was reached
	 */
	public int run()
	{
		return this.run(this.theta, this.maxSweeps);
	}
	
	/**
	 * Run Value Iteration.
	 * @param theta The maximal allowed difference of V_t(s) and V_t+1(s) within two consecutive sweeps (convergence measure)
	 * @param maxSweeps Maximum number of sweeps through the state set, if theta is not reached (no limit if set to 0)
	 * @return number of iterations if convergence was reached (biggest value difference smaller theta), -1 if maxSweeps was reached
	 */
	public int run(double theta, int maxSweeps)
	{
		this.theta = theta;
		this.maxSweeps = maxSweeps;
		
		double maxValueDiff = Double.POSITIVE_INFINITY;
		int sweepCount = 0;
		
		while(maxValueDiff > this.theta)
		{
			maxValueDiff = Double.NEGATIVE_INFINITY; // will be updated below

			for (int i = 0; i < stateSet.size(); i++)
			{
				ActionSet validActions = actionSet.getValidActions(stateSet.get(i));
				
				double maxQ = Double.NEGATIVE_INFINITY;
				double maxV = Double.NEGATIVE_INFINITY;
		        
				for (Action a : validActions)
				{
					// sum over possible successor states (weighted by probability)
					double sum = 0;
					// get successor states and the transition probabilities leading to this states
					List<TransitionProbability> tpList= tf.getTransitionProbabilities(stateSet.get(i), a);
					
					for (TransitionProbability tp : tpList)
					{
					    log4j.debug("p("+tp.getState()+","+tp.getAction()+","+tp.getNextState()+") = "+tp.getProbability());
					}
					
					// iterate over all possible successor states and sum up their values weighted by probability
					for (TransitionProbability tp : tpList)
					{
						State sn = tp.getNextState();
						double r = rf.getReward(stateSet.get(i), a, sn);
						double Vn = V.getValue(sn);
						
						if (Vn > maxV)
						{
							maxV = Vn;
						}
						
						sum = sum + tp.getProbability() * (r + gamma * Vn);
					}
					
					maxQ = Math.max(maxQ, sum);
				}
				
				maxValueDiff = Math.max(maxValueDiff, Math.abs(maxQ - V.getValue(stateSet.get(i))));
				
				V.setValue(stateSet.get(i), maxQ);
			}

			sweepCount++;
			
			if (sweepCount > this.maxSweeps)
			{
				return -1;
			}
		}
		
		return sweepCount;
	}
	
	/**
	 * Returns the discounting rate
	 * @return the gamma
	 */
	public double getGamma()
	{
		return gamma;
	}

	/**
	 * Returns the theta
	 * @return theta
	 */
	public double getTheta()
	{
		return this.theta;
	}
	
	/**
	 * @return maxSweeps
	 */
	public int getMaxSweeps()
	{
		return this.maxSweeps;
	}
	
	
	/**
	 * @param gamma the gamma to set
	 */
	public void setGamma(double gamma)
	{
		this.gamma = gamma;
	}
	
	/**
	 * @param theta The maximal allowed difference of V_t(s) and V_t+1(s) within two consecutive sweeps (convergence measure)
	 */
	public void setTheta(double theta)
	{
		this.theta = theta;
	}
	/**
	 * @param maxSweeps Maximum number of sweeps through the state set, if theta is not reached (no limit if set to 0)
	 */
	public void setMaxSweeps(int maxSweeps)
	{
		this.maxSweeps = maxSweeps;
	}
}