package org.hswgt.teachingbox.usecases.narmedbandit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.NArmedBanditEnv;
import org.hswgt.teachingbox.core.rl.etrace.ETraceType;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.experiment.RewardAverager;
import org.hswgt.teachingbox.core.rl.learner.TabularQLearner;
import org.hswgt.teachingbox.core.rl.learner.stepsize.SampleAverageAlpha;
import org.hswgt.teachingbox.core.rl.plot.DataAveragePlotter;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.policy.Policy;
import org.hswgt.teachingbox.core.rl.tabular.HashQFunction;

/**
 * A use-case for the n-armed bandit problem.
 *  - Exact reproduction of the "10-armed testbed" from 
 *   (Sutton and Barto, Reinforcement Learning: An introduction, p. 28-30) 
 *  
 * @author Michel Tokic
 */
public class NArmedBandit {
	
	static int LEVERS = 10;
	static int EPISODES = 1; // because we average over experiments
	static int EXPERIMENTS = 2000;
	static int STEPS = 1000;
	static double epsilons[] = new double[] {0.0, 0.01, 0.1, 0.5, 1.0};

	public static void main(String[] args) throws Exception   {
		
		Logger.getRootLogger().setLevel(Level.OFF);
		
		System.out.println ("starting multi-armed bandit experiment");
		DataAveragePlotter dataPlotter = new DataAveragePlotter("bandit.png", "nArmedBandit results - EGreedyPolicy");
		dataPlotter.setErrorPlotting(100);
		
		// vary exploration parameter
		for (double epsilon : epsilons) {
			
			// configure plotter
			RewardAverager ra = new RewardAverager(STEPS, "epsilon=" + epsilon);
		    dataPlotter.addScalarAverager(ra);
	        dataPlotter.setTics(
	        		new double[]{0, 200, STEPS},  
					new double[]{0.0, 0.1, 1.5}
	        );  
	        dataPlotter.setLabel("Steps", "Average Reward");
	        
	        
	        // create instance of environment (experiment runs slowly, 
	        // if the environment is instantiated in the 
	        // subsequent for-loop) 
	        NArmedBanditEnv env = new NArmedBanditEnv(LEVERS);
	        
	        // average results over experiments (because of reinitializing the value function)
			for (int e=0; e<EXPERIMENTS; e++) {
				
				// reinitialize bandit levers
				env.initRandom();
				
				// configure Q-function and policy
				HashQFunction Q = new HashQFunction(0, NArmedBanditEnv.ACTION_SET);
				Policy pi = new EpsilonGreedyPolicy (Q, NArmedBanditEnv.ACTION_SET, epsilon);
				
				// configure Agent
				Agent agent = new Agent (pi);

				// configure learner
				TabularQLearner learner = new TabularQLearner(Q); 
				learner.setStepSizeCalculator(new SampleAverageAlpha());
				learner.setGamma(0.0);
				learner.setEtraceType(ETraceType.none);
				agent.addObserver(learner);
				
				// configure experiment
				Experiment experiment = new Experiment (agent, env, EPISODES, STEPS);
				
				// add result plotter to experiment 
				experiment.addObserver(ra);
				experiment.run();				
				
				//ra.setConfigString("epsilon=" + epsilon + ", averages=" + (e+1) + " (of " + EXPERIMENTS + ")");
				dataPlotter.setVisible(true);
				dataPlotter.plotGraph();
			}
			
			// export PNG after running each exploration parameter
			dataPlotter.exportPNG("bandit-result.png");
		} 	
	}
}
