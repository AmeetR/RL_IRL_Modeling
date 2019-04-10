package org.hswgt.teachingbox.core.rl.nfq.util;

import java.io.Serializable;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver;
/**
 * this class averages the amount of steps per episode 
 * and the terminal state rate of an agent over multiple experiments.
 * @author tokicm
 *
 */
public class PolicyEvaluatorDataAverager implements ExperimentObserver, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4483458631293556480L;

	/**
	 * the episode counter
	 */
	private int iEpisode;

	/**
	 * the episodic step accumulator
	 */
	private double cumSteps;
	
	/**
	 * the goal state accumulator
	 */
	private double cumTermination;
	
	private double goalStateRate;
	private double stepRate;
	
	
	public void update(State arg0, Action arg1, State arg2, Action arg3,
			double arg4, boolean terminalState) {
		
		if (terminalState) {
			cumTermination++;
		}

		cumSteps++;
	}

	PolicyEvaluatorDataAverager() {
		cumSteps = 0.0;
		cumTermination = 0.0;
		iEpisode = 0;
		goalStateRate = 0.0;
		stepRate = 0.0;
	}

	
	public void reset() {
		cumSteps = 0.0;
		cumTermination = 0.0;
		iEpisode = 0;		
	}
	
	
	/**
	 * returns the rate of goal state terminations
	 * @return The goal-state rate
	 */
	public double getGoalStateRate() {
		//System.out.println ("cumTermination=" + cumTermination + ", iEpisode=" + iEpisode);
		return goalStateRate;
	}
	
	/**
	 * returns the average steps to finish an episode
	 * @return The average length of an episode
	 */
	public double getAverageEpisodeLength() {
		//System.out.println ("cumSteps=" + cumSteps + ", iEpisode=" + iEpisode);
		return stepRate;
	}
	
	public double getEpisodes() {
		return iEpisode;
	}
	
	public double getCumTermination() {
		return cumTermination;
	}
	

	@Override
	public void updateExperimentStart() {
		iEpisode++;
		
		goalStateRate = cumTermination / (double)iEpisode;
		stepRate = cumSteps / (double) iEpisode;
		
		//System.out.print(".");
	}

	@Override
	public void updateExperimentStop() {
		// correct episodes!!! 
		iEpisode--;
		
		goalStateRate = cumTermination / (double)iEpisode;
		stepRate = cumSteps / (double) iEpisode;
	}

	@Override
	public void updateNewEpisode(State initialState) {
		iEpisode++;
		
		goalStateRate = cumTermination / (double)iEpisode;
		stepRate = cumSteps / (double) iEpisode;
		
		//System.out.print(".");
	}
}
