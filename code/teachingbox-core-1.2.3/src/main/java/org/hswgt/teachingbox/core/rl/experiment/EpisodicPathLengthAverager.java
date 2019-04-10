/**
 *
 * $Id: EpisodicPathLengthAverager.java 744 2010-06-21 11:38:29Z twanschik $
 *
 * @version   $Rev: 744 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-06-21 13:38:29 +0200 (Mo, 21 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.experiment;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * Averages the number of steps the agent needed for each episode.
 */
public class EpisodicPathLengthAverager extends EpisodicCumulativScalarAverager {

    /**
     * The constructor.
     * @param maxEpisodes The maximum number of episodes
     * @param configString The config string used as the plot title
     */
    public EpisodicPathLengthAverager(int maxEpisodes,
            String configString) {
        super(maxEpisodes, configString);
    }

    /*
     * (non-Javadoc)
     * @see org.hswgt.teachingbox.core.rl.experiment.DataAverager#update(org.hswgt.teachingbox.core.rl.env.State, org.hswgt.teachingbox.core.rl.env.Action, org.hswgt.teachingbox.core.rl.env.State, org.hswgt.teachingbox.core.rl.env.Action, double, boolean)
     */
    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean terminalState) {

        this.episodicData += 1;
    }
}
