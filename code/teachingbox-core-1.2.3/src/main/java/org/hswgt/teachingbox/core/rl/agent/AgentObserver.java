
package org.hswgt.teachingbox.core.rl.agent;

import java.io.Serializable;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * The AgentObserver will be notified if the agent
 * does a new step or a new episode is started
 */
public interface AgentObserver extends Serializable {
    /**
     * This method will be called in every step of the experiment
     * @param state State at time t
     * @param action Action at time t
     * @param nextState State at time t+1
     * @param nextAction Action at time t+1
     * @param reward Reward for doing action a in state s
     * @param terminalState True if sn is a terminal state
     */
    public void update(State state, Action action, State nextState, Action nextAction,
            double reward, boolean terminalState);
    
    /**
     * This method will be called at if a new Episode starts
     * @param initialState The initial state
     */
    public void updateNewEpisode(State initialState);

}
