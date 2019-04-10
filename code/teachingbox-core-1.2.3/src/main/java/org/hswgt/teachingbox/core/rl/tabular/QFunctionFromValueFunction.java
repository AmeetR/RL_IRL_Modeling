/**
 *
 * $Id: QFunctionFromValueFunction.java 764 2010-10-25 18:55:20Z twanschik $
 *
 * @version   $Rev: 764 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-10-25 20:55:20 +0200 (Mon, 25 Oct 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.tabular;

import java.util.List;

import org.hswgt.teachingbox.core.rl.datastructures.ActionFilter;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.datastructures.RewardFunction;
import org.hswgt.teachingbox.core.rl.datastructures.TransitionFunction;
import org.hswgt.teachingbox.core.rl.datastructures.TransitionProbability;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;
import org.hswgt.teachingbox.core.rl.valuefunctions.ValueFunction;

/**
 * This QFunction has an internal ValueFunction and
 * uses the fact, that
 * <pre> Q(s,a) = sum_{s'} p(s'|s,a)(r(s,a,s') + gamma V(s')) </pre>
 * Therefore the transition function <pre>p(s'|s,a)</pre>, the reward
 * function <pre>r(s,a,s')</pre> and the discount factor <pre>gamma</pre>
 * is needed.
 */
public class QFunctionFromValueFunction implements QFunction
{
    private static final long serialVersionUID = 4580000142401615275L;
    ValueFunction V;
    TransitionFunction tf;
    RewardFunction rf;
    double gamma;
    ActionSet actionSet;
    
    /**
     * The Constructor
     * @param V The ValueFunction to use
     * @param actionSet A set with possible actions
     * @param tf The transition function
     * @param rf The reward function
     * @param gamma The discount factor
     */
    public QFunctionFromValueFunction(ValueFunction V, ActionSet actionSet, TransitionFunction tf, RewardFunction rf, double gamma)
    {
        this.V = V;
        this.tf = tf;
        this.rf = rf;
        this.gamma = gamma;
        this.actionSet = actionSet;
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.valuefunctions.QFunction#getMaxValue(org.hswgt.teachingbox.env.State)
     */
    public double getMaxValue(State s)
    {
        ActionSet validActions = actionSet.getValidActions(s);
        double maxQ = Double.NEGATIVE_INFINITY;
        
        // for all valid action search the one with
        // highest successor state
        for( Action a : validActions ) {
            // sum over possible successor states
            double sum = 0;
            // get successor states and their transition probabilities
            List<TransitionProbability> tpList= tf.getTransitionProbabilities(s, a);
            
            // iterate over all possible successor states
            // and sum up their values weighted by probability
            for( TransitionProbability tp : tpList ){
                State sn = tp.getNextState();
                double r = rf.getReward(s, a, sn);
                double Vn = V.getValue(sn);
                sum += tp.getProbability() * (r + gamma * Vn);
            }
            maxQ = Math.max(maxQ, sum);
        }
        return maxQ;
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.valuefunctions.QFunction#getValue(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action)
     */
    public double getValue(State s, Action a)
    {
        ActionFilter filter = this.actionSet.getFilter();
        if (filter != null && filter.isPermitted(s, a) == false ){
            return Double.NEGATIVE_INFINITY; 
        }
        
        // sum over possible successor states
        double sum = 0;
        // get successor states and their transition probabilities
        List<TransitionProbability> tpList= tf.getTransitionProbabilities(s, a);
        
        // iterate over all possible successor states
        // and sum up their values weighted by probability
        for( TransitionProbability tp : tpList ){
            State sn = tp.getNextState();
            double r = rf.getReward(s, a, sn);
            sum += tp.getProbability() * (r + gamma * V.getValue(sn));
        }
        return sum;
    }
    
}
