/**
 *
 * $Id: WrapAdjustedStepLengthAverager.java 696 2010-06-21 11:38:29Z twanschik $
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
import org.hswgt.teachingbox.core.rl.tools.MathUtils;

/**
 * Averages the step length between a state transition i.e.
 * ||(state - nextState)||.
 *
 * Uses MathUtils.shortestDistance in order to deal with boundary transitions if
 * needed. Otherwise it produces the same results as StepLengthAverager.
 * (see MathUtils.shortestDistance for an example).
 *
 * Pass in which dimensions should be adjusted as well as the corresponding
 * boundaries the adjustes value is allowed to be in.
 */

public class WrapAdjustedStepLengthAverager extends ScalarAverager {
    protected double[][] boundaries;
    protected int[] dimensionsToAdjust;
    
    /**
     * The constructor
     * @param dimensionsToAdjust array indicating which dimensions to adjust
     * @param boundaries array containing boundaries for each dimension to adjust
     * @param maxSteps the maximum steps per episode
     * @param configString the config string for plotting
     */
    public WrapAdjustedStepLengthAverager(int[] dimensionsToAdjust,
            double[][] boundaries, int maxSteps, String configString) {
        super(maxSteps, configString);
        this.dimensionsToAdjust = dimensionsToAdjust.clone();
        this.boundaries = boundaries.clone();
    }

    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean terminalState) {

        State diff = nextState.copy();
        SeqBlas.seqBlas.daxpy(-1.0, state, diff);

        for (int dimensionToAdjust : this.dimensionsToAdjust) {
            // adjust distances between state and nextState for the chosen
            // dimensions
            diff.set(dimensionToAdjust, MathUtils.shortestDistance(
                    diff.get(dimensionToAdjust), this.boundaries[dimensionToAdjust][0],
                    this.boundaries[dimensionToAdjust][1], true));
        }
        
        this.updateAverage(Math.sqrt(diff.zDotProduct(diff)));
    }
}
