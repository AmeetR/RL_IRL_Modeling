package org.hswgt.teachingbox.usercontrib.crawler.usecases;

//import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.learner.TabularQLearner;
import org.hswgt.teachingbox.core.rl.policy.PolicyConfigurator;
import org.hswgt.teachingbox.core.rl.tabular.HashQFunction;
import org.hswgt.teachingbox.core.rl.tabular.TabularQFunction;
import org.hswgt.teachingbox.usercontrib.crawler.env.CrawlerEnvironment;
//import org.hswgt.teachingbox.env.Action;

/**
 * This use-case runs Q-Learning on the hardware walking robot
 * @author Michel Tokic
 */
public class QLearningTabular {
	public static void main(String[] args) throws Exception
    {
		Logger.getRootLogger().setLevel(Level.INFO);
        
        // initialize new Q-Function
        TabularQFunction Q = new HashQFunction(0);
        
        // create new Crawler Environment 
        CrawlerEnvironment env = new CrawlerEnvironment(19200, "/dev/ttyUSB0"); 
        
        // policy
        PolicyConfigurator pi = new PolicyConfigurator (Q, CrawlerEnvironment.ACTION_SET);

        // create agent
        Agent agent = new Agent(pi);
        
        // experiment setups
        final int MAX_EPISODES = 1;
        final int MAX_STEPS    = 30000;
        final double alpha     = 0.6;
        final double gamma     = 0.9;
        
        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);
        
        // setup learner
        TabularQLearner learner = new TabularQLearner(Q);
        learner.setAlpha(alpha);
        learner.setGamma(gamma);
        
        // no learning, only action selection
        agent.addObserver(learner);
        
        // Display Q-Function after each step.
        DisplayQValuesObserver vObserver = new DisplayQValuesObserver(Q);
        vObserver.sendAllValues(); // display initial Q-Function
        experiment.addObserver(vObserver);
        
        experiment.run();
    }
}
