/**
 *
 * $Id: TileDistanceCalculator.java 679 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 679 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network.adaption;

import java.io.Serializable;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.linalg.SeqBlas;
import org.hswgt.teachingbox.core.rl.network.NetworkNode;

/*
 * Calculates the euclidean distance squared to save computation time.
 */
public class EuclideanDistanceSquared extends InScopeCalculator implements Serializable {
    
    private static final long serialVersionUID = -138450911259802720L;
    // default maximum distance is 1
    protected double maxDistance = 1;

    public EuclideanDistanceSquared() {
    }

    public EuclideanDistanceSquared(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public boolean isInScope(final NetworkNode node, final DoubleMatrix1D feat) {
        return (EuclideanDistanceSquared.getDistance(node.getPosition(), feat)
                < this.maxDistance);
    }

    public static double getDistance(final DoubleMatrix1D featA,
            final DoubleMatrix1D featB) {
        DoubleMatrix1D diff = featA.copy();
        SeqBlas.seqBlas.daxpy(-1, featB, diff);
        return diff.zDotProduct(diff);
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }
}

