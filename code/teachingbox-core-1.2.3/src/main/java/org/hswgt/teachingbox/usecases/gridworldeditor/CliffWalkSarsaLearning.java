package org.hswgt.teachingbox.usecases.gridworldeditor;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.env.gridworldeditor.env.GridworldEnvironment;
import org.hswgt.teachingbox.core.rl.etrace.ETraceType;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.learner.TabularSarsaLearner;
import org.hswgt.teachingbox.core.rl.tabular.HashQFunction;
import org.hswgt.teachingbox.core.rl.tabular.TabularQFunction;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;

/**
 * Sarsa-Learning gridworld use-case of the cliff-walk example from the 
 * Sutton and Barto book "Reinforcement Learning: An Introduction".
 * URL: http://www.cs.ualberta.ca/~sutton/book/ebook/node65.html
 * @author tokicm
 *
 */
public class CliffWalkSarsaLearning {
	public static void main(String[] args) throws Exception
    {
		Logger.getRootLogger().setLevel(Level.DEBUG);
        
        // init new Q-Function with default value 0
        TabularQFunction Q = new HashQFunction(0, GridworldEnvironment.ACTION_SET);
        //TabularQFunction Q = ObjectSerializer.load("/tmp/QFunction-gridworld.ser");
        
        GridworldEnvironment env = new GridworldEnvironment("data/gridworld/CliffWalk.xml");

        //PolicyConfigurator pi = new PolicyConfigurator(Q, GridworldEnvironment.ACTION_SET);
        EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy(Q, GridworldEnvironment.ACTION_SET, 0.2);
  
        // create agent
        Agent agent = new Agent(pi);
        
        // experiment setups
        final int MAX_EPISODES = 50000;
        final int MAX_STEPS    = 100;
        final double alpha     = 0.3;
        final double gamma     = 1;
        
        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);
        //experiment.addObserver(new EpisodicSaver("/tmp/QFunction-gridworld.ser", Q) );
        experiment.setInitState(new State(new double[]{0, 3}));
        
        // setup learner
        //TabularQLearner learner = new TabularQLearner(Q);
        TabularSarsaLearner learner = new TabularSarsaLearner(Q);
        learner.setAlpha(alpha);
        learner.setGamma(gamma);
        learner.setEtraceType(ETraceType.none);
        // attach learner to agent
        agent.addObserver(learner);
        
        // Display Q-Function after each step.
        experiment.addObserver(new DisplayQValuesObserver(Q));

        // run experiment
        experiment.run();
    }
}
