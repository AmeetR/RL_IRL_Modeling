package org.hswgt.teachingbox.usecases.grid2x3;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.Grid2x3Environment;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.learner.TabularQLearner;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.tabular.HashQFunction;
import org.hswgt.teachingbox.core.rl.tabular.TabularQFunction;

/**
 * <pre>
 * Environment: MountainCar
 * Algorithm: Q-Learning
 * Approximation: Adaptive RBFNetwork
 * </pre>
 */
public class QLearningTabular
{
    public static void main(String[] args) throws Exception
    {
        Logger.getRootLogger().setLevel(Level.INFO);
        
        // init new Q-Function with default value 0
        TabularQFunction Q = new HashQFunction(0, Grid2x3Environment.ACTION_SET);
        
        // setup environment
        Grid2x3Environment env = new Grid2x3Environment();
        
        // setup policy
        // we use a greedy e-policy with epsilon 0.9 here
        // in order to make decisions we have to provide the
        // QFunction and the set of all possible action
        EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy(Q, Grid2x3Environment.ACTION_SET, 0.9);
        
        // create agent
        Agent agent = new Agent(pi);
        
        // experiment setups
        final int MAX_EPISODES = 1000;
        final int MAX_STEPS    = 5000;
        final double alpha     = 1.0;
        final double gamma     = 0.9;
        final double lambda    = 0.9;
        
        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);
        
        // setup learner
        TabularQLearner learner = new TabularQLearner(Q);
        learner.setAlpha(alpha);
        learner.setGamma(gamma);
        learner.setLambda(lambda);
        
        // attach learner to agent
        agent.addObserver(learner);
        
        // Display Q-Function after each step.
        experiment.addObserver(new DisplayQValuesObserver(Q));
        
        // run experiment
        experiment.run();
    }
}