/**
 *
 * $Id: AdaptionRule.java 669 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 669 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network.adaption;

import java.io.Serializable;

import org.hswgt.teachingbox.core.rl.network.*;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/**
 * This is the abstract base class for an adaptive method to use in a network.
 * Implement changeNet to specify when to add/delete/reshape network nodes.
 * changeNet is called just before getFeatures so that the rule can
 * change the network just in time.
 */

public abstract class AdaptionRule implements Serializable {

    private static final long serialVersionUID = 708134800756309715L;
    protected Network net;

    public AdaptionRule() {
    }

    public AdaptionRule(Network net) {
        this.net = net;
    }

    // implement changeNet to define when RBFs have to be added/deleted/reshaped
    public abstract void changeNet(DoubleMatrix1D feat);

    public void changeNet(double[] feat) {
        changeNet(new DenseDoubleMatrix1D(feat));
    }

    // setter and getter

    public Network getNet() {
        return net;
    }

    public void setNet(Network net) {
        this.net = net;
    }
}
