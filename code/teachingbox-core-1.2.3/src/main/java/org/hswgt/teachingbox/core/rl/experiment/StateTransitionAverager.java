

package org.hswgt.teachingbox.core.rl.experiment;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.linalg.SeqBlas;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * Averages state transitions that is state - nextState. Can be used to get the
 * average direction the agents went throughout the experiment.
 * @author twanschik
 */
public class StateTransitionAverager extends DoubleMatrix1DAverager {

    /**
     * The constructor
     * @param maxSteps maximum steps per episode
     * @param configString the config string for plotting
     * @param dimension The dimension
     */
    public StateTransitionAverager(int maxSteps, String configString,
            int dimension) {
        super(maxSteps, configString, dimension);
    }

    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean terminalState) {
        DoubleMatrix1D diff = state.copy();
                SeqBlas.seqBlas.daxpy(-1, nextState, diff);
        this.updateAverage(diff);
    }
}