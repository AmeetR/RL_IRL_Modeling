/**
 *
 * $Id:  $
 *
 * @version   $Rev: $
 * @author    $Author: $
 * @date      $Date:  $
 *
 */

package org.hswgt.teachingbox.core.rl.experiment;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * Averages the rewards at each time step over experiments
 * @author tokicm
 */
public class CumulativeRewardAverager extends ScalarAverager {
		
    private static final long serialVersionUID = 7963389815902355970L;

    /**
     * The constructor
     * @param maxSteps maximum steps per episode
     * @param configString the config string for plotting
     */
    public CumulativeRewardAverager(int maxSteps, String configString) {
        super(maxSteps, configString);
    }

    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean terminalState) {
        // we pass
        this.updateAverage(reward + dataAccumulator);        
        this.dataAccumulator += reward;
    }
}
