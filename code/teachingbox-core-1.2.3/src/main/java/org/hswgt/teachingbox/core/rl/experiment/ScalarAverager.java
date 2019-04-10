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
 * Averages the some scalar value at each time step over episodes.
 * @author twanschik
 */
public class ScalarAverager extends DataAverager<Double, Double> {
		
    private static final long serialVersionUID = 7963389815902355970L;

    /**
     * The constructor
     * @param maxSteps maximum steps per episode
     * @param configString the config string for plotting
     */
    public ScalarAverager(int maxSteps, String configString) {
        super(maxSteps, configString);
    }

    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean terminalState) {
        // default is to average the reward
        this.updateAverage(reward);
    }

    // use this method to pass in the parameter you want to average in each time step
    public void updateAverage(double parameter) {
        double oldMean = this.dataArray.get(t);
        this.dataArray.set(t, this.dataArray.get(t) + ((1.0 /
                ((double) episode)) * (parameter - this.dataArray.get(t))));
        
        // calculate the variance, see Donald Knuth's Art of Computer Programming,
        // Vol 2, page 232, 3rd edition. Algorithm by by B. P. Welford

        // added small modification to the algorithm: we divide by the
        // number of samples in order to always store the variance, thus we
        // have to multiply it with the number of sample one step before so
        // we can update the variance correctly :)
        this.varianceDataArray.set(t, this.varianceDataArray.get(t) * ((double) this.episode - 1));
        this.varianceDataArray.set(t, this.varianceDataArray.get(t) + (parameter - oldMean) * (parameter - this.dataArray.get(t)));
        this.varianceDataArray.set(t, this.varianceDataArray.get(t) / (double) this.episode);
        t++;
    }

    public void clearDataArrray() {
        // initialize data array
        for (int i=0; i<maxSteps+1; i++)
            this.dataArray.set(i, 0.0);
    }

    public void clearVarianceDataArrray() {
        // initialize data array
        for (int i=0; i<maxSteps+1; i++)
            this.varianceDataArray.set(i, 0.0);
    }
}
