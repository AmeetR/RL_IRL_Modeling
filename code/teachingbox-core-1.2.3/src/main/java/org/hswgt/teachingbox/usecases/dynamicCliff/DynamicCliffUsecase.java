package org.hswgt.teachingbox.usecases.dynamicCliff;

import org.hswgt.teachingbox.core.rl.env.DynamicCliffEnvironment;
import org.hswgt.teachingbox.core.rl.experiment.SimpleExperiment;



public class DynamicCliffUsecase {
	public static void main(String[] args) throws Exception   {   
	
		System.out.println ("starting dynamicCliff");
	
		args = new String[] {null, "dynamicCliff.properties", "reinforceVdbeSoftmax.properties"};
		SimpleExperiment se = new SimpleExperiment ( 
				args, "dynamicCliff",
				SimpleExperiment.ENVIRONMENT_DYNAMIC_CLIFF, 
				null,
				SimpleExperiment.PLOTTING_EPISODIC
		);
		//fe.setupExperiment (200, 3000, 300, 1.0, new double[]{0.21, 0.14, 0.29}, false);
		se.setInitState(DynamicCliffEnvironment.DEFAULT_INIT_STATE);
		
		// add extra plot parameters
		String plotParameters[]  = new String[] {
				DynamicCliffEnvironment.LAST_GOAL_1,
				DynamicCliffEnvironment.LAST_GOAL_2,
				DynamicCliffEnvironment.LAST_STEP,
				DynamicCliffEnvironment.LAST_WALL					
		};		
		se.setObservableEnvironmentParameters(plotParameters);
		
		// run Q-learning
		se.setupLearner(SimpleExperiment.LEARNER_QLEARNING);
		se.run();
		
		// Sarsa
		//se.setupLearner(SimpleExperiment.LEARNER_SARSA);
		//se.run();
		
		// dont quit immediately (allows for watching the plots)
		Thread.sleep(60000);
	}
}
