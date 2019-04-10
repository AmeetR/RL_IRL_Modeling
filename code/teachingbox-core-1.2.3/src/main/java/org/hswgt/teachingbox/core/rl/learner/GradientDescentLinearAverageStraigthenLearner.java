/**
 *
 * $Id: GradientDescentSarsaLinearAverageLearner.java 663 2010-11-06 10:10:57Z Thomas Wanschik $
 *
 * @version   $Rev: 663 $
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
 * Modified Gradient descent method similar to SARSA. It's neither SARSA nor
 * Q-learning because it uses the q-value of the (nextState, currentAction) for 
 * the td-error. 
 * See update rule from "Learning to reacj by reinforcement learning using a
 * receptive field based function approximation approach with continuous actions"
 * from Minija Tamosiunaite
 * 
 * @see <a href="http://www.cs.ualberta.ca/%7Esutton/book/ebook/node89.html">http://www.cs.ualberta.ca/%7Esutton/book/ebook/node89.html</a>
 */
public class GradientDescentLinearAverageStraigthenLearner extends GradientDescentTdLearner
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
    public GradientDescentLinearAverageStraigthenLearner(DifferentiableQFunction Q,
            FeatureFunction featureFunction, ActionSet actionSet)
    {
        super(Q, featureFunction, actionSet);
        this.Q = Q;
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.GradientDescentControlLearner#getTdError(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public DoubleMatrix1D getTdErrors(State s, Action a, State sn, Action an, double r, boolean isTerminalState)
    {
        double qn = 0;
        
        // the Q-Value of a terminal state is 0
        // the difference to GradientDescentSarsaLinearAverageLearner is that we
        // take the q-value of the (nextState, currentAction) so as if we would
        // taken the same action for the next state (off-policy). This rule is
        // taken from "Learning to reacj by reinforcement learning using a
        // receptive field based function approximation approach with continuous
        // actions" from Minija Tamosiunaite
        if ( !isTerminalState ) 
        {
            qn = Q.getValue(sn, a);
        }
        // get weights after Q.getValue(sn, an) was calculated cause Q.getValue(sn, an)
        // can result in adding a RBF
        DoubleMatrix1D weights = Q.getWeights();

        final double tmp = r + gamma*qn;
        // calculates the tdError for each weight, proven to be more stable, see
        // reynold "The sability of General Discouted Reinforcement Learning with
        // Linear Function Approximation"
        DoubleMatrix1D tdErrors = weights.copy().assign(new DoubleFunction() {
            public double apply(double weight) {
                return tmp - weight;
            }
        });
        return tdErrors;
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.GradientDescentTdLearner#getGradient(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public DoubleMatrix1D getGradient(State s, Action a, State sn, Action an, double r, boolean isTerminalState)
    {
        return Q.getGradient(s, a);
    }
}
