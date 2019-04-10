package org.hswgt.teachingbox.usecases.nfq.mountaincar;

import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQMountainCarEnv;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQMountainCarEnv.MC_DYNAMICS;
import org.hswgt.teachingbox.core.rl.nfq.util.TransitionExporter;
import org.hswgt.teachingbox.core.rl.plot.TrajectoryPlotter2d;
import org.hswgt.teachingbox.core.rl.policy.RandomPolicy;


/**
 * This is a usecase samples mountain car transitions and memorizes them to a text file
 * 
 * @author tokicm
 *
 */
public class MountainCarSampleTransitions {
	
    // experiment setups
	public static int MAX_EPISODES = 50;
    public static int MAX_STEPS    = 20;
	public static int SEED = 1;

	
	public static MC_DYNAMICS mcDynamics = MC_DYNAMICS.RIEDMILLER; 
	//public static MC_DYNAMICS mcDynamics = MC_DYNAMICS.SUTTON_BARTO; 

	public static void sampleTransitions() {
        // setup environment: reward := -1.0 / MAX_STEPS
    	NFQMountainCarEnv env = new NFQMountainCarEnv(mcDynamics, true, -1.0/MAX_STEPS);
    	env.setSeed(SEED);
    	env.setRandomInitialVelocity(true);
    	
    	// setup random exploration policy
        RandomPolicy pi = new RandomPolicy(env.getActionSet());
        pi.setSeed(SEED);
        
        // create agent
        Agent agent = new Agent(pi);
        
        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);	        	      
        
        // add transition exporter to experiment
		experiment.addObserver(new TransitionExporter("nfq-mc-transitions.txt"));		
		
		// add trajectory plotter to experiment
		TrajectoryPlotter2d tp = new TrajectoryPlotter2d();
		tp.setLabel("position", "velocity");
		tp.setTitle("MountainCar NFQ Training-Batch");
		experiment.addObserver(tp);
		
        // RUN!
        experiment.run();
        tp.plot();
        //System.out.println("Network: " + Q);
	}

	public static void main(String[] args) throws Exception {
		MountainCarSampleTransitions.sampleTransitions();
	}
}
