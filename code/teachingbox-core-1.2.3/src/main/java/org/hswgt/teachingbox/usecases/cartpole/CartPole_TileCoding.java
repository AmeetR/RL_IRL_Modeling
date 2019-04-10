package org.hswgt.teachingbox.usecases.cartpole;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.CartPoleEnvironment;
import org.hswgt.teachingbox.core.rl.etrace.ETraceType;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.learner.GradientDescentQLearner;
import org.hswgt.teachingbox.core.rl.network.Network;
import org.hswgt.teachingbox.core.rl.network.cmacs.TileCodingFactory;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;
import org.hswgt.teachingbox.core.rl.viz.pole.CartPoleVisualization;

public class CartPole_TileCoding {
	public static void main(String[] args) throws Exception
    {
        Logger.getRootLogger().setLevel(Level.DEBUG);
        
        // the configuration. {from, to, number if discs}
        int nTilings = 3;
        double[][] config = new double[][] {
                {-0.3, 0.3, 6}, // theta
                {-3.5, 3.5, 6}, // thetad 
                {-1.5, 1.5, 6}, // cartx
                {-3.3, 3.3, 6} // cartxd 
        };
        
        // create square tiling
        Network net = new Network();
        net.add(TileCodingFactory.createTilings(config, nTilings));
        
        // setup Q-Function
        QFeatureFunction Q = new QFeatureFunction(net, CartPoleEnvironment.ACTION_SET);
        //QFeatureFunction Q = ObjectSerializer.load(outputFilename);
        
        // setup environment
        CartPoleEnvironment env = new CartPoleEnvironment();
        
        // setup policy
        EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy(Q, CartPoleEnvironment.ACTION_SET, 0.1);
        
        // create agent
        Agent agent = new Agent(pi);
        
        // experiment setups
        final int MAX_EPISODES = 10000;
        final int MAX_STEPS    = 10000;
        final double alpha     = 0.1;
        final double gamma     = 0.9;
        final double lambda    = 0.9;
                
        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);
        
        // setup learner
        GradientDescentQLearner learner = new GradientDescentQLearner(Q, net,
                CartPoleEnvironment.ACTION_SET);
        learner.setAlpha(alpha);
        learner.setGamma(gamma);
        learner.setLambda(lambda);
        learner.setETraceType(ETraceType.replacing);
        
        // attach learner to agent
        agent.addObserver(learner);       

        // run experiment
        experiment.run();

        // memorize Q function for replay (see @Replay_CartPole_TileCoding)
        ObjectSerializer.save("data/cartpole/qlearning-tilecoding.Q", Q);
    }
}
