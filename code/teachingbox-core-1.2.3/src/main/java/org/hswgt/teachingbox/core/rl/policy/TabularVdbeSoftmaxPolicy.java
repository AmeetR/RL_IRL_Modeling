package org.hswgt.teachingbox.core.rl.policy;

import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;

import cern.jet.random.Uniform;

/**
 * Implementation of the VDBE-Softmax policy from:
 *  
 *  M. Tokic and G. Palm. Value-difference based exploration: Adaptive control 
 *  between epsilon-greedy and softmax. In J. Bach and S. Edelkamp, editors, 
 *  KI 2011: Advances in Artificial Intelligence, volume 7006 of Lecture Notes 
 *  in Artificial Intelligence, pages 335-346. Springer Berlin / Heidelberg, 
 *  2011.  
 * @author tokicm
 *
 */
public class TabularVdbeSoftmaxPolicy extends TabularVDBEPolicy {

	private double minNormValue = -1;
	private double maxNormValue = 1;

	private static final long serialVersionUID = 402577854022381234L;
	

	/**
	 * The constructor
	 * @param Q The Q-function
	 * @param as The action set
	 * @param sigma The inverse sensitivity parameter
	 */
	public TabularVdbeSoftmaxPolicy (QFunction Q, ActionSet as, double sigma) {
		super(Q, as, sigma);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Sets the minimum and the maximum norm values (default: minNormValue = -1, maxNormValue = 1)
	 * @param minNormValue The minimum normalized value
	 * @param maxNormValue The maximum normalized value
	 */
	public void setNormBoundaries (double minNormValue, double maxNormValue) {
		this.minNormValue = minNormValue;
		this.maxNormValue = maxNormValue;
	}
	
	/**
	 * Returns an action for given state and epsilon
	 * @param state The @State
	 * @return The @Action
	 */
    public Action getAction(final State state) {
    	
    	Action action;
    	
    	
    	// return softmax action from the set of sub-optimal actions 
        if( Uniform.staticNextDouble() < (epsilon.getValue(state, epsilonAction))) {
        	
        	// uses default temperature tau=1 ; according to (Tokic & Palm 2011)
        	action = SoftmaxActionSelection.getNormalizedSoftmaxAction (Q, this.as, 1, state, minNormValue, maxNormValue);        	
        	
    	// return greedy action
        } else {
        	action = getBestAction(state);         	
        }
        return action; 
    }	
    
	/**
	 * Returns an action for given state and epsilon
	 * @param state The @State
	 * @param epsilon The exploration rate [0, 1.0]
	 * @return The @Action
	 */
    public Action getAction(final State state, double epsilon) {
    	
    	Action action;
    	
    	// return softmax action from the set of sub-optimal actions 
        if( Uniform.staticNextDouble() < epsilon) {
        	
        	// uses default temperature tau=1 ; according to (Tokic & Palm 2011)
        	action = SoftmaxActionSelection.getNormalizedSoftmaxAction (Q, this.as, 1, state, minNormValue, maxNormValue);        	
        	
    	// return greedy action
        } else {
        	action = getBestAction(state);         	
        }
        return action; 
    }	    
}
