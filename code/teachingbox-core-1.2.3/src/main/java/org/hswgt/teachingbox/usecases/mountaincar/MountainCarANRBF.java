
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
import org.hswgt.teachingbox.core.rl.network.adaption.NoNodeNearby;
import org.hswgt.teachingbox.core.rl.network.rbf.RadialBasisFunction;
import org.hswgt.teachingbox.core.rl.network.rbf.adaption.RBFDistanceCalculator;
import org.hswgt.teachingbox.core.rl.policy.GreedyPolicy;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;

/**
 * <pre>
 * Environment: MountainCar
 * Algorithm: Q-Learning
 * Approximation: Adaptive RBFNetwork
 * </pre>
 */
public class MountainCarANRBF
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

		// choose simga
		final double POS_STEP = (env.MAX_POS - env.MIN_POS)/50;
		final double VEL_STEP = (env.MAX_VEL - env.MIN_VEL)/50;
		final double[] sigma = new double[]{POS_STEP, VEL_STEP};

		// create adaptive network adding rbfs
		Network net = new Network(new NoNodeNearby(
				new RadialBasisFunction(sigma, sigma),
				new RBFDistanceCalculator()));
		net.setIsNormalized(true);

		// setup Q-Function
		QFeatureFunction Q = new QFeatureFunction(net, MountainCarEnv.ACTION_SET);
		//QFeatureFunction Q = ObjectSerializer.load("mc-anrbf.Q");

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

		// memorize Q function to file (for replay in Replay_MountainCarANRBF) every 100 episodes 
		experiment.addObserver(new EpisodicSaver("data/mc-anrbf.Q", Q, 100));

		// run experiment
		experiment.run();
	}
}
