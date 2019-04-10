package org.hswgt.teachingbox.core.rl.policy.explorationrate;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.learner.stepsize.StepSizeCalculator;
import org.hswgt.teachingbox.core.rl.policy.explorationrate.stepsize.SampleAverageStepSize;

/**
 * @author twanschik Decreases epsilon using an running average of the
 *         cumulative reward of an episode. Higher cumulative reward averages
 *         produce less exploration whereas low averages result in more
 *         exploration. Note that this method only works if we know the biggest
 *         available reward since we have to normalize epsilon!It's as if the
 *         agent knows that he can do better so he keeps on exploring if the
 *         average cumulativ reward is low and stops exploring with bigger and
 *         bigger average cumulative rewards.
 * 
 *         A StepSizeCalculator can be used to define how to average ( sample
 *         averages, exponential averages, ...) Default is to use a sample
 *         average.
 */
public class AverageCumulativeRewardBasedEpsilonDecrease implements
		EpsilonCalculator {
	protected double maxCumulativeReward;
	private double minCommulativeReward;
	protected double averageCumulativeReward = 0;

	protected double cumulativeReward = 0;
	protected double maxExplorationRate = 1.0;
	// default is a constant stepsize calculator resulting in discounting
	// older observations more and more
	protected StepSizeCalculator stepSizeCalculator;
	protected int currentEpisode = 0;
	protected double epsilon;

	public AverageCumulativeRewardBasedEpsilonDecrease(
			double maxCommulativeReward) {
		this(maxCommulativeReward, 0.0, 1.0, new SampleAverageStepSize());
	}

	public AverageCumulativeRewardBasedEpsilonDecrease(
			double maxCommulativeReward, double minCommulativeReward) {
		this(maxCommulativeReward, minCommulativeReward, 1.0,
				new SampleAverageStepSize());
	}

	public AverageCumulativeRewardBasedEpsilonDecrease(
			double maxCommulativeReward, double minCommulativeReward,
			double maxExplorationRate, StepSizeCalculator stepSizeCalculator) {
		this.maxCumulativeReward = maxCommulativeReward;
		this.setMinCommulativeReward(minCommulativeReward);
		// the averageCumulativeReward has to be set to the minimal one at the
		// beginning
		// to ensure maximal exploration
		this.averageCumulativeReward = this.minCommulativeReward;
		this.cumulativeReward = 0;
		this.maxExplorationRate = maxExplorationRate;
		this.setStepSizeCalculator(stepSizeCalculator);
	}

	public double getEpsilon(State state) {
		return this.epsilon;
	}

	public void update(State state, Action action, State nextState,
			Action nextAction, double reward, boolean isTerminalState) {
		this.cumulativeReward += reward;
	}

	public void updateNewEpisode(State initialState) {
		// update the stepSizeCalculators value each episode instead of each
		// step because we deal with cumulative values.
		if (this.currentEpisode > 0) {
			this.stepSizeCalculator.update(initialState, null, initialState,
					null, cumulativeReward, true);
			this.averageCumulativeReward += this.stepSizeCalculator.getAlpha(
					initialState, null)
					* (this.cumulativeReward - this.averageCumulativeReward);
		}
		
		// update exploration
		// the higher the average the less we have to explore, normalize by
		// maximum cumulativ reward and to the maximum exploration rate
		this.epsilon = (1.0 - (this.averageCumulativeReward - this.minCommulativeReward)
				/ (this.maxCumulativeReward - this.minCommulativeReward))
				* this.maxExplorationRate;
		// epsilon can become less than zero if we choose a maximal value less than the true 
		// maximum
		if (this.epsilon < 0)
			this.epsilon = 0.0;

		this.cumulativeReward = 0.0;
		this.currentEpisode++;
	}
	
	public void reset() {
		this.averageCumulativeReward = this.minCommulativeReward;
		this.cumulativeReward = 0;
		this.currentEpisode = 0;
		// TODO: step size calculator would have to be reset too
	}

	// setter and getter

	public double getMaxCommulativeReward() {
		return maxCumulativeReward;
	}

	public void setMaxCommulativeReward(double maxCommulativeReward) {
		this.maxCumulativeReward = maxCommulativeReward;
	}

	public void setMinCommulativeReward(double minCommulativeReward) {
		this.minCommulativeReward = minCommulativeReward;
	}

	public double getMinCommulativeReward() {
		return minCommulativeReward;
	}

	public double getMaxExplorationRate() {
		return maxExplorationRate;
	}

	public void setMaxExplorationRate(double maxExplorationRate) {
		this.maxExplorationRate = maxExplorationRate;
	}

	public StepSizeCalculator getStepSizeCalculator() {
		return stepSizeCalculator;
	}

	public void setStepSizeCalculator(StepSizeCalculator stepSizeCalculator) {
		this.stepSizeCalculator = stepSizeCalculator;
	}
}
