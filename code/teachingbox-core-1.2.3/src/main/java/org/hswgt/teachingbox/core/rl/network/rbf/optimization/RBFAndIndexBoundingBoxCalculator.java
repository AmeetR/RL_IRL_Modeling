/**
 *
 * $Id: RBFAndIndexBoundingBoxCalculator.java 713 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 713 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.network.rbf.optimization;

import cern.colt.matrix.DoubleMatrix1D;
import org.hswgt.teachingbox.core.exceptions.IllegalConfigurationException;
import org.hswgt.teachingbox.core.rl.network.optimization.BoundingBoxCalculator;
import org.hswgt.teachingbox.core.rl.network.optimization.Box;
import org.hswgt.teachingbox.core.rl.network.optimization.NodeAndIndex;
import org.hswgt.teachingbox.core.rl.network.rbf.RadialBasisFunction;

/**
 * BoundingBox calculator which knows how to get a BoundingBox for a
 * RadialBasisFunction from a NodeAndIndex instance.
 */
public class RBFAndIndexBoundingBoxCalculator implements
        BoundingBoxCalculator<NodeAndIndex> {

    public Box createBoundingBox(NodeAndIndex rbfAndIndex) {
        RadialBasisFunction rbf = (RadialBasisFunction) rbfAndIndex.getNode();

        // TODO: Refactore this to be located in the RadialBasisFunction itself
        // somehow
        if (rbf.getNet() != null && rbf.getNet().isNormalized())
            throw new IllegalConfigurationException("It's not possible to use a" +
                    " feature generator for a normalized net in combination with RBFs");

        double maxDistance = rbf.getMaxDistance();
        DoubleMatrix1D sigma = rbf.getSigma();
        DoubleMatrix1D widths = sigma.like();

        for(int i=0; i<widths.size(); i++)
            widths.set(i, Math.sqrt(maxDistance*2*sigma.get(i)*sigma.get(i)));

        return new Box(rbf.getPosition(), widths);
    }
}