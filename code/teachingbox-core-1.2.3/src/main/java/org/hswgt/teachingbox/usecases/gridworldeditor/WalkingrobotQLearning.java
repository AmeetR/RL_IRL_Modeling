package org.hswgt.teachingbox.usecases.gridworldeditor;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.env.gridworldeditor.env.GridworldEnvironment;
import org.hswgt.teachingbox.core.rl.etrace.ETraceType;
import org.hswgt.teachingbox.core.rl.experiment.EpisodicSaver;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.learner.TabularQLearner;
import org.hswgt.teachingbox.core.rl.policy.PolicyConfigurator;
import org.hswgt.teachingbox.core.rl.tabular.HashQFunction;
import org.hswgt.teachingbox.core.rl.tabular.TabularQFunction;

/**
 * Gridworld usecase with the walkingrobot example
 * @author tokicm
 *
 */
public class WalkingrobotQLearning {
	public static void main(String[] args) throws Exception
    {
		Logger.getRootLogger().setLevel(Level.INFO);
        
        // init new Q-Function with default value 0
        TabularQFunction Q = new HashQFunction(0);
        
        GridworldEnvironment env = new GridworldEnvironment("data/gridworld/Walkingrobot-RealWorldData.xml");

        //PolicyConfigurator pi = new PolicyConfigurator(Q, GridworldEnvironment.ACTION_SET);
        PolicyConfigurator pi = new PolicyConfigurator(Q, GridworldEnvironment.ACTION_SET);
        //EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy (Q, GridworldEnvironment.ACTION_SET,0.1);
  
        // create agent
        Agent agent = new Agent(pi);
        
        // experiment setups
        final int MAX_EPISODES = 500;
        final int MAX_STEPS    = 2000;
        final double alpha     = 1.0;
        final double gamma     = 0.99;
        
        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);
        experiment.setInitState(new State(new double[]{0, 3}));
        
        // setup learner
        TabularQLearner learner = new TabularQLearner(Q);
        learner.setAlpha(alpha);
        learner.setGamma(gamma);
        learner.setEtraceType(ETraceType.none);
        // attach learner to agent
        agent.addObserver(learner);
        
        // Display Q-Function after each step.
        experiment.addObserver(new DisplayQValuesObserver(Q) );
        System.out.println("Please press enter to start the experiment!");
        System.in.read();
        // run experiment
        experiment.run();
    }
}
