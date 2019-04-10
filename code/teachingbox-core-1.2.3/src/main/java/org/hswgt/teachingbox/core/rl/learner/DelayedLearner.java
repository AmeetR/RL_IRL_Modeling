/**
 *
 * $Id: DelayedLearner.java 662 2010-11-06 10:10:57Z Thomas Wanschik $
 *
 * @version   $Rev: 662 $
 * @author    $Author: Thomas Wanschik $
 * @date      $Date: 2010-11-06 10:10:57 +0100 (Fr, 11 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.learner;

import java.util.Vector;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.env.UpdateStruct;

/**
 * Learner which updates state-action pairs with a constant delay using
 * delegation to another learner.
 *
 * Note observers which calculate an average for each step HAVE to use the same delay
 * otherwise they calculate averages for wong steps!
 */
public class DelayedLearner implements Learner {
    // learner is an arbitrary Learner used to delegate learning to
    protected Learner learner;
    // updateParams is used to save the previous n state-action pairs
    // selected by the agent, where n = this.delay
    protected Vector<UpdateStruct> updateParams = new Vector<UpdateStruct>();
    protected int delay;

    public DelayedLearner(Learner learner, int delay) {
        this.learner = learner;
        this.delay = delay;
    }

    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean isTerminalState) {
        this.updateParams.add(new UpdateStruct(state, action, nextState,
                    nextAction, reward, isTerminalState));
        // we start updating the V/Q-Function as soon as the number of steps the
        // agent took, exceeds this.delay
        if(this.updateParams.size() > this.delay) {
            UpdateStruct tmpUpdateParams = this.updateParams.remove(0);
            // the current reward and terminal-state is used for the state-action
            // pairs selected this.delay steps ago, delegate to this.learner
            this.learner.update(tmpUpdateParams.getState(), tmpUpdateParams.getAction(),
                    tmpUpdateParams.getNextState(), tmpUpdateParams.getNextAction(),
                    reward, isTerminalState);
        }
    }

    public void updateNewEpisode(State state) {
    	this.updateParams.clear();
        learner.updateNewEpisode(state);
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public Learner getLearner() {
        return learner;
    }

    public void setLearner(Learner learner) {
        this.learner = learner;
    }
}
