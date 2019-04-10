/**
 *
 * $Id: PoleSwingUpANRBF.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

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
import org.hswgt.teachingbox.core.rl.plot.QFunctionPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.network.adaption.NoNodeNearby;
import org.hswgt.teachingbox.core.rl.network.rbf.RadialBasisFunction;
import org.hswgt.teachingbox.core.rl.network.rbf.adaption.RBFDistanceCalculator;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;



/**
 * <pre>
 * Environment: Pole swing-up
 * Algorithm: Q-Learning
 * Approximation: Adaptive RBFNetwork
 * </pre>
 */
public class PoleSwingUpANRBF
{
	public static void main(String[] args) throws Exception
	{
		Logger.getRootLogger().setLevel(Level.DEBUG);


		// setup environment
		PoleSwingupEnvironment env = new PoleSwingupEnvironment();
		
		// choose sigma
		final double[] sigma = new double[]{
				(PoleSwingupEnvironment.MAX_POS - PoleSwingupEnvironment.MIN_POS)/50,
				(PoleSwingupEnvironment.MAX_VEL - PoleSwingupEnvironment.MIN_VEL)/50
		};

		// create adaptive network adding rbfs
		Network net = new Network(new NoNodeNearby(
				new RadialBasisFunction(sigma, sigma),
				new RBFDistanceCalculator()));
		net.setIsNormalized(true);

		// setup Q-Function
		QFeatureFunction Q = new QFeatureFunction(net, PoleSwingupEnvironment.ACTION_SET);

		// setup policy with a little bit of exploration
		EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy(Q, PoleSwingupEnvironment.ACTION_SET, 0.00);

		// create agent
		Agent agent = new Agent(pi);

		// experiment setups
		final int MAX_EPISODES = 1000;
		final int MAX_STEPS    = 1000;
		final int PLOT_EPISODES = 10;
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

		// memorize Q function to file (for replay in Replay_MountainCarANRBF) every 20 episodes 
		experiment.addObserver(new EpisodicSaver("data/pole-swingup-anrbf.Q", Q, PLOT_EPISODES));

	    // Initialize Q-Function 3D-Plotter
		QFunctionPlotter3D qPlotter = new QFunctionPlotter3D(Q);
		qPlotter.setBounds(
				new double[] { PoleSwingupEnvironment.MIN_POS, PoleSwingupEnvironment.MAX_POS}, 
				new double[] { PoleSwingupEnvironment.MIN_VEL, PoleSwingupEnvironment.MAX_VEL});
		qPlotter.setLabels("Position", "Velocity", "Q");
		qPlotter.setTitle("PoleSwingUp QFunction ANRBF");
		experiment.addObserver(new RuntimePlotter(qPlotter, Mode.EPISODE, PLOT_EPISODES, null));
		
		
		// run experiment
		experiment.run();

	}
}
