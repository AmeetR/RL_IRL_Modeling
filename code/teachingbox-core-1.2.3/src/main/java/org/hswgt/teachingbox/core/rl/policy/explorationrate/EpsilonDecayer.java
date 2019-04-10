package org.hswgt.teachingbox.core.rl.policy.explorationrate;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * Abstract class for epsilon decay mechanisms. Contains reuseable code for decaying
 * epsilon after every n steps / episodes.
 */
public abstract class EpsilonDecayer implements EpsilonCalculator {
    public static enum Mode {
        // decays Q-values after every n steps
        STEP,
        // decays Q-values after every n episodes
        EPISODE
    }

    protected Mode mode;
    protected int step = 0;
    protected int episode = 0;
    protected int decayInterval;
    
    public EpsilonDecayer() {

    }

    public EpsilonDecayer(Mode mode, int decayInterval) {
        this.mode = mode;
        this.decayInterval = decayInterval;
    }

    public void update(State state, Action currentAction, State nextState,
            Action nextAction, double reward, boolean isTerminalState) {
        step = step + 1;
        if (mode == Mode.STEP && (step % decayInterval) == 0)
            this.decayEpsilon();
    }

    public void updateNewEpisode(State initialState) {
        episode = episode + 1;
        step = 0;
        if (mode == Mode.EPISODE && (episode % decayInterval ) == 0)
            this.decayEpsilon();
    }

    // implement decayEpsilon in order decay epsilon
    public abstract void decayEpsilon();

    // setter and getter

    public int getDecayInterval() {
        return decayInterval;
    }

    public void setDecayInterval(int decayInterval) {
        this.decayInterval = decayInterval;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
