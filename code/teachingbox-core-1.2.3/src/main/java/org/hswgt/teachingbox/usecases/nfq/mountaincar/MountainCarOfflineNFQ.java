package org.hswgt.teachingbox.usecases.nfq.mountaincar;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.experiment.EpisodicSaver;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQMountainCarEnv;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQMountainCarInputFeatures;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQMountainCarEnv.MC_DYNAMICS;
import org.hswgt.teachingbox.core.rl.nfq.learner.OfflineNFQLearner;
import org.hswgt.teachingbox.core.rl.nfq.util.PolicyEvaluator;
import org.hswgt.teachingbox.core.rl.nfq.util.PolicyEvaluator.STOP_CRITERION;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.encog.EncogNfqBackprop;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.encog.EncogNfqRprop;
import org.hswgt.teachingbox.core.rl.plot.PolicyPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.QFunctionPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.policy.GreedyPolicy;
import org.hswgt.teachingbox.core.rl.policy.RandomPolicy;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;

/**
 * This is a usecase for learning a Q-function represented by a multi-layer 
 * perceptron (MLP) within the mountain-car domain. The MLP is learned through 
 * the Encog library (http://www.heatonresearch.com/encog). 
 * 
 * Learning takes place in the "Neural Fitted Q-Iteration" (NFQ) framework 
 * as described in Riedmiller (2005) "Neural Fitted Q Iteration- First Experiences 
 * with a Data Efficient Neural Reinforcement Learning Method".
 * Transition samples are loaded from a file (using the SampleTransitions usecase), 
 * then trained episodically to the neural Q function. 
 * 
 * After each learning epoch a policy evaluation takes place, which 
 * evaluates the learned policy for 100 random starting states. If the 
 * evaluation agent finds a path to the goal within a maximum of 300 steps 
 * (for 100% of all fixed starting states), the experiment is terminated 
 * and the Q-function is saved to "data/piEval-NFQ-Mountaincar.Q". 
 * 
 * Use ReplayMountainCarNFQ for evaluating the learned Q-function!! 
 * 
 * @author tokicm
 *
 */
public class MountainCarOfflineNFQ {

	//public static MC_DYNAMICS mcDynamics = MC_DYNAMICS.RIEDMILLER; 	
	public static MC_DYNAMICS mcDynamics = MountainCarSampleTransitions.mcDynamics; 	
    public static int MAX_EPISODES = 100;

	// amount of RPROP epochs per NFQ iteration
	public static int NFQ_RPROP_EPOCHS = 300;
	
	// amount of test episodes	
	public static int TEST_EPISODES = 100; 
	
	// amount of maxSteps per test-episode
	public static int TEST_MAX_STEPS = 100;

    public static double alpha     = 0.1;
    public static double gamma     = 0.95;
    public static int SEED = 0;
    
    public static boolean visualization = false; 
    
	public static void main(String[] args) throws Exception {
		
		Logger.getRootLogger().setLevel(Level.DEBUG);

		// sample transitions for offline training
		MountainCarSampleTransitions.sampleTransitions();
        
		// setup environment: reward := -1.0 / MAX_STEPS
    	NFQMountainCarEnv env = new NFQMountainCarEnv(mcDynamics, true, -1);
    	env.setSeed(SEED);
    	NFQMountainCarInputFeatures features = new NFQMountainCarInputFeatures(env);
    	
    	// setup Q function        	
    	EncogNfqRprop Q = new EncogNfqRprop (
    				env.getActionSet(), features,
            		// one hidden layer having 20 neurons
            		new int[]{features.getNumInputFeatures(), 20, 1}
        );
    	Q.randomizeWeights(SEED);
    	Q.setEarlyStopping(10, 10);
    	        
        // create agent
        RandomPolicy pi = new RandomPolicy(env.getActionSet());
        Agent agent = new Agent(pi);
        
        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, 0);	        	      
        
	    // setup neural fitted learner
    	OfflineNFQLearner learner = new OfflineNFQLearner(Q, env, NFQ_RPROP_EPOCHS);
    	learner.setAlpha(alpha);
    	learner.setGamma(gamma);
    	// PARSE TRANSITIONS FROM FILE (DO NOT LEARN ONLINE!!)
		learner.importCSV("nfq-mc-transitions.txt", ";", new int[]{2, 3}, new int[]{4}, new int[]{5, 6}, 7, true);
		learner.setGoalHeuristicPercent(1);

        // attach learner to agent
        agent.addObserver(learner);	       

        // meta data averager for evaluating the learned poliyc (100 random start positions, maxSteps = 200, policy=greedy)
    	// do not terminate on bound violations (for measuring successful episodes)
    	NFQMountainCarEnv evalEnv = new NFQMountainCarEnv(mcDynamics, false);
        PolicyEvaluator piEval = new PolicyEvaluator(
        		new EpsilonGreedyPolicy (Q, env.getActionSet(), 0.0), 
        		evalEnv, experiment, 1, TEST_EPISODES, TEST_MAX_STEPS);
        // STOP experiment if all the agent found a path to the goal for all random starting states
        piEval.terminateOnGoalStateRate(STOP_CRITERION.HIGHER_OR_EQUAL_THAN, 1.0);
        piEval.set2dVisualization(false, null,  null);
        agent.addObserver(piEval);
        
        if (visualization) {
	        
            piEval.set2dVisualization(true, "Position", "Velocity");
        
	        QFunctionPlotter3D qPlotter = new QFunctionPlotter3D(Q);
			qPlotter.setBounds(
					new double[] { env.getMinPos(), env.getMaxPos()}, 
					new double[] { env.getMinVel(), env.getMaxVel()});
			qPlotter.setLabels("Position", "Velocity", "Action");
			qPlotter.setFilename("mc-policy.svg");
			qPlotter.setTitle("Mountain-Car V(s)-Function");
			experiment.addObserver(new RuntimePlotter(qPlotter, Mode.EPISODE, 1, null));
	        
			PolicyPlotter3D policyPlotter = new PolicyPlotter3D(pi);
			policyPlotter.setBounds(
					new double[] { env.getMinPos(), env.getMaxPos()}, 
					new double[] { env.getMinVel(), env.getMaxVel()});
			policyPlotter.setLabels("Position", "Velocity", "Action");
			policyPlotter.setFilename("mc-policy.svg");
			policyPlotter.setTitle("Mountain-Car Policy");
			experiment.addObserver(new RuntimePlotter(policyPlotter, Mode.EPISODE, 1, null));
        }
		
		//experiment.addObserver(new RuntimePlotter(learner, Mode.EPISODE, 1, null));
		experiment.addObserver(new RuntimePlotter(Q, Mode.EPISODE, 1, null));
		
        // RUN!		
		experiment.run();
		
		// RUN SIMULATION OF LEARNED POLICY
        ObjectSerializer.save(ReplayMountainCarNFQ.Q_FILENAME, Q);
		ReplayMountainCarNFQ.main(null);
	}
}
