package org.hswgt.teachingbox.core.rl.policy;

import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.policy.SoftmaxActionSelection;
import org.hswgt.teachingbox.core.rl.policy.explorationrate.ConstantEpsilon;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;

import cern.jet.random.Uniform;

/**
 * Implementation of Wiering's Max-Boltzmann Exploration rule. Same as
 * e-Greedy, but, in case of random &lt; epsilon, a softmax action is chosen
 * instead of an equal-distributed action. 
 * @author tokicm
 *
 */
public class MaxBoltzmannExplorationPolicy extends EpsilonGreedyPolicy {

	
	private double temperature = 1.0;
	private double minNorm = -1.0;
	private double maxNorm = 1.0;
	private ActionSet actionSet;
	private QFunction Q;
	
	
	//SoftmaxActionSelection softmax;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8872392646453447161L;

	public MaxBoltzmannExplorationPolicy(QFunction Q, ActionSet actionSet,
			double epsilon) {		
		super(Q, actionSet, epsilon);
		
		// set local variables
		this.Q = Q;
		this.actionSet = actionSet;
		//this.epsilon = epsilon;
		this.epsilonCalculator = new ConstantEpsilon(epsilon);
		this.temperature = 1.0;
		
		// using temperature=1 by default (due to normalizing action values) 
		//softmax = new SoftmaxActionSelection(Q, actionSet, 1);
	}
	


	/**
	 * choose an action to perform. it uses the value "Temperature" to choose
	 * which Action to perform.
	 * 
	 * @param state The state
	 */
	public Action getAction(final State state) {
		
		// return a value-sensitive softmax action instead of an equally distributed e-greedy action 
		if( Uniform.staticNextDouble() < this.epsilonCalculator.getEpsilon(state)) {
			return SoftmaxActionSelection.getNormalizedSoftmaxAction(Q, actionSet, temperature, state, minNorm, maxNorm);
        }

        return getBestAction(state);
	}
	
	/**
	 * the normalization boundaries used for selecting the softmax action 
	 * @param minNorm The minimum value of normalization
	 * @param maxNorm The maximum value of normalization
	 */
	public void setNormBoundaries(double minNorm, double maxNorm) {
		
		this.minNorm = minNorm;
		this.maxNorm = maxNorm;
	}
}

