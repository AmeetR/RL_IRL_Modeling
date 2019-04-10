
package org.hswgt.teachingbox.core.rl.dp;

import java.util.List;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.datastructures.RewardFunction;
import org.hswgt.teachingbox.core.rl.datastructures.TransitionFunction;
import org.hswgt.teachingbox.core.rl.datastructures.TransitionProbability;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.learner.Learner;
import org.hswgt.teachingbox.core.rl.tabular.TabularValueFunction;

/**
 * The ValueIteration algorithm
 * See <a href="http://www.cs.ualberta.ca/%7Esutton/book/ebook/node44.html">http://www.cs.ualberta.ca/%7Esutton/book/ebook/node44.html/</a>
 * for algorithm details. This implementation needs a TransitionFunction that encodes the
 * probability of <pre>p(s'|s,a)</pre> and a RewardFunction <pre>r(s,a,s')</pre>.
 * 
 * This implementation sweeps not over all states, but it searches d-steps in all directions (default is 100).
 */
public class ValueIterationLearner implements Learner
{
    // Logger
    private static final Logger log4j = Logger.getLogger("ValueIterationLearner");
    
    private static final long serialVersionUID = 3076124288869247642L;
    ActionSet actionSet;
    TransitionFunction tf;
    RewardFunction rf;
    int deep;
    
    /**
     * The default discount-rate
     */
    public static final double DEFAULT_GAMMA = 0.95;
    
    // The discount-rate
    protected double gamma  = DEFAULT_GAMMA;
    
    // This is the value function to learn
    protected TabularValueFunction V;

    /**
     * Constructor
     * @param V The ValueFunction to learn
     * @param actionSet A ActionSet with all possible actions
     * @param tf The TransitionFunction
     * @param rf The RewardFunction
     */
    public ValueIterationLearner(TabularValueFunction V, ActionSet actionSet, TransitionFunction tf, RewardFunction rf)
    {
        this(V, actionSet, tf, rf, 100);
    }
    

    /**
     * Constructor
     * @param V The ValueFunction to learn
     * @param actionSet A ActionSet with all possible actions
     * @param tf The TransitionFunction
     * @param rf The RewardFunction
     * @param deep Number of deep searches in all directions
     */
    public ValueIterationLearner(TabularValueFunction V, ActionSet actionSet, TransitionFunction tf, RewardFunction rf, int deep)
    {
        this.actionSet = actionSet;
        this.tf = tf;
        this.rf = rf;
        this.deep = deep;
        this.V = V;
    }
    
    protected void iterationStep(final State s, int iterationsLeft)
    {
        if( iterationsLeft <= 0 )
            return;
        
        ActionSet validActions = actionSet.getValidActions(s);
        double maxQ = Double.NEGATIVE_INFINITY;
        double maxV = Double.NEGATIVE_INFINITY;
        State bestSn = s;
        
        log4j.debug("interationStep("+s+","+iterationsLeft+")");
        // for all valid action search the one with
        // highest successor state
        for( Action a : validActions ){
            // sum over possible successor states
            double sum = 0;
            // get successor states and their transition probabilities
            List<TransitionProbability> tpList= tf.getTransitionProbabilities(s, a);
            
            for( TransitionProbability tp : tpList ){
                log4j.debug("p("+tp.getState()+","+tp.getAction()+","+tp.getNextState()+") = "+tp.getProbability());
            }
            
            // iterate over all possible successor states
            // and sum up their values weighted by probability
            for( TransitionProbability tp : tpList ){
                State sn = tp.getNextState();
                double r = rf.getReward(s, a, sn);
                double Vn = V.getValue(sn);
                if( Vn > maxV ){
                    maxV = Vn;
                    bestSn = sn;
                }
                sum += tp.getProbability() * (r + gamma * Vn);
            }
            maxQ = Math.max(maxQ, sum);
        }
        V.setValue(s, maxQ);
        
        iterationStep(bestSn, iterationsLeft-1);
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.Learner#update(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public void update(State s, Action a, State sn, Action an, double r,
            boolean isTerminalState)
    {
        iterationStep(s, deep);
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.Learner#updateNewEpisode(org.hswgt.teachingbox.env.State)
     */
    public void updateNewEpisode(State initialState)
    {
        // 
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
     * Sets the discounting rate
     * @param gamma the gamma to set
     */
    public void setGamma(double gamma)
    {
        this.gamma = gamma;
    }
}
