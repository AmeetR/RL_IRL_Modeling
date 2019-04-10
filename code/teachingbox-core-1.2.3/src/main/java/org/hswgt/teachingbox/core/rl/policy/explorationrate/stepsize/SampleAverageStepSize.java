package org.hswgt.teachingbox.core.rl.policy.explorationrate.stepsize;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.learner.stepsize.StepSizeCalculator;

/**
 * Only calculates 1/k with k updated each step if added to an agent as an observer.
 * If you would like to calculate 1/k for each state-action pair use class
 * rl.learner.stepsize.SampleAverageStepSize
 * @author twanschik
 */
public class SampleAverageStepSize implements StepSizeCalculator {
    protected int k = 0;

    public double getAlpha(State state, Action action) {
        return 1.0/((double) this.k);
    }

    public void update(State s, Action a, State sn, Action an, double r,
            boolean isTerminalState) {
        this.k++;
    }

    public void updateNewEpisode(State initialState) {
    }
}
