/**
 *
 * $Id: StepLength.java 696 2010-06-21 11:38:29Z twanschik $
 *
 * @version   $Rev: 696 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-06-21 13:38:29 +0200 (Mo, 21 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.experiment;

import cern.colt.matrix.linalg.SeqBlas;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * Averages step length between a state transition i.e.
 * ||(state - nextState)||.
 *
 * This can be used to see how big the steps the agent takes are in average.
 */

public class StepLengthAverager extends ScalarAverager {

    /**
     * The constructor
     * @param maxSteps the maximum steps per episode
     * @param configString the config string for plotting
     */
    public StepLengthAverager(int maxSteps, String configString) {
        super(maxSteps, configString);
    }

     /*
     * (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.DataAverager#update(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean terminalState) {

        State diff = nextState.copy();
        SeqBlas.seqBlas.daxpy(-1.0, state, diff);
        
        this.updateAverage(Math.sqrt(diff.zDotProduct(diff)));
    }
}
