package org.hswgt.teachingbox.usecases.nfq.mountaincar;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQMountainCarEnv;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQMountainCarInputFeatures;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQMountainCarEnv.MC_DYNAMICS;
import org.hswgt.teachingbox.core.rl.plot.PolicyPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.QFunctionPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter;
import org.hswgt.teachingbox.core.rl.plot.TrajectoryPlotter2d;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;
import org.hswgt.teachingbox.core.rl.viz.mountaincar.MountainCarVisualization;



/**
 * A usecase running the MountainCar environment (without learning)
 * 10 times from random starting positions. 
 * An already learned value function is used instead of learning from scratch.
 * The policy is epsilon-Greedy with epsilon=0.1 (i.e. 10% random actions)
 * @author tokicm
 */
public class ReplayMountainCarNFQ {
	
    // experiment setups
	public static int MAX_EPISODES = 10;
    public static int MAX_STEPS    = 300;
    public static int PLOT_EPISODES = 1;
    public final static int MAX_EXPERIMENTS = 1;
    public static final String Q_FILENAME = "data/piEval-NFQ-Mountaincar.Q";
	
	public static MC_DYNAMICS mcDynamics = MC_DYNAMICS.RIEDMILLER; 
	//public static MC_DYNAMICS mcDynamics = MC_DYNAMICS.SUTTON_BARTO; 
	
	public static void main(String[] args) throws Exception {
		Logger.getRootLogger().setLevel(Level.OFF);
	
		// Configure Environment
       	NFQMountainCarEnv env = new NFQMountainCarEnv(mcDynamics, false, -1.0 / (double) MAX_STEPS);
       	NFQMountainCarInputFeatures features = new NFQMountainCarInputFeatures(env);	        		
       	
       	System.out.println ("InputFeatures=" + features.getNumInputFeatures());

       	// load Q-function
       	QFunction Q = ObjectSerializer.load(Q_FILENAME);
       	
        // setup policy
        EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy(Q, env.getActionSet(), 0.1);

        // create agent
        Agent agent = new Agent(pi);
	        
		// setup experiment
		Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);	        	    

		
		// Initialize Plotter
		QFunctionPlotter3D qPlotter = new QFunctionPlotter3D(Q);
		qPlotter.setBounds(
				new double[] { env.getMinPos(), env.getMaxPos()}, 
				new double[] { env.getMinVel(), env.getMaxVel()});
		qPlotter.setLabels("Position", "Velocity", "Action");
		qPlotter.setFilename("mc-policy.svg");
		qPlotter.setTitle("Mountain-Car QFunction");
		qPlotter.plot();
		//experiment.addObserver(new RuntimePlotter(qPlotter, Mode.EPISODE, 1, null));
		
		
		PolicyPlotter3D policyPlotter = new PolicyPlotter3D(pi);
		policyPlotter.setBounds(
				new double[] { env.getMinPos(), env.getMaxPos()}, 
				new double[] { env.getMinVel(), env.getMaxVel()});
		policyPlotter.setLabels("Position", "Velocity", "Action");
		policyPlotter.setFilename("mc-policy.svg");
		policyPlotter.setTitle("Mountain-Car Policy");
		policyPlotter.plot();

		//policyPlotter.setView(0.01, 0.01);
		//experiment.addObserver(new RuntimePlotter(policyPlotter, Mode.EPISODE, 1, null));

		// CAR-Visualization
        MountainCarVisualization viz = new MountainCarVisualization (
		        agent, null, env, Mode.EPISODE, PLOT_EPISODES, MAX_STEPS);
        experiment.addObserver(viz);
		
		// TrajectoryPlotter
		TrajectoryPlotter2d trajectoryPlotter = new TrajectoryPlotter2d();
		trajectoryPlotter.setLabel("Position", "Velocity");
		trajectoryPlotter.setTitle("MountainCar NFQ trajectories");
		agent.addObserver(trajectoryPlotter); // observes step/episode updates
		experiment.addObserver(new RuntimePlotter(trajectoryPlotter, Mode.EPISODE, 1, null)); // calls plot() after each episode
		              
		// START EXPERIMENT
		experiment.run();
	}
}
