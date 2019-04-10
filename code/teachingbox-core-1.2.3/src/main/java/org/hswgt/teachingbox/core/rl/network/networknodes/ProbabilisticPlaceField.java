/**
 *
 * $Id: ProbabilisticPlaceField.java 675 2010-06-11 08:45:17Z twanschik $
 *
 * @version $Rev: 675 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-11 10:45:17 +0200 (Fr, 11 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network.networknodes;

import java.io.Serializable;

import org.hswgt.teachingbox.core.rl.network.rbf.RadialBasisFunction;
import cern.colt.matrix.DoubleMatrix1D;
import cern.jet.random.Uniform;

/**
 * Similar to a RadialBasisFunction with the difference that the computed value
 * is used as a probability of switching the ProbabilisticPlaceField on/off. So a ProbabilisticPlaceField
 * is a probabilistic binary feature.
 */

public class ProbabilisticPlaceField extends RadialBasisFunction implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1022388918287010249L;


    public ProbabilisticPlaceField(DoubleMatrix1D mu, DoubleMatrix1D sigma) {
        super(mu, sigma);
    }

    public ProbabilisticPlaceField(double[] mu, double[] sigma) {
        super(mu, sigma);
    }

    public ProbabilisticPlaceField(DoubleMatrix1D mu, DoubleMatrix1D sigma, double scale) {
        super(mu, sigma, scale);
    }

    public ProbabilisticPlaceField(double[] mu, double[] sigma, double scale) {
        super(mu, sigma, scale);
    }

    public double getValue(DoubleMatrix1D x) {
        if (Uniform.staticNextDouble() < super.getValue(x))
            return 1.0;
        return 0.0;
    }


    public ProbabilisticPlaceField copy() {
        return new ProbabilisticPlaceField(this.position, this.sigma, this.scale);
    }
}
