/**
 *
 * $Id: GradientDescentSarsaLinearAverageLearner.java 661 2010-11-06 10:10:57Z Thomas Wanschik $
 *
 * @version   $Rev: 661 $
 * @author    $Author: Thomas Wanschik $
 * @date      $Date: 2010-11-06 10:10:57 +0100 (Fr, 11 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.learner;

import cern.colt.function.DoubleFunction;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.DifferentiableQFunction;

import cern.colt.matrix.DoubleMatrix1D;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.feature.FeatureFunction;

/**
 * Modified Gradient descent SARSA method avoiding divergences to infinity in
 * function approximation. See update rule from "Path-finding in real and
 * simulated rats: assessing the influence of path characteristics on navigation
 * learning" from Minija Tamosiunaite.
 * 
 * @see <a href="http://www.cs.ualberta.ca/%7Esutton/book/ebook/node89.html">http://www.cs.ualberta.ca/%7Esutton/book/ebook/node89.html</a>
 */
public class GradientDescentSarsaLinearAverageLearner extends GradientDescentTdLearner
{        
    private static final long serialVersionUID = 2571781809714199751L;

    // The QFunction that will be estimated
    protected DifferentiableQFunction Q;
        
    /**
     * Constructs a new SARSA Learner that uses gradient descent
     * to learn a Q-Function
     * @param Q The Q-Function to learn
     * @param featureFunction The feature function
     * @param actionSet The action set
     */
    public GradientDescentSarsaLinearAverageLearner(DifferentiableQFunction Q,
            FeatureFunction featureFunction, ActionSet actionSet)
    {
        super(Q, featureFunction, actionSet);
        this.Q = Q;
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.GradientDescentControlLearner#getTdError(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public DoubleMatrix1D getTdErrors(State state, Action action, State nextState,
            Action nextAction, double reward, boolean isTerminalState)
    {
        double qn = 0;
        
        // the Q-Value of a terminal state is 0
        if ( !isTerminalState ) 
        {
            qn = Q.getValue(nextState, nextAction);
        }
        // get weights after Q.getValue(sn, an) is calculated cause
        // Q.getValue(sn, an) can result in adding a RBF
        // Q.getWeigths already returns a copy
        DoubleMatrix1D tdErrors = Q.getWeights();

        final double tmp = reward + gamma*qn;
        // calculates the tdError for each weight, proven to be more stable, see
        // reynold "The sability of General Discouted Reinforcement Learning with
        // Linear Function Approximation"
        tdErrors.assign(new DoubleFunction() {
            public double apply(double weight) {
                return tmp - weight;
            }
        });
        return tdErrors;
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.GradientDescentTdLearner#getGradient(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public DoubleMatrix1D getGradient(State state, Action action, State nextState,
            Action nextAction, double reward, boolean isTerminalState)
    {
        return Q.getGradient(state, action);
    }
}
