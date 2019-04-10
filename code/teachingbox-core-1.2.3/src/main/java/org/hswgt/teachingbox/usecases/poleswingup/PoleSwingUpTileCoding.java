package org.hswgt.teachingbox.usecases.poleswingup;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.PoleSwingupEnvironment;
import org.hswgt.teachingbox.core.rl.etrace.ETraceType;
import org.hswgt.teachingbox.core.rl.experiment.EpisodicSaver;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.learner.GradientDescentQLearner;
import org.hswgt.teachingbox.core.rl.network.Network;
import org.hswgt.teachingbox.core.rl.network.cmacs.TileCodingFactory;
import org.hswgt.teachingbox.core.rl.plot.QFunctionPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;
import org.hswgt.teachingbox.core.rl.viz.pole.PoleSwingUpVisualization;

/**
 * <pre>
 * Environment: Pole swing-up
 * Algorithm: Q-Learning
 * Approximation: Tile Coding
 * </pre>
 */
public class PoleSwingUpTileCoding {
	
    public static void main(String[] args) throws Exception
    {
        Logger.getRootLogger().setLevel(Level.DEBUG);

	    // create Network
	    Network net = new Network();
        	
	    // the configuration. {from, to, number if discs}
	    double[][] config = new double[][] {
	            {PoleSwingupEnvironment.MIN_POS, PoleSwingupEnvironment.MAX_POS, 10}, // position
	            {PoleSwingupEnvironment.MIN_VEL, PoleSwingupEnvironment.MAX_VEL, 10}, // velocity
	    };
	    net.add(TileCodingFactory.createTilings(config, 10));
	

	    // setup Q-Function
	    QFeatureFunction Q = new QFeatureFunction(net, PoleSwingupEnvironment.ACTION_SET);
	
	    // setup environment
	    PoleSwingupEnvironment env = new PoleSwingupEnvironment();
	
	    // setup policy with a little bit of exploration
	    EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy(Q, PoleSwingupEnvironment.ACTION_SET,0.05);
	
	    // create agent
	    Agent agent = new Agent(pi);
	
	    // experiment setups
	    final int MAX_EPISODES = 1000;
	    final int MAX_STEPS    = 1000;
	    final int PLOT_EPISODES = 20;
	    final double alpha     = 0.1;
	    final double gamma     = 0.9;
	    final double lambda    = 0.9;
	    
	
	    // setup experiment
	    Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);
	
	    // setup learner
	    GradientDescentQLearner learner = new GradientDescentQLearner(Q, net,
                PoleSwingupEnvironment.ACTION_SET);
	    learner.setAlpha(alpha);
	    learner.setGamma(gamma);
	    learner.setLambda(lambda);
	    learner.setETraceType(ETraceType.replacing);
	
	    // attach learner to agent
	    agent.addObserver(learner);
	    
	    PoleSwingUpVisualization viz = new PoleSwingUpVisualization(agent,
                    learner, Mode.EPISODE, PLOT_EPISODES, MAX_STEPS);
	    viz.setDelayTime(5);
	    experiment.addObserver(viz);
	
	    // Initialize Q-Function 3D-Plotter
		QFunctionPlotter3D qPlotter = new QFunctionPlotter3D(Q);
		qPlotter.setBounds(
				new double[] { PoleSwingupEnvironment.MIN_POS, PoleSwingupEnvironment.MAX_POS}, 
				new double[] { PoleSwingupEnvironment.MIN_VEL, PoleSwingupEnvironment.MAX_VEL});
		qPlotter.setLabels("Position", "Velocity", "Q");
		qPlotter.setTitle("PoleSwingUp QFunction TileCoding");
		experiment.addObserver(new RuntimePlotter(qPlotter, Mode.EPISODE, PLOT_EPISODES, null));
		
		// memorize Q function to file (for replay in Replay_MountainCarANRBF) every 20 episodes 
		experiment.addObserver(new EpisodicSaver("data/pole-swingup-tileCoding.Q", Q, PLOT_EPISODES));

	    
	    // run experiment
	    experiment.run();
    }

}
