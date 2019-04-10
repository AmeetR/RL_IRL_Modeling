package org.hswgt.teachingbox.usecases.nfq.polebalance;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.PoleSwingupEnvironment;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQPoleBalanceEnvironment;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQPoleBalanceInputFeatures;
import org.hswgt.teachingbox.core.rl.nfq.learner.OnlineNFQLearner;
import org.hswgt.teachingbox.core.rl.nfq.util.PolicyEvaluator;
import org.hswgt.teachingbox.core.rl.nfq.util.PolicyEvaluator.STOP_CRITERION;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.Nfq;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.encog.EncogNfqBackprop;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.encog.EncogNfqRprop;
import org.hswgt.teachingbox.core.rl.plot.PolicyPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.QFunctionPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.policy.GreedyPolicy;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;

/**
 * This is a usecase for learning a Q-function represented by a multi-layer 
 * perceptron (MLP) within the pole-balance domain. The MLP is learned through 
 * the Encog library (http://www.heatonresearch.com/encog). 
 * 
 * Learning takes place in the "Neural Fitted Q-Iteration" (NFQ) framework 
 * as described in Riedmiller (2005) "Neural Fitted Q Iteration- First Experiences 
 * with a Data Efficient Neural Reinforcement Learning Method".
 * Transition samples are collected and added to an transition batch, which is trained 
 * with the Q-learning rule after the end of each episode. After the Q-learning 
 * rule was performed for all transition samples, the resulting Q-function 
 * is trained to the MLP.
 * 
 * After each learning episode a policy evaluation takes place, which 
 * evaluates the learned policy for random starting states. If the 
 * evaluation agent manages to balance the pole for a minimum of 100 steps, 
 * the experiment is terminated and the 
 * Q-function is saved to "data/piEval-NFQ-PoleBalance.Q" for replaying. 
 * 
 * @author tokicm
 *
 */
public class PoleBalanceOnlineNFQ {
	
    
    // experiment setups
	public static int MAX_EPISODES = 1000;
    public static int MAX_STEPS    = 20;
	
	// start NFQ training after n episodes
	public static int NFQ_SAMPLE_EPISODES = 1;
	
	// amount of NFQ iterations
	public static int NFQ_ITERATIONS= 1;
	
	// amount of RPROP epochs per NFQ iteration
	public static int NFQ_RPROP_EPOCHS = 100;
	
	// amount of test episodes
	public static int TEST_EPISODES = 100; 
	
	// amount of maxSteps per test-episode
	public static int TEST_MAX_STEPS = 100;


    public final static double alpha     = 0.7;
    public final static double gamma     = 0.95; // because of continuous environment

    public final static long SEED = 2;
    
    public final static boolean visualization = false;

	public static void main(String[] args) throws Exception
	{
	
		Logger.getRootLogger().setLevel(Level.DEBUG);
		
        // setup environment        	
		NFQPoleBalanceEnvironment env = new NFQPoleBalanceEnvironment();
		env.setSeed(SEED);
		NFQPoleBalanceInputFeatures features = new NFQPoleBalanceInputFeatures();
		
		// setup neural Q function with one hidden layer having 20 neurons
    	EncogNfqBackprop Q = new EncogNfqBackprop(PoleSwingupEnvironment.ACTION_SET, features, 
            		new int[]{features.getNumInputFeatures(), 20, 1});
    	Q.randomizeWeights(SEED);
    	Q.setEarlyStopping(25, 10);
    	Q.setBatchSize(25);
    	Q.setLearningRate(0.01);
    	Q.setMomentum(0.0);
    	
    	// maximize speed by learning on all available CPU cores
    	Q.setThreadCount(Runtime.getRuntime().availableProcessors());
        
        // setup policy: sample dynamics with random actions
    	EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy (Q, PoleSwingupEnvironment.ACTION_SET, 1.0);
    	pi.setSeed(SEED);
         
        // create agent
        Agent agent = new Agent(pi);
    
        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);	        	      
        
        // setup neural fitted learner
    	OnlineNFQLearner learner = new OnlineNFQLearner((Nfq)Q, env, 
    			NFQ_SAMPLE_EPISODES, NFQ_ITERATIONS, NFQ_RPROP_EPOCHS);
    	learner.setAlpha(alpha);
    	learner.setGamma(gamma);
		learner.setGoalHeuristicPercent(10);
        		        
        // attach learner to agent
        agent.addObserver(learner);	       
        

	    // meta data averager (100 random start positions, maxSteps = 200)
    	NFQPoleBalanceEnvironment evalEnv = new NFQPoleBalanceEnvironment();
        PolicyEvaluator piEval = new PolicyEvaluator( 
        		new EpsilonGreedyPolicy (Q, PoleSwingupEnvironment.ACTION_SET, 0.01),  
        		evalEnv, experiment, NFQ_SAMPLE_EPISODES, TEST_EPISODES, TEST_MAX_STEPS);
        //piEval.setInitState(startState);
        piEval.terminateOnGoalStateRate(STOP_CRITERION.LOWER_OR_EQUAL_THAN, 0.02);
        piEval.set2dVisualization(false, null, null);
        agent.addObserver(piEval);
        
        if (visualization) {
            QFunctionPlotter3D qPlotter = new QFunctionPlotter3D(Q);
    		qPlotter.setBounds(
    				new double[] { NFQPoleBalanceEnvironment.MIN_POS, NFQPoleBalanceEnvironment.MAX_POS}, 
    				new double[] { NFQPoleBalanceEnvironment.MIN_VEL, NFQPoleBalanceEnvironment.MAX_VEL});
    		qPlotter.setLabels("Position", "Velocity", "Action");
    		qPlotter.setFilename("mc-policy.svg");
    		qPlotter.setTitle("Pole-Balancing V(s)-Function");
    		experiment.addObserver(new RuntimePlotter(qPlotter, Mode.EPISODE, 1, null));
            

    		PolicyPlotter3D policyPlotter = new PolicyPlotter3D(pi);
    		policyPlotter.setBounds(
    				new double[] { NFQPoleBalanceEnvironment.MIN_POS, NFQPoleBalanceEnvironment.MAX_POS}, 
    				new double[] { NFQPoleBalanceEnvironment.MIN_VEL, NFQPoleBalanceEnvironment.MAX_VEL});
    		policyPlotter.setLabels("Position", "Velocity", "Action");
    		policyPlotter.setFilename("mc-policy.svg");
    		policyPlotter.setTitle("Pole-Balancing NFQ Policy");
    		//policyPlotter.setView(0.01, 0.01);
    		experiment.addObserver(new RuntimePlotter(policyPlotter, Mode.EPISODE, 1, null));
        }

        experiment.run();
        
        // RUN SIMULATION OF LEARNED POLICY
        ObjectSerializer.save(ReplayPoleBalanceNFQ.Q_FILENAME, Q);
        ReplayPoleBalanceNFQ.main(null);
	}
}
