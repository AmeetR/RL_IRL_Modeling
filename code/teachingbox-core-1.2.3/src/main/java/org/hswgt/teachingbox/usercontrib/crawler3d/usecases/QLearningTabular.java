package org.hswgt.teachingbox.usercontrib.crawler3d.usecases;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.learner.TabularQLearner;
import org.hswgt.teachingbox.core.rl.policy.PolicyConfigurator;
import org.hswgt.teachingbox.core.rl.tabular.HashQFunction;
import org.hswgt.teachingbox.core.rl.tabular.TabularQFunction;
import org.hswgt.teachingbox.usercontrib.crawler3d.env.Crawler3DEnvironment;
import org.hswgt.teachingbox.usercontrib.crawler3d.usecases.DisplayQValuesObserver;


/**
 * This use-case runs Q-Learning on the hardware walking robot
 * @author Michel Tokic
 */
public class QLearningTabular {
	public static void main(String[] args) throws Exception
    {
		
		/*******************************************************************
		 * IMPORTANT: run usercontrib/crawler3D/python/start.py first!!
		 ******************************************************************/
		
		
		Logger.getRootLogger().setLevel(Level.DEBUG);
        
        // initialize new Q-Function
        TabularQFunction Q = new HashQFunction(0);
        
        // create new Crawler Environment (with termination at robot position x=20)
        Crawler3DEnvironment env = new Crawler3DEnvironment (200.0); 
        
        // policy
        PolicyConfigurator pi = new PolicyConfigurator (Q, Crawler3DEnvironment.ACTION_SET);

        // create agent
        Agent agent = new Agent(pi);
        
        // experiment setups
        final int MAX_EPISODES = 1;
        final int MAX_STEPS    = 300000;
        final double alpha     = 0.3;
        final double gamma     = 0.98;
        
        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);
        
        // setup learner
        TabularQLearner learner = new TabularQLearner(Q);
        learner.setAlpha(alpha);
        learner.setGamma(gamma);
        
        // no learning, only action selection
        agent.addObserver(learner);
                
        // Display Q-Function after each step.
        experiment.addObserver(new DisplayQValuesObserver(Q));
        
        experiment.run();
    }
}
