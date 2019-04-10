package org.hswgt.teachingbox.usecases.cliffWalking;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.gridworldeditor.env.GridworldEnvironment;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.learner.TabularQLearner;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.tabular.HashQFunction;

/**
 * Use case for the cliff-walking environment.
 * @author tokicm
 *
 */
public class CliffWalking {
	public static void main(String[] args) throws Exception   {   
	
		System.out.println ("starting cliffwalking");
		
		Logger.getRootLogger().setLevel(Level.INFO);
		
		final int MAX_STEPS = 100; 
		final int EPISODES = 1000;
		
		// setup environment
		GridworldEnvironment env = new GridworldEnvironment("data/gridworld/cliffwalk-4x12.gridworld", false);
		
		// setup Q function and policy
		HashQFunction Q = new HashQFunction (0);
		EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy(Q, GridworldEnvironment.ACTION_SET);
		
		// setup learner and agent
		TabularQLearner learner = new TabularQLearner (Q);
		learner.setAlpha(0.1);
		learner.setGamma(0.9);
		
		Agent agent = new Agent (pi);
		agent.addObserver(learner);
		
		// run experiment
		Experiment experiment = new Experiment(agent, env, EPISODES, MAX_STEPS);
		experiment.run();
		
	}
}
