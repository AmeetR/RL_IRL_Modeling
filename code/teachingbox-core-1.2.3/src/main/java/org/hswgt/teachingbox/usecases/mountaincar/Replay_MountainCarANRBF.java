package org.hswgt.teachingbox.usecases.mountaincar;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.MountainCarEnv;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.plot.PolicyPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.QFunctionPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter;
import org.hswgt.teachingbox.core.rl.plot.TrajectoryPlotter2d;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.policy.GreedyPolicy;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;
import org.hswgt.teachingbox.core.rl.viz.mountaincar.MountainCarVisualization;

/**
 * This class uses the learned Q-function from @MountainCarANRBF and starts
 * 10 experiments with random start positions. Each episode is visualized. 
 * As well all performed trajectories.
 */
public class Replay_MountainCarANRBF
{
	/**
	 * @param args The command-line arguments
	 * @throws Exception The Exception
	 */
    public static void main(String[] args) throws Exception
    {
        Logger.getRootLogger().setLevel(Level.DEBUG);

        // setup environment
        MountainCarEnv env = new MountainCarEnv();
        
        // setup Q-Function
        QFeatureFunction Q = ObjectSerializer.load("data/mc-anrbf.Q");

        // setup policy
        GreedyPolicy pi = new GreedyPolicy(Q, MountainCarEnv.ACTION_SET);

        // create agent
        Agent agent = new Agent(pi);

        // experiment setups
        final int MAX_EPISODES = 10;
        final int MAX_STEPS    = 5000;

        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);

        // visualization (every 30 episodes for maximal 500 simulation steps)
        MountainCarVisualization viz = new MountainCarVisualization(agent, null, env, Mode.EPISODE, 1, MAX_STEPS);
        experiment.addObserver(viz);

	    // Initialize Q-Function 3D-Plotter
		QFunctionPlotter3D qPlotter = new QFunctionPlotter3D(Q);
		qPlotter.setBounds(
				new double[] { env.MIN_POS, env.MAX_POS}, 
				new double[] { env.MIN_VEL, env.MAX_VEL});
		qPlotter.setLabels("Position", "Velocity", "Costs");
		qPlotter.setFilename("mc-policy.svg");
		qPlotter.setTitle("Mountain-Car QFunction");
		qPlotter.setCosts(true);
		experiment.addObserver(new RuntimePlotter(qPlotter, Mode.EPISODE, 1, null));
		
		// Initialize Policy plotter
		PolicyPlotter3D policyPlotter = new PolicyPlotter3D(pi);
		policyPlotter.setBounds(
				new double[] { env.MIN_POS, env.MAX_POS}, 
				new double[] { env.MIN_VEL, env.MAX_VEL});
		policyPlotter.setLabels("Position", "Velocity", "Action");
		policyPlotter.setFilename("mc-policy.svg");
		policyPlotter.setTitle("Mountain-Car Policy");
		experiment.addObserver(new RuntimePlotter(policyPlotter, Mode.EPISODE, 1, null));

		// TrajectoryPlotter
		TrajectoryPlotter2d trajectoryPlotter = new TrajectoryPlotter2d();
		trajectoryPlotter.setLabel("Position", "Velocity");
		trajectoryPlotter.setTitle("MountainCar ANRBF trajectories");
		agent.addObserver(trajectoryPlotter); // observes step/episode updates
		experiment.addObserver(new RuntimePlotter(trajectoryPlotter, Mode.EPISODE, 1, null)); // calls plot() after each episode
				             
		
        // run experiment
        experiment.run();
    }
}
