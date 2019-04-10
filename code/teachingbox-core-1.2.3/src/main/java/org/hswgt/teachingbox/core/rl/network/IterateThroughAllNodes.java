/**
 *
 * $Id: IterateThroughAllNodes.java 696 2010-06-21 11:38:29Z twanschik $
 *
 * @version   $Rev: 696 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-06-21 13:38:29 +0200 (Mo, 21 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.network;

import java.io.Serializable;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/*
 * IterateThroughAllNodes will iterate through all nodes and call node.getValue
 * to generate the feature vector.
 *
 */
public class IterateThroughAllNodes extends FeatureGenerator implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4740203479560653923L;

    IterateThroughAllNodes(Network net) {
        this.setNet(net);
    }

    public DoubleMatrix1D getFeatureVector(DoubleMatrix1D state) {
        DoubleMatrix1D feature = new DenseDoubleMatrix1D(net.size());
        int i = 0;
        for (NetworkNode node: net) {
            feature.set(i, node.getValue(state));
            i++;
        }
        return feature;
    }
}
