package org.hswgt.teachingbox.usecases.thrift;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.MountainCarEnv;
import org.hswgt.teachingbox.core.rl.env.ThriftMountainCarEnv;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.learner.GradientDescentQLearner;
import org.hswgt.teachingbox.core.rl.network.Network;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.policy.GreedyPolicy;
import org.hswgt.teachingbox.core.rl.network.adaption.NoNodeNearby;
import org.hswgt.teachingbox.core.rl.network.rbf.RadialBasisFunction;
import org.hswgt.teachingbox.core.rl.network.rbf.adaption.RBFDistanceCalculator;
import org.hswgt.teachingbox.core.rl.thrift.ThriftEnvironmentClient;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;
import org.hswgt.teachingbox.core.rl.viz.mountaincar.MountainCarVisualization;

/**
 * <pre>
 * Environment: thriftMountainCar
 * Algorithm: Q-Learning
 * Approximation: Adaptive RBFNetwork
 * </pre>
 */
public class Client
{
    /**
     * @param args The command-line arguments
     * @throws Exception The Exception
     */
    public static void main(String[] args) throws Exception
    {
        Logger.getRootLogger().setLevel(Level.DEBUG);

        // setup environment
        ThriftEnvironmentClient env = new ThriftEnvironmentClient("localhost", 7911);

        // choose simga
        final double POS_STEP = (ThriftMountainCarEnv.MAX_POS - ThriftMountainCarEnv.MIN_POS)/50;
        final double VEL_STEP = (ThriftMountainCarEnv.MAX_VEL - ThriftMountainCarEnv.MIN_VEL)/50;
        final double[] sigma = new double[]{POS_STEP, VEL_STEP};

        // create adaptive network adding rbfs
        Network net = new Network(new NoNodeNearby(
                new RadialBasisFunction(sigma, sigma), new RBFDistanceCalculator()));
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
        final double alpha     = 1;
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

        // attach learner to agent
        agent.addObserver(learner);

        // visualization (every 10 episodes for maximal 500 simulation steps)
        MountainCarVisualization viz = 
        		new MountainCarVisualization(agent, learner, new MountainCarEnv(), 
                Mode.EPISODE, 10, 500);
        experiment.addObserver(viz);

        // run experiment
        experiment.run();
    }
}
