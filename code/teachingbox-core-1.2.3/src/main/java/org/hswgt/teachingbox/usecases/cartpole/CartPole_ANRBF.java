package org.hswgt.teachingbox.usecases.cartpole;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.CartPoleEnvironment;
import org.hswgt.teachingbox.core.rl.etrace.ETraceType;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.learner.GradientDescentQLearner;
import org.hswgt.teachingbox.core.rl.network.Network;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.network.adaption.NoNodeNearby;
import org.hswgt.teachingbox.core.rl.network.rbf.RadialBasisFunction;
import org.hswgt.teachingbox.core.rl.network.rbf.adaption.RBFDistanceCalculator;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;
import org.hswgt.teachingbox.core.rl.viz.pole.CartPole2dWindow;
import org.hswgt.teachingbox.core.rl.viz.pole.CartPoleVisualization;

/**
 * <pre>
 * Environment: Cart Pole
 * Algorithm: Q-Learning
 * Approximation: Adaptive RBFNetwork
 * </pre>
 */
public class CartPole_ANRBF {
	public static void main(String[] args) throws Exception
    {
        Logger.getRootLogger().setLevel(Level.DEBUG);
        
        // Configure RBF network
        // choose sigma based on empirical measurements 
        // theta-bound=[-0.26455076435408115,0.2635074986966336] 
        // thetad-bound=[-3.1416352795027405,3.436925864539302] 
        // cartx-bound=[-1.0879061244855888,1.3884157001373418] 
        // cartxd-bound=[-3.1691498282497297,2.735797695191127]
        //String outputFilename = new String ("data/cartpole/qlearning-anrbf.Q");
        double sigmaQuotient = 15.0;
        final double[] sigma = new double[]{
        		0.6 / sigmaQuotient,
        		7 / sigmaQuotient,
        		3 / sigmaQuotient,
        		6.6 / sigmaQuotient
        };      	
        // create adaptive network adding rbfs
        Network net = new Network(new NoNodeNearby(
                new RadialBasisFunction(sigma, sigma),
                new RBFDistanceCalculator()));
        net.setIsNormalized(true);
        

        // setup Q-Function
        QFeatureFunction Q = new QFeatureFunction(net, CartPoleEnvironment.ACTION_SET);
        //QFeatureFunction Q = ObjectSerializer.load(outputFilename);
        
        // setup environment
        //PoleSwingupEnvironment env = new PoleSwingupEnvironment();
        CartPoleEnvironment env = new CartPoleEnvironment();
        
        // setup policy
        EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy(Q, CartPoleEnvironment.ACTION_SET, 0.0);
        //SoftmaxActionSelection pi = new SoftmaxActionSelection(Q, CartPoleEnvironment.ACTION_SET, 2.0);
        
        // create agent
        Agent agent = new Agent(pi);
        
        // experiment setups
        final int MAX_EPISODES = 20000;
        final int MAX_STEPS    = 10000;
        final double alpha     = 0.1;
        final double gamma     = 0.95;
        final double lambda    = 0.95;
                
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

        CartPoleVisualization viz = new CartPoleVisualization(agent, learner, Mode.EPISODE, MAX_EPISODES/10, MAX_STEPS);
        experiment.addObserver(viz);
        
        // run experiment
        experiment.run();

        // memorize Q function for replay (see @Replay_CartPole_ANRBF)
        ObjectSerializer.save("data/cartpole/qlearning-anrbf.Q", Q);
    }
}
