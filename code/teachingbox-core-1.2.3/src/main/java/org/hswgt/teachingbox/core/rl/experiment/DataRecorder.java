/**
 *
 * $Id: DataRecorder.java 731 2010-07-17 13:44:31Z twanschik $
 *
 * @version   $Rev: 731 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-07-17 15:44:31 +0200 (Sa, 17 Jul 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.experiment;


import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;

/*
 * Records all data (episode, stepm state, action, ...) of an experiment and
 * sasves it at the end of an experiment. Once done, the data can be used by
 * DataExperiment in order to analyise (i.e. make plots of the data) afterwoods.
 */

public class DataRecorder implements ExperimentObserver {

    private static final Logger log4j = Logger.getLogger("DataRecorder");
    protected ExperimentData data;
    protected int current_step = 0;
    protected int current_episode = -1;
    protected String filename;

    public DataRecorder(String filename, int maxEpisodes, int maxSteps) {
        this.data = new ExperimentData(maxEpisodes, maxSteps);
        this.filename = filename;
    }

    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean isTerminalState) {
        this.data.addEpisodicData(this.current_episode, this.current_step,
                state, action, nextState, nextAction, reward, isTerminalState);
        this.current_step++;
    }

    public void updateNewEpisode(State initial_state) {
        this.current_step = 0;
        this.current_episode++;
    }

    public void updateExperimentStop() {
        ObjectSerializer.save(this.filename, this.data);
    }

    public void updateExperimentStart() {
        this.current_episode = -1;
        this.current_step = 0;
    }
}
