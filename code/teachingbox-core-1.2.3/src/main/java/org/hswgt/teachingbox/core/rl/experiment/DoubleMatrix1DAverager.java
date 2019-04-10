package org.hswgt.teachingbox.core.rl.experiment;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.SeqBlas;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * Averages a vector valued variable at each time step over episodes. Default
 * is to average the actions taken (which can be useful for continous actions).
 * @author twanschik
 */
public class DoubleMatrix1DAverager extends DataAverager<DoubleMatrix1D, DoubleMatrix2D> {

    private static final long serialVersionUID = 7963389815902355970L;
    // the dimension of the vectors we want to average
    protected int dimension = 0;

    /**
     * The constructor
     * @param maxSteps maximum steps per episode
     * @param configString the config string for plotting
     * @param dimension The dimension
     */
    public DoubleMatrix1DAverager(int maxSteps, String configString, int dimension) {
        super(maxSteps, configString);
        this.dimension = dimension;
        this.clearDataArrray();
        this.clearVarianceDataArrray();
    }

    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean terminalState) {
        // default is to average the action taken, can be useful for continuous
        // actions, but for discrete actions it can produce meaningfull results
        // too
        this.updateAverage(action);
    }

    // use this method to pass in the parameter you want to average in each time step
    public void updateAverage(DoubleMatrix1D parameter) {
        DoubleMatrix1D oldMean = this.dataArray.get(t).copy();

        DoubleMatrix1D diff = parameter.copy();
        SeqBlas.seqBlas.daxpy(-1, this.dataArray.get(t), diff);
        SeqBlas.seqBlas.dscal(1.0/((double) episode), diff);
        SeqBlas.seqBlas.daxpy(1, diff, this.dataArray.get(t));

        // calculate the variance, see Donald Knuth's Art of Computer Programming,
        // Vol 2, page 232, 3rd edition. Algorithm by by B. P. Welford

        // added small modification to the algorithm: we divide by the
        // number of samples in order to always store the variance, thus we
        // have to multiply it with the number of sample one step before so
        // we can update the variance correctly :)
        SeqBlas.seqBlas.dscal(this.episode - 1, this.varianceDataArray.get(t));
        diff = parameter.copy();
        SeqBlas.seqBlas.daxpy(-1, this.dataArray.get(t), diff);
        DoubleMatrix1D oldMeanDiff = parameter.copy();
        SeqBlas.seqBlas.daxpy(-1, oldMean, oldMeanDiff);
        SeqBlas.seqBlas.dger(1, oldMeanDiff, diff, this.varianceDataArray.get(t));
        SeqBlas.seqBlas.dscal(1.0 /((double) episode), this.varianceDataArray.get(t));
        t++;
    }

    public void clearDataArrray() {
        // initialize data array
        for (int i=0; i<maxSteps+1; i++)
            this.dataArray.set(i, new DenseDoubleMatrix1D(this.dimension));
    }

    public void clearVarianceDataArrray() {
        // initialize data array
        for (int i=0; i<maxSteps+1; i++)
            this.varianceDataArray.set(i, new DenseDoubleMatrix2D(this.dimension,
                    this.dimension));
    }
}