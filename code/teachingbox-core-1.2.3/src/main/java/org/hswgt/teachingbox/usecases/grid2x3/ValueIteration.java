package org.hswgt.teachingbox.usecases.grid2x3;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.datastructures.RewardFunction;
import org.hswgt.teachingbox.core.rl.datastructures.TransitionFunction;
import org.hswgt.teachingbox.core.rl.datastructures.TransitionProbability;
import org.hswgt.teachingbox.core.rl.dp.ValueIterationLearner;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.Grid2x3Environment;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.policy.RandomPolicy;
import org.hswgt.teachingbox.core.rl.tabular.HashValueFunction;
import org.hswgt.teachingbox.core.rl.tabular.QFunctionFromValueFunction;
import org.hswgt.teachingbox.core.rl.tabular.TabularValueFunction;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;

import cern.colt.matrix.linalg.SeqBlas;

/**
 * <pre>
 * Environment: MountainCar
 * Algorithm: ValueIteration
 * Approximation: Tabular
 * </pre>
 */
public class ValueIteration
{
    public static void main(String[] args) throws Exception
    {
        Logger.getRootLogger().setLevel(Level.INFO);
        Logger.getLogger(ValueIterationLearner.class.getSimpleName()).setLevel(Level.DEBUG);
        
        // init new V-Function with default value 0
        TabularValueFunction V = new HashValueFunction(0);
        
        // setup environment
        Grid2x3Environment env = new Grid2x3Environment();
        
        // setup policy
        RandomPolicy pi = new RandomPolicy(Grid2x3Environment.ACTION_SET);
        
        // create agent
        Agent agent = new Agent(pi);
        
        // experiment setups
        final int MAX_EPISODES = 1000;
        final int MAX_STEPS    = 5000;
        final double gamma     = 0.9;
        
        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);
        
        // setup transition function and reward function
        TransitionFunction tf = new Grid2x3TransitionFunction();
        RewardFunction rf = new Grid2x3RewardFunction();
        
        // setup learner
        ValueIterationLearner learner = new  ValueIterationLearner(V, Grid2x3Environment.ACTION_SET, tf, rf);
        
        learner.setGamma(gamma);
        
        // attach learner to agent
        agent.addObserver(learner);
        
        // Display Q-Function after each step.
        QFunction Q = new QFunctionFromValueFunction(V, Grid2x3Environment.ACTION_SET, tf, rf, gamma);
        experiment.addObserver(new DisplayValuesObserver(V) );
        experiment.addObserver(new DisplayQValuesObserver(Q) );
        
        // run experiment
        experiment.run();
    }
}

class Grid2x3TransitionFunction implements TransitionFunction
{
    private static final long serialVersionUID = 2721625934013237870L;

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.datastructures.TransitionFunction#getTransitionProbabilities(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action)
     */
    public List<TransitionProbability> getTransitionProbabilities(State s, Action a)
    {
        State sn = s.copy();
        
        // s = s + a;
        SeqBlas.seqBlas.daxpy(1, a, sn);
        
        // check range [0 2][0 1]
        sn.set(0, Math.min(sn.get(0), 2));
        sn.set(0, Math.max(sn.get(0), 0));
        sn.set(1, Math.min(sn.get(1), 1));
        sn.set(1, Math.max(sn.get(1), 0));
        
        List<TransitionProbability> tp = new LinkedList<TransitionProbability>();
        tp.add(new TransitionProbability(s,a,sn,1));
        return tp;
    }
}

class Grid2x3RewardFunction implements RewardFunction
{
    private static final long serialVersionUID = 2452455971918030887L;

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.datastructures.RewardFunction#getReward(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State)
     */
    public double getReward(State s, Action a, State sn)
    {
        // calc reward
        double r = 0;
        if( s.get(1) == 0 ){
            if( a.equals( Grid2x3Environment.RIGHT ) ){
                r = +1;
            }
            if( a.equals( Grid2x3Environment.LEFT ) ){
                r = -1;
            }
        }
        return r;
    }    
}