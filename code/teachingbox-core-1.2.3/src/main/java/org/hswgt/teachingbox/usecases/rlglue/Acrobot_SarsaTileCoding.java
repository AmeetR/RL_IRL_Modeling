package org.hswgt.teachingbox.usecases.rlglue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.learner.GradientDescentSarsaLearner;
import org.hswgt.teachingbox.core.rl.network.Network;
import org.hswgt.teachingbox.core.rl.network.cmacs.TileCodingFactory;
import org.hswgt.teachingbox.core.rl.network.cmacs.optimization.TileAndIndexBoundingBoxCalculator;
import org.hswgt.teachingbox.core.rl.network.optimization.GridHashFeatureGenerator;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.rlglue.RLGlueRemoteEnvironment;
import org.hswgt.teachingbox.core.rl.rlglue.RlGlueAgent;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;
import org.rlcommunity.rlglue.codec.RLGlue;
import org.rlcommunity.rlglue.codec.util.AgentLoader;


/**
 * This is an Agent/Experiment usecase for the Acrobot-Java implementation (the environment) from 
 * RL-Library. The agent uses Sarsa for learning the Q-function with tile-coding approximation [1]. 
 * The policy is e-greedy with 1% random action selection.   
 * 
 * Steps for running this usecase: 
 *   1) follow the installation instructions of Acrobot from [2] 
 *   2) start  the Acrobot-Java environment
 *   3) type "rl_glue" in your command shell
 *   4) finally start this use-case. 
 *  
 *  [1] Sutton, R., Generalization in Reinforcement Learning: Successful Examples Using Sparse Coarse Coding, 
 *      In: Advances in Neural Information Processing Systems 8, pp. 1038-1044, MIT Press, 1996. 
 *  [2] http://library.rl-community.org/wiki/Acrobot_(Java)
 *  
 * @author tokicm
 *
 */
public class Acrobot_SarsaTileCoding {
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
		

		/************************************** 
		 * Configure Tile-Coding approximation 
		 **************************************/
	    // BEGIN TILE CODING APPROXIMATION
        // the number of tilings/layers
        int nTilings = 48;
        
        // the configuration. {from, to, number if discs}
        double[][] config = new double[][] {
                {-Math.PI, Math.PI, 6},
                {-Math.PI, Math.PI, 6},
                {-12.57, 12.57, 6},
                {-28.28, 28.28, 6}                
        };
        
        // create square tilings
        Network net = new Network();
        net.setIsNormalized(true);
        double[][] optimizationConfig = config.clone();
        net.setFeatureGenerator(new GridHashFeatureGenerator(
            optimizationConfig, new TileAndIndexBoundingBoxCalculator()));
        net.add(TileCodingFactory.createTilings(config, nTilings));
        
        // setup Q-Function
        QFeatureFunction Q = new QFeatureFunction(net, rlGlueAgent.getTeachingboxActionSet());
        // END TILE-CODING
        
        
        
        
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
		
		// RUN THE EXPERIMENT		
		experiment.run();
		
		// cleanup rl-glue at the end
		RLGlue.RL_cleanup();
	
		System.exit(1);
	}

}
