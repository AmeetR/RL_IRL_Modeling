/**
 *
 * $Id: GradientDescentSarsaLearner.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.learner;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.DifferentiableQFunction;

import cern.colt.matrix.DoubleMatrix1D;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.feature.FeatureFunction;

/**
 * Gradient descent SARSA
 * 
 * @see <a href="http://www.cs.ualberta.ca/%7Esutton/book/ebook/node89.html">http://www.cs.ualberta.ca/%7Esutton/book/ebook/node89.html</a>
 */
public class GradientDescentSarsaLearner extends GradientDescentTdLearner
{        
    private static final long serialVersionUID = 2571781809714199751L;

    // The QFunction that will be estimated
    protected DifferentiableQFunction Q;
        
    /**
     * Constructs a new SARSA Learner that uses gradient descent
     * to learn a Q-Function
     * @param Q The Q-Function to {@link Learner}
     * @param featureFunction The feature function
     * @param actionSet The action set
     */
    public GradientDescentSarsaLearner(DifferentiableQFunction Q,
            FeatureFunction featureFunction, ActionSet actionSet) {
        super(Q, featureFunction, actionSet);
        this.Q = Q;
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.GradientDescentControlLearner#getTdError(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public DoubleMatrix1D getTdErrors(State state, Action action, State nextState,
            Action nextAction, double reward, boolean isTerminalState) {
        // get Q-Value for actual state and next state
        double q = Q.getValue(state, action);
        double qn = 0;
        
        // the Q-Value of a terminal state is 0
        if ( !isTerminalState ) {
            qn = Q.getValue(nextState, nextAction);
        }
        // calcuate td-error
        return Q.getWeights().like().assign(reward + gamma*qn - q);
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.GradientDescentTdLearner#getGradient(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public DoubleMatrix1D getGradient(State state, Action action, State nextState,
            Action nextAction, double reward, boolean isTerminalState) {
        return Q.getGradient(state, action);
    }
}
