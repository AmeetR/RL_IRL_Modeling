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
 * This is a MLP learner. Transition tuples (s, a, r, sn) are accumulated in 
 * a QLearningBatch. The Q-learning learning rule is performed in each NFQ 
 * iteration on all transition samples for learning the action values.  
 * @author tokicm
 */
public class OnlineNFQLearner extends NFQLearner implements Learner, Serializable, ParameterObserver 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 686393875693434947L;
	
    // Logger
    private final static Logger log4j = Logger.getLogger("SingleBatchNFQLearner");
	
	protected int iGoalHintSamples = 0;	
	
	/**
	 * Constructor
	 * 
	 * @param NFQ The neural network Q-function object
	 * @param nfqEnv The NFQEnvironment object
	 * @param sampleEpisodes The amount of sample episodes. After every sampleEpisode, the batch of transition samples is trained.
	 * @param nfqIterations The amount of NFQ iterations after sampling "sampleEpisodes" episodes
	 * @param rpropEpochs The amount of RPROP epochs for each nfqIteration
	 */
	public OnlineNFQLearner(Nfq NFQ, NFQEnvironment nfqEnv, int sampleEpisodes, int nfqIterations, int rpropEpochs)
	{
		this.nfqEnv = nfqEnv;
		this.actionSet = nfqEnv.getActionSet();		
		this.Q = NFQ;
		this.sampleEpisodes = sampleEpisodes;
		this.nfqIterations = nfqIterations;
		this.trainEpochs = rpropEpochs;

		this.transitionBatch = new QLearningBatch(NFQ, nfqEnv);
	}

	@Override
	public void update(State state, Action action, State nextState,
			Action nextAction, double reward, boolean isTerminalState)
	{
		// accumulate reward
        R += reward;
        steps++;
        
        // put transition sample to the batch
        this.addTransition(state, nextAction, nextState, reward);
        this.iTransitionSamples++;
	}
	
	
	@Override
	public void updateNewEpisode(State initialState)
	{		
		if (iEpisode > 0) {
			
			log4j.info("episode " + iEpisode + " ended after " + steps + " transition samples. Return=" + R);
			
			// train all experiences after every sampleEpisodes
			if((iEpisode%sampleEpisodes) == 0) {
				
				log4j.debug ("starting NFQ training");
				
				for (int i=0; i<nfqIterations; i++) {

					// add goal heuristics 
					int iNewGoalSamples = (int)(iTransitionSamples * ((double)goalHeuristicPercent/100.0)) - 
										iGoalHintSamples;
					
					iGoalHintSamples += iNewGoalSamples;
					for (int s=0; s<iNewGoalSamples; s++) {
						TransitionSampleSARS sars = nfqEnv.getGoalHeuristic();
						if (sars != null) {
							this.addTransition(sars.getState(), sars.getAction(), sars.getNextState(), sars.getReward());
						}
					}
					

					// iterate over all episodic experiences
					// do q-learning on all transition samples
					transitionBatch.qLearning(alpha, gamma);
					
					log4j.debug("Start training of " + this.iTransitionSamples + " transition samples " + 
								"iNewGoalSamples: " + iNewGoalSamples + ", " + 
								"(total " + iGoalHintSamples + " goal hints)");
					
					// start training
					log4j.debug("NFQ-Iteration: " + i + ", " +
							"transitionBatch.size: " + transitionBatch.size() + ", " +
							"Epochs: " + this.trainEpochs);
					
					Q.trainBatch(this.trainEpochs, transitionBatch.getQTable());
				}
				
				if (this.episodicTransitionBatchCleaning) {
					this.transitionBatch.clear();
					this.iTransitionSamples = 0;
				}
			}
	
			log4j.debug("Return of last episode: " + R);
		}

		this.iEpisode++;		
		R = 0;		
		steps = 0;
	}
}
