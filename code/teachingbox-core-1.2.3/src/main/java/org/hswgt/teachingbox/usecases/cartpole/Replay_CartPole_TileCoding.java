package org.hswgt.teachingbox.usecases.cartpole;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.CartPoleEnvironment;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;
import org.hswgt.teachingbox.core.rl.viz.pole.CartPole2dWindow;

/**
 * <pre>
 * Environment: Cart pole
 * Function: Replay of a previously learned policy of the cart pole task. The  
 * 			 function has been learned by Q-learning with TileCoding approximation (3* 6x6x6x6 tiles)  
 * 			 within ~520 episodes and a greedy policy.
 * 			 This use case can serve as a test bed for evaluating policy parameters.
 * Author: Michel Tokic 
 * </pre>
 */
public class Replay_CartPole_TileCoding {

	public static void main(String[] args) throws Exception
    {
        Logger.getRootLogger().setLevel(Level.DEBUG);

        
        QFeatureFunction Q = ObjectSerializer.load("data/cartpole/qlearning-tilecoding.Q");
        
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
        
        CartPole2dWindow window = new CartPole2dWindow("cart pole", 10);
        experiment.addObserver(window);
       
        // run experiment
        experiment.run();
    }
}
