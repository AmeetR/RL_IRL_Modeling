package org.hswgt.teachingbox.usecases.rlglue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.MountainCarEnv;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.learner.GradientDescentSarsaLearner;
import org.hswgt.teachingbox.core.rl.network.Network;
import org.hswgt.teachingbox.core.rl.network.adaption.NoNodeNearby;
import org.hswgt.teachingbox.core.rl.network.rbf.RadialBasisFunction;
import org.hswgt.teachingbox.core.rl.network.rbf.adaption.RBFDistanceCalculator;
import org.hswgt.teachingbox.core.rl.plot.Plotter;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.rlglue.RLGlueRemoteEnvironment;
import org.hswgt.teachingbox.core.rl.rlglue.RlGlueAgent;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;
import org.hswgt.teachingbox.core.rl.valuefunctions.ValueFunctionEQ;
import org.hswgt.teachingbox.core.rl.viz.mountaincar.MountainCarVisualization;
import org.rlcommunity.rlglue.codec.RLGlue;
import org.rlcommunity.rlglue.codec.util.AgentLoader;


/**
 * This is an Agent/Experiment usecase for the MountainCar-Java implementation (the environment) from 
 * RL-Library. The agent uses Sarsa for learning the Q-function with radial basis function network (RBF) 
 * approximation [1]. The policy is e-greedy with 1% random action selection.   
 * 
 * This usecase also demonstrates the TB's plotting library where the Q-function is plotted
 * after each 20th episode. Furthermore the learned policy is visualized after each 20th episode.    
 *  
 * Steps for running this usecase: 
 *   1) follow the installation instructions of MountainCarJava from [2] 
 *   2) start  the MountainCar-Java environment
 *   3) type "rl_glue" in your command shell
 *   4) finally start this use-case. 
 *  
 *  [1] Sutton, R., Generalization in Reinforcement Learning: Successful Examples Using Sparse Coarse Coding, 
 *      In: Advances in Neural Information Processing Systems 8, pp. 1038-1044, MIT Press, 1996. 
 *  [2] http://code.google.com/p/rl-library/wiki/MountainCarJava
 *  
 * @author tokicm
 *
 */
public class MountainCar_SarsaRBF {
	public static void main(String[] args)
	{

		/* Log level*/		
	    Logger.getRootLogger().setLevel(Level.INFO);
	    
		/***************
		 *  Setup Agent
		 ***************/
		/* create RLGlue Agent to get task specification (needed by the learners Q-function approximator) */
		RlGlueAgent rlGlueAgent = new RlGlueAgent();
		// create an agentLoader that will start the agent when its run method is called
		AgentLoader agentLoader = new AgentLoader(rlGlueAgent);
		// create thread so that the agent and environment can run asynchronously 		
		Thread agentThread = new Thread(agentLoader);
		// start the thread
		agentThread.start();

	    String taskSpec = RLGlue.RL_init();
		System.out.println("Task-Specification: " + taskSpec);
		
		/****************************** 
		 * Configure RBF approximation 
		 ******************************/
		// BEGIN NORMALIZED RBF NETWORK
		// choose sigma
		final double POS_STEP = (1.2 - -0.6)/25;
        final double VEL_STEP = (0.07 - -0.07)/25;
        final double[] sigma = new double[]{POS_STEP, VEL_STEP};
      
        // create adaptive network adding rbfs
        Network net = new Network(new NoNodeNearby(
                new RadialBasisFunction(sigma, sigma),
                new RBFDistanceCalculator()));
        net.setIsNormalized(true);
        
        // setup Q-Function
        QFeatureFunction Q = new QFeatureFunction(net, rlGlueAgent.getTeachingboxActionSet());
        // END RBF
        
        
        
        /*****************************************
         * setup policy, learner & the TB's agent
         *****************************************/
		// the ActionSet for the policy is read from the rlGlueAgent (RL_init must have been called before!)
		EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy(Q, rlGlueAgent.getTeachingboxActionSet(), 0.01);
		System.out.println ("POLICY-LEARNER ActionSet: " + rlGlueAgent.getTeachingboxActionSet());
		GradientDescentSarsaLearner learner = new GradientDescentSarsaLearner (Q, net, rlGlueAgent.getTeachingboxActionSet());
		learner.setAlpha(0.5);
		learner.setGamma(1.0);
		learner.setLambda(0.9);
		Agent tbAgent = new Agent(pi);		
		tbAgent.addObserver(learner);
		
		/* SET THE TEACHINGBOX-AGENT IN THE RL-GLUE-AGENT-ADAPTER */		
		rlGlueAgent.setTeachingBoxAgent(tbAgent);
		
		/*********************************
		 *  Setup Experiment and Plotting
		 *********************************/
		RLGlueRemoteEnvironment rlEnv = new RLGlueRemoteEnvironment();				
		// configure experiment with 100 episodes and maximum 3000 steps/episode 
		Experiment experiment = new Experiment(tbAgent, rlEnv, 100, 3000);
				
        // CAR VISUALIZATION AFTER EACH 20th EPISODE WITH A MAXIMUM OF 500 STEPS
        MountainCarVisualization viz = new MountainCarVisualization(tbAgent,
                learner, new MountainCarEnv(), Mode.EPISODE, 20, 500);
        experiment.addObserver(viz);
		
		// RUN THE EXPERIMENT		
		experiment.run();
		
		// cleanup rl-glue at the end
		RLGlue.RL_cleanup();
	
		System.exit(1);
	}

}
