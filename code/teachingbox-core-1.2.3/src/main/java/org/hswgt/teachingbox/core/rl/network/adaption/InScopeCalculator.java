/**
 *
 * $Id: InScopeCalculator.java 679 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 679 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network.adaption;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import org.hswgt.teachingbox.core.rl.network.NetworkNode;

public abstract class InScopeCalculator<T extends NetworkNode> {

    public abstract boolean isInScope(T node, DoubleMatrix1D feat);

    public boolean isInScope(T node, double[] feat) {
        return this.isInScope(node, new DenseDoubleMatrix1D(feat));
    }

}
