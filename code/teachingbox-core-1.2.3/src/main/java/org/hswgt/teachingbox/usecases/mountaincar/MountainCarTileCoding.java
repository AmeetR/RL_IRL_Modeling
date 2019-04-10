package org.hswgt.teachingbox.usecases.mountaincar;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.MountainCarEnv;
import org.hswgt.teachingbox.core.rl.etrace.ETraceType;
import org.hswgt.teachingbox.core.rl.experiment.EpisodicSaver;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.learner.GradientDescentQLearner;
import org.hswgt.teachingbox.core.rl.network.Network;
import org.hswgt.teachingbox.core.rl.network.cmacs.TileCodingFactory;
import org.hswgt.teachingbox.core.rl.policy.GreedyPolicy;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;

/**
 * <pre>
 * Environment: MountainCar
 * Algorithm: Q-Learning
 * Approximation: Tile Coding with 10 10x10 tilings
 * </pre>
 */
public class MountainCarTileCoding
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

		// create Network
		Network net = new Network();

		// the configuration. {from, to, number if discs}
		double[][] config = new double[][] {
				{env.MIN_POS, env.MAX_POS, 10}, // position
				{env.MIN_VEL, env.MAX_VEL, 10}, // velocity
		};
		net.add(TileCodingFactory.createTilings(config, 1));
		net.setIsNormalized(true);
		
		// setup Q-Function
		QFeatureFunction Q = new QFeatureFunction(net, MountainCarEnv.ACTION_SET);

		// setup policy
		GreedyPolicy pi = new GreedyPolicy(Q, MountainCarEnv.ACTION_SET);

		// create agent
		Agent agent = new Agent(pi);

		// experiment setups
		final int MAX_EPISODES = 1000;
		final int MAX_STEPS    = 5000;
		final double alpha     = 0.4;
		final double gamma     = 1;
		final double lambda    = 0.9;

		// setup experiment
		Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);

		// setup learner
		GradientDescentQLearner learner = new GradientDescentQLearner(Q, net,
				MountainCarEnv.ACTION_SET);
		learner.setAlpha(alpha);
		learner.setGamma(gamma);
		learner.setLambda(lambda);
		learner.setETraceType(ETraceType.replacing);

		// attach learner to agent
		agent.addObserver(learner);

		// memorize Q function to file (for replay in ReplayMountainCarANRBF) every 100 episodes 
		experiment.addObserver(new EpisodicSaver("data/mc-tileCoding.Q", Q, 100));

		// run experiment
		experiment.run();
	}
}
