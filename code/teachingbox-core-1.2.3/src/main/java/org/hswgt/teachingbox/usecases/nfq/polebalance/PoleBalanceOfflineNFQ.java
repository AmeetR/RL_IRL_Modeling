package org.hswgt.teachingbox.usecases.nfq.polebalance;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.PoleBalanceEnvironment;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQPoleBalanceEnvironment;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQPoleBalanceInputFeatures;
import org.hswgt.teachingbox.core.rl.nfq.learner.OfflineNFQLearner;
import org.hswgt.teachingbox.core.rl.nfq.util.PolicyEvaluator;
import org.hswgt.teachingbox.core.rl.nfq.util.PolicyEvaluator.STOP_CRITERION;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.encog.EncogNfqRprop;
import org.hswgt.teachingbox.core.rl.plot.PolicyPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.QFunctionPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.policy.GreedyPolicy;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;

public class PoleBalanceOfflineNFQ {

    public static int MAX_EPISODES = 1000;

	// amount of RPROP epochs per NFQ iteration
	public static int NFQ_RPROP_EPOCHS = 1000;
	
	// amount of test episodes	
	public static int TEST_EPISODES = 100; 
	
	// amount of maxSteps per test-episode
	public static int TEST_MAX_STEPS = 1000;

    public static double alpha     = 0.7;
    public static double gamma     = 0.95;
    public static int SEED = 2;
    public static final boolean visualization = false; 

	public static void main(String[] args) throws Exception {
		
		Logger.getRootLogger().setLevel(Level.DEBUG);
		
		// sample transitions
		PoleBalanceSampleTransitions.sampleTransitions();
		
        // setup environment: reward := -1.0 / MAX_STEPS
		NFQPoleBalanceEnvironment env = new NFQPoleBalanceEnvironment();
		env.setSeed(SEED);
		NFQPoleBalanceInputFeatures features = new NFQPoleBalanceInputFeatures();
    	
    	// setup Q function        	
    	EncogNfqRprop Q = new EncogNfqRprop(
    	//EncogNfqBackprop Q = new EncogNfqBackprop (
            		env.getActionSet(), features,
            		// one hidden layer having 20 neurons
            		new int[]{features.getNumInputFeatures(), 20, 1}
        );
    	//Q.setLearningRate(0.05);
    	//Q.setMomentum(0.01);
    	Q.setBatchSize(25);
    	Q.randomizeWeights(SEED);
    	
    	// maximize speed by learning on all available CPU cores
    	Q.setThreadCount(Runtime.getRuntime().availableProcessors());

    	// early stopping
    	Q.setEarlyStopping(10, 10);
        
        // create agent
        GreedyPolicy pi = new GreedyPolicy(Q, env.getActionSet());
        Agent agent = new Agent(pi);
        
        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, 0);	        	      
        
	    // setup neural fitted learner
    	OfflineNFQLearner learner = new OfflineNFQLearner(Q, env, NFQ_RPROP_EPOCHS);
    	learner.setAlpha(alpha);
    	learner.setGamma(gamma);
    	// PARSE TRANSITIONS FROM FILE (DO NOT LEARN ONLINE!!)
		learner.importCSV(PoleBalanceSampleTransitions.FILENAME, ";", new int[]{2, 3}, new int[]{4}, new int[]{5, 6}, 7, true);
		learner.setGoalHeuristicPercent(1);
		
        // attach learner to agent
        agent.addObserver(learner);	       
        	        
        // meta data averager for evaluating the learned poliyc (100 random start positions, maxSteps = 200, policy=greedy)
    	// do not terminate on bound violations (for measuring successful episodes)
        NFQPoleBalanceEnvironment evalEnv = new NFQPoleBalanceEnvironment();
        PolicyEvaluator piEval = new PolicyEvaluator(
        		new EpsilonGreedyPolicy (Q, env.getActionSet(), 0.01), 
        		evalEnv, experiment, 1, TEST_EPISODES, TEST_MAX_STEPS);
        
        // STOP experiment if all the agent found a path to the goal for all random starting states
        piEval.terminateOnGoalStateRate(STOP_CRITERION.LOWER_OR_EQUAL_THAN, 0.02);
        piEval.set2dVisualization(false, null, null);
        agent.addObserver(piEval);
        
        if (visualization) {
		    piEval.set2dVisualization(true, "Position", "Velocity");
		    
		    QFunctionPlotter3D qPlotter = new QFunctionPlotter3D(Q);
			qPlotter.setBounds(
					new double[] { PoleBalanceEnvironment.MIN_POS, PoleBalanceEnvironment.MAX_POS}, 
					new double[] { PoleBalanceEnvironment.MIN_VEL, PoleBalanceEnvironment.MAX_VEL});
			qPlotter.setLabels("Position", "Velocity", "Action");
			qPlotter.setFilename("pb-policy.svg");
			qPlotter.setTitle("Pole-Balance V(s)-Function");
			experiment.addObserver(new RuntimePlotter(qPlotter, Mode.EPISODE, 1, null));
		    
		
			PolicyPlotter3D policyPlotter = new PolicyPlotter3D(pi);
			policyPlotter.setBounds(
					new double[] { PoleBalanceEnvironment.MIN_POS, PoleBalanceEnvironment.MAX_POS}, 
					new double[] { PoleBalanceEnvironment.MIN_VEL, PoleBalanceEnvironment.MAX_VEL});
			policyPlotter.setLabels("Position", "Velocity", "Action");
			policyPlotter.setFilename("pb-policy.svg");
			policyPlotter.setTitle("Pole-Balance Policy");
			experiment.addObserver(new RuntimePlotter(policyPlotter, Mode.EPISODE, 1, null));
        }

        // debug Q-function every episode
		experiment.addObserver(new RuntimePlotter(Q, Mode.EPISODE, 1, null));

        // RUN!
        experiment.run();        
        // RUN SIMULATION OF LEARNED POLICY
        ObjectSerializer.save(ReplayPoleBalanceNFQ.Q_FILENAME, Q);
        ReplayPoleBalanceNFQ.main(null);
	}
}
