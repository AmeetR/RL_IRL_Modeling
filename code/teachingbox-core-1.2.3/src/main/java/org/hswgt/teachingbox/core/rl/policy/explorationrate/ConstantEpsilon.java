package org.hswgt.teachingbox.core.rl.policy.explorationrate;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * @author Thomas Wanschik
 *
 */
public class ConstantEpsilon implements EpsilonCalculator {

    // default value of the parameter epsilon
    private double epsilon = 0.1;

    /**
     * Constructor.
     * @param epsilon Value of the parameter epsilon
     */
    public ConstantEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * Set epsilon
     * @param epsilon Value of epsilon.
     */
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public double getEpsilon(State state) {
        return this.epsilon;
    }

    public void update(State state, Action action, State nextState, Action nextAction,
            double reward, boolean isTerminalState) {
    }

    public void updateNewEpisode(State initialState) {
    }
}
