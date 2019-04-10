/**
 *
 * $Id: DoNothing.java 669 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 669 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.network.adaption;

import java.io.Serializable;

import cern.colt.matrix.DoubleMatrix1D;

// A adaptive rule doing nothing that is not changing the network at all

public class DoNothing extends AdaptionRule implements Serializable{

    private static final long serialVersionUID = -8623905177743389360L;

    public void changeNet(DoubleMatrix1D feat) {
    }
}
