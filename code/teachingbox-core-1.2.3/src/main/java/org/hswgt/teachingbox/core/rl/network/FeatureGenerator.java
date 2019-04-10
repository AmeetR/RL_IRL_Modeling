/**
 *
 * $Id: FeatureGenerator.java 696 2010-06-21 11:38:29Z twanschik $
 *
 * @version   $Rev: 696 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-06-21 13:38:29 +0200 (Mo, 21 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.network;

/*
 * FeatureGenerator generators the feature vector which the network will return 
 * in Network.getFeatures. Use this function to optimze feature vector generation. 
 */

import cern.colt.matrix.DoubleMatrix1D;

public abstract class FeatureGenerator implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6622066814657638339L;

    protected Network net;
    public abstract DoubleMatrix1D getFeatureVector(DoubleMatrix1D state);

    public FeatureGenerator() {
    }

    public FeatureGenerator(Network net) {
        this.net = net;
    }

    // setter and getter

    public Network getNet() {
        return net;
    }

    public void setNet(Network net) {
        this.net = net;
    }

}
