package org.hswgt.teachingbox.core.rl.nfq.learner;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.env.TransitionSampleSARS;
import org.hswgt.teachingbox.core.rl.experiment.ParameterObserver;
import org.hswgt.teachingbox.core.rl.learner.Learner;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQEnvironment;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.Nfq;

/**
 * This is a MLP learner. A fixed set of transition tuples (s, a, r, sn) are loaded initially 
 * to the QLearningBatch. The Q-learning learning rule is performed in each NFQ 
 * iteration on all transition samples for learning the action values.  
 * @author tokicm
 */
public class OfflineNFQLearner extends NFQLearner implements Learner, Serializable, ParameterObserver {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 4360064230700782159L;

	// Logger
    private final static Logger log4j = Logger.getLogger("OfflineNFQLearner");

	protected int iGoalHintSamples = 0;	

	/**
	 * Constructor
	 * 
	 * @param NFQ The neural network Q-function object
	 * @param nfqEnv The NFQEnvironment object
	 * @param rpropEpochs The amount of RPROP epochs for each nfqIteration
	 */
	public OfflineNFQLearner(Nfq NFQ, NFQEnvironment nfqEnv, int rpropEpochs)
	{
		this.nfqEnv = nfqEnv;
		this.actionSet = nfqEnv.getActionSet();		
		this.Q = NFQ;
		this.transitionBatch = new QLearningBatch(NFQ, nfqEnv);
		this.trainEpochs = rpropEpochs;
	}	
	
	@Override
	public void updateNewEpisode(State initialState)
	{		
		// print out training progress
		if (R > Rbest) {
			Rbest = R; 
			RbestEpoch = iEpisode-1;
		}
		log4j.info ("Starting Q-Learning in episode " + iEpisode + ", R_lastEpisode=" + R + 
					", R_bestEpisode=" + Rbest + " (episode " + RbestEpoch + ")");
						
		int iNewGoalSamples = (int)(this.iTransitionSamples* ((double)goalHeuristicPercent/100.0)) - 
							iGoalHintSamples;
		this.iGoalHintSamples += iNewGoalSamples;
		for (int s=0; s<iNewGoalSamples; s++) {
			TransitionSampleSARS sars = nfqEnv.getGoalHeuristic();
			if (sars != null) {
				this.addTransition(sars.getState(), sars.getAction(), sars.getNextState(), sars.getReward());
			}
		}

		// iterate over all episodic experiences
		// do q-learning on all transition samples
		transitionBatch.qLearning(alpha, gamma);

		log4j.info("Start training of " + this.iTransitionSamples + " transition samples " + 
				"iNewGoalSamples: " + iNewGoalSamples + ", " + 
				"(total " + iGoalHintSamples + " goal hints)");
		
		Q.trainBatch(this.trainEpochs, transitionBatch.getQTable());

		this.iEpisode++;
				
		R = 0;		
		steps = 0;
	}

	@Override
	public void update(State state, Action action, State nextState,
			Action nextAction, double reward, boolean isTerminalState) {

		// Do nothing except of accumulating the episodic return + steps. 
		// Learning takes place at the end of an episode		
		R += reward;
		steps++;
	}
}
