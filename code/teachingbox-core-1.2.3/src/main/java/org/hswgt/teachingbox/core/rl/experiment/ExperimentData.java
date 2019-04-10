/**
 *
 * $Id: ExperimentData.java 731 2010-07-17 13:44:31Z twanschik $
 *
 * @version   $Rev: 731 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-07-17 15:44:31 +0200 (Sa, 17 Jul 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.experiment;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * ExperimentData contains **all** data of an experiment to be stored via
 * DataRecorder i.e. maxSteps , maxEpisodes, and all ExperimentStepData
 * collected through the whole experiment.
 */
public class ExperimentData implements Iterable<ExperimentStepData>, Serializable {
    protected LinkedList<ExperimentStepData> experimentStepData = 
            new LinkedList<ExperimentStepData>();;
    protected int maxSteps;
    protected int maxEpisodes;

    public ExperimentData(int maxEpisodes, int maxSteps) {
        this.maxEpisodes = maxEpisodes;
        this.maxSteps = maxSteps;
    }

    public void addEpisodicData(int episode, int step, State state, Action action,
            State nextState, Action nextAction, double reward,
            boolean isTerminalState) {
        this.addEpisodicData(new ExperimentStepData(episode, step, state, action,
                nextState, nextAction, reward, isTerminalState));
    }

    public void addEpisodicData(ExperimentStepData episodicData) {
        this.experimentStepData.add(episodicData);
    }

    public Iterator<ExperimentStepData> iterator() {
        return this.experimentStepData.iterator();
    }

    public int getMaxEpisodes() {
        return this.maxEpisodes;
    }

    public int getMaxSteps() {
        return this.maxSteps;
    }
}
