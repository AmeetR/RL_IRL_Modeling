/**
 *
 * $Id: GradientDescentExpectedSarsaLearner.java 765 2010-11-04 12:22:45Z twanschik $
 *
 * @version   $Rev: 765 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-11-04 13:22:45 +0100 (Do, 04 Nov 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.learner;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.DifferentiableQFunction;

import cern.colt.matrix.DoubleMatrix1D;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.feature.FeatureFunction;
import org.hswgt.teachingbox.core.rl.policy.Policy;

/**
 * Gradient descent expected SARSA from "A Theoretical and Empirical Analysis of
 * Expected Sarsa", Harm van Seijen, Hado van Hasselt, Shimon Whiteson and
 * Marco Wiering
 *
 * Expected SARSA seems to outperform traditional SARSA as well as Q-learning
 * 
 */
public class GradientDescentExpectedSarsaLearner extends GradientDescentTdLearner
{        
    private static final long serialVersionUID = 2571781809714199751L;

    // The QFunction that will be estimated
    protected DifferentiableQFunction Q;
    protected Policy policy;
    protected ActionSet actionSet;
        
    /**
     * Constructs a new SARSA Learner that uses gradient descent
     * to learn a Q-Function
     * @param Q The Q-Function to learn
     * @param featureFunction The feature function
     * @param actionSet The action set
     * @param policy The policy
     */
    public GradientDescentExpectedSarsaLearner(DifferentiableQFunction Q,
            FeatureFunction featureFunction, ActionSet actionSet, Policy policy) {
        super(Q, featureFunction, actionSet);
        this.actionSet = actionSet;
        this.Q = Q;
        this.policy = policy;
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.GradientDescentControlLearner#getTdError(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public DoubleMatrix1D getTdErrors(State state, Action action, State nextState,
            Action nextAction, double reward, boolean isTerminalState) {
        // get Q-Value for actual state and next state
        double q = Q.getValue(state, action);
        double expectationValue = 0;
        
        // the expectation value of a terminal state is 0
        if ( !isTerminalState ) {
            for (Action tmpAction : this.actionSet.getValidActions(nextState)) {
                expectationValue += this.policy.getProbability(nextState,
                    tmpAction)*Q.getValue(nextState, tmpAction);
            }
        }
        // calcuate td-error
        return Q.getWeights().like().assign(reward + gamma*expectationValue - q);
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.GradientDescentTdLearner#getGradient(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public DoubleMatrix1D getGradient(State state, Action action, State nextState,
            Action nextAction, double reward, boolean isTerminalState) {
        return Q.getGradient(state, action);
    }
}
