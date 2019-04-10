/**
 *
 * $Id: ExperimentStepData.java 731 2010-07-17 13:44:31Z twanschik $
 *
 * @version   $Rev: 731 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-07-17 15:44:31 +0200 (Sa, 17 Jul 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.experiment;

import java.io.Serializable;
import org.hswgt.teachingbox.core.rl.env.UpdateStruct;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * Data containing information for a given step i.e. state, action, ... reward,
 * isTerminal state as well as the episode and step for which this data was
 * collected.
 */
public class ExperimentStepData implements Serializable {
    protected UpdateStruct updateData;
    protected int episode;
    protected int step;

    public ExperimentStepData(int episode, int step, State state, Action action,
            State nextState, Action nextAction, double reward,
            boolean isTerminalState) {
        this.episode = episode;
        this.step = step;
        this.updateData = new UpdateStruct(state, action, nextState, nextAction,
                reward, isTerminalState);
    }

    public int getEpisode() {
        return this.episode;
    }

    public int getStep() {
        return this.step;
    }

    public State getState() {
        return this.updateData.getState();
    }

    public Action getAction() {
        return this.updateData.getAction();
    }

    public State getNextState() {
        return this.updateData.getNextState();
    }

    public Action getNextAction() {
        return this.updateData.getNextAction();
    }

    public double getReward() {
        return this.updateData.getReward();
    }

    public boolean getIsTerminalState() {
        return this.updateData.getIsTerminalState();
    }
}