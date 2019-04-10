package org.hswgt.teachingbox.usercontrib.crawler.usecases;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.policy.PolicyConfigurator;
import org.hswgt.teachingbox.core.rl.tabular.TabularQFunction;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;
import org.hswgt.teachingbox.usercontrib.crawler.env.CrawlerEnvironment;
import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridModel;


/**
 * This is a usecase to run the learned environment from a 5x5 gridworld on the walkingrobot hardware. 
 * Actions according to the learned policy are sent to the robot and the "real" environment reward is sent 
 * back for visualization to the gridworld GUI. This use case does not involve learning.  
 * @author tokicm
 *
 */
public class GridworldSimuExec {
	public static void main(String[] args) throws Exception
    {
		Logger.getRootLogger().setLevel(Level.INFO);
        
        // init: load Q-Function saved by gridworld usecase
        TabularQFunction Q = ObjectSerializer.load("/tmp/QFunction-gridworld.ser");//new HashQFunction(0);
        
        // create new Crawler Environment 
        CrawlerEnvironment env = new CrawlerEnvironment(19200, "/dev/ttyUSB0"); 
        GridModel.getInstance().loadXMLFile(new File ("/home/tokicm/walkingrobot-simu.gridworld"));

        PolicyConfigurator pi = new PolicyConfigurator (Q, CrawlerEnvironment.ACTION_SET);

        // create agent
        Agent agent = new Agent(pi);
        
        // experiment setups
        final int MAX_EPISODES = 1;
        final int MAX_STEPS    = 30000;
        
        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);
             
        // Display Q-Function after each step.
        DisplayQValuesObserver vObserver = new DisplayQValuesObserver(Q);
        vObserver.sendAllValues(); // display initial Q-Function
        experiment.addObserver(vObserver);
        
        experiment.run();
    }
}
