
package org.hswgt.teachingbox.core.rl.learner;

import org.hswgt.teachingbox.core.rl.agent.AgentObserver;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * Interface for all learning algorithms
 */
public interface Learner extends AgentObserver
{
    /**
     * This method will be called in every step of the experiment
     * @param state State at time t
     * @param action Action at time t
     * @param nextState State at time t+1
     * @param nextAction Action at time t
     * @param reward Reward for doing Action in State
     * @param isTerminalState True if sn is a terminal state
     */
    public void update(State state, Action action, State nextState, Action nextAction,
            double reward, boolean isTerminalState);
    
    /**
     * This method will be called at if a new Episode starts
     * @param initialState The initial State
     */
    public void updateNewEpisode(State initialState);
}
