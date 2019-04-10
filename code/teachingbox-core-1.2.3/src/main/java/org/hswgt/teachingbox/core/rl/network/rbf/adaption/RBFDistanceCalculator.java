/**
 *
 * $Id: RBFDistanceCalculator.java 679 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 679 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network.rbf.adaption;

import java.io.Serializable;

import cern.colt.matrix.DoubleMatrix1D;
import org.hswgt.teachingbox.core.rl.network.adaption.InScopeCalculator;
import org.hswgt.teachingbox.core.rl.network.rbf.RadialBasisFunction;

/*
 * Calculates the exponent of a radial basis function instead of the value to
 * save computation time.
 *
 * TODO: Maybe rewrite it to work like in TileInScopeCalculator. Instead of
 * passing in the maxDistance to check against we would pass in the sigma to use
 * Maybe this is easier for the developer.
 */
public class RBFDistanceCalculator extends InScopeCalculator<RadialBasisFunction>
        implements Serializable {

    private static final long serialVersionUID = -3536598810540720778L;
    // default maximum distance is 1
    protected double maxDistance = 1;

    public RBFDistanceCalculator() {
    }

    public RBFDistanceCalculator(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public boolean isInScope(final RadialBasisFunction node, final DoubleMatrix1D feat) {
        return (RBFDistanceCalculator.getDistance(node.getPosition(),
                node.getSigma(), feat) < this.maxDistance);
    }

    public static double getDistance(final DoubleMatrix1D position,
            final DoubleMatrix1D sigma, final DoubleMatrix1D feat) {
        if ((position.size() != sigma.size()) || position.size() != feat.size()) {
            throw new IllegalArgumentException(
                    "features and sigma must have the same size");
        }

        double sum = 0;
        for (int i=0; i<sigma.size(); i++) {
            double dist = position.get(i) - feat.get(i);
            sum += (dist * dist) / (2 * sigma.get(i) * sigma.get(i));
        }

        return sum;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }
}
