/**
 *
 * $Id: WeightDecay.java 676 2010-06-11 08:45:17Z twanschik $
 *
 * @version $Rev: 676 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-11 10:45:17 +0200 (Fr, 11 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.learner;

import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.function.DoubleFunction;

/**
 * Decays all weights of a QFeatureFunction by a given factor each step
 * resulting in an exponentionaly decay (see "Path-finding in real and simulated
 * rats: assessing the influence of path characteristics on navigation learning"
 * from Minija Tamosiunaite). Can be used to simulate the process of forgetting
 * thus forgetting learned solutions which are bad or sub-optimal.
 */
public class WeightDecay extends QDecayer<QFeatureFunction> {
    protected double decayRate;

    // threshold can be used to set the qFuntion value to 0 when it reaches the
    // threshold.
    protected double threshold;

    public WeightDecay(QFeatureFunction qFunction) {
        this(qFunction, 0.9995, 0.000001, QDecayer.Mode.STEP, 1);
    }

    public WeightDecay(QFeatureFunction qFunction, double decayRate,
            double threshold, QDecayer.Mode mode, int decayInterval) {
        super(qFunction, mode, decayInterval);
        this.decayRate = 0.9995;
        this.threshold = threshold;
    }

    public void decay() {
        this.qFunction.updateWeightsScaled((this.decayRate - 1),
                this.qFunction.getWeights());
        // reset values lower than the given threshold to 0.0
        DoubleMatrix1D weights = this.qFunction.getWeights();
        weights.assign(new DoubleFunction() {
            public double apply(double value) {
                return Math.abs(value) < threshold ? 0.0 : value;
            }
        });
        this.qFunction.setWeights(weights);
    }

    // getter and setter
    
    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getDecayRate() {
        return decayRate;
    }

    public void setDecayRate(double decayRate) {
        this.decayRate = decayRate;
    }
}
