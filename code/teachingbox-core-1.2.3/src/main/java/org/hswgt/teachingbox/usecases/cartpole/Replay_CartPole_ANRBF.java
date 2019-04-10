package org.hswgt.teachingbox.usecases.cartpole;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.CartPoleEnvironment;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;
import org.hswgt.teachingbox.core.rl.viz.pole.CartPole2dWindow;
import org.hswgt.teachingbox.core.rl.viz.pole.CartPoleVisualization;


/**
 * <pre>
 * Environment: Cart pole
 * Function: Replay of a learned policy from . The  
 * 			 function has been learned by Q-learning with adaptive ANRBF approximation  
 * 			 within 480 episodes and a greedy policy.
 * 			 This use case can serve as a test bed for evaluating policy parameters.
 * Author: Michel Tokic 
 * </pre>
 */
public class Replay_CartPole_ANRBF {

	public static void main(String[] args) throws Exception
    {
        Logger.getRootLogger().setLevel(Level.DEBUG);

        
        QFeatureFunction Q = ObjectSerializer.load("data/cartpole/qlearning-anrbf.Q");
        
        // setup environment
        //PoleSwingupEnvironment env = new PoleSwingupEnvironment();
        CartPoleEnvironment env = new CartPoleEnvironment();
        
        // setup policy
        EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy(Q, CartPoleEnvironment.ACTION_SET, 0.0);
        //SoftmaxActionSelection pi = new SoftmaxActionSelection(Q, CartPoleEnvironment.ACTION_SET, 2.0);
        
        // create agent
        Agent agent = new Agent(pi);
        
        // experiment setups
        final int MAX_EPISODES = 3;
        final int MAX_STEPS    = 10000;

        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);
        
        CartPoleVisualization viz = new CartPoleVisualization(agent, null, Mode.EPISODE, 1, MAX_STEPS);
        experiment.addObserver(viz);
        
        // run experiment
        experiment.run();
    }
}
