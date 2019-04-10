package org.hswgt.teachingbox.usecases.nfq.polebalance;

import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQPoleBalanceEnvironment;
import org.hswgt.teachingbox.core.rl.nfq.util.TransitionExporter;
import org.hswgt.teachingbox.core.rl.plot.TrajectoryPlotter2d;
import org.hswgt.teachingbox.core.rl.policy.RandomPolicy;


/**
 * This usecase samples pole-balance transitions and memorizes them to a text file
 * 
 * @author tokicm
 *
 */
public class PoleBalanceSampleTransitions {
	
    // experiment setups
	public static final int MAX_EPISODES = 100;
    public static final int MAX_STEPS    = 20;
	public static final int SEED = 5;
	public static final String FILENAME = "nfq-pb-transitions.txt";

	public static void sampleTransitions() {
		
        // setup environment: reward := -1.0 / MAX_STEPS
		NFQPoleBalanceEnvironment env = new NFQPoleBalanceEnvironment();
		env.setSeed(SEED);
    	
    	// setup random exploration policy
        RandomPolicy pi = new RandomPolicy(env.getActionSet());
        pi.setSeed(SEED);
        
        // create agent
        Agent agent = new Agent(pi);
        
        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);	        	      
        
        // RUN!
		experiment.addObserver(new TransitionExporter(FILENAME));
		
		TrajectoryPlotter2d tp = new TrajectoryPlotter2d();
		tp.setLabel("position", "velocity");
		tp.setTitle("PoleBalance NFQ Training-Batch");
		experiment.addObserver(tp);
		
        experiment.run();
        tp.plot();
        //System.out.println("Network: " + Q);
	}
}
