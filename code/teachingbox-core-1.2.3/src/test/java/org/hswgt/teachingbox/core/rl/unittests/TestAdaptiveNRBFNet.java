/**
 *
 * $Id: TestAdaptiveNRBFNet.java 989 2015-06-17 20:16:58Z micheltokic $
 *
 * @version   $Rev: 989 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 22:16:58 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.unittests;


import static org.junit.Assert.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.network.adaption.NoNodeNearby;
import org.junit.Test;


import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import org.hswgt.teachingbox.core.rl.network.Network;
import org.hswgt.teachingbox.core.rl.network.rbf.RadialBasisFunction;



public class TestAdaptiveNRBFNet
{

    /**
     * Test the create of new RBF.
     */
    @Test
    public void testCreate()
    {
        Logger.getRootLogger().setLevel(Level.DEBUG);

        double[] sigma = new double[] {0.5};
        // create adaptive network adding rbfs, use sigma for the position of
        // the sample RBF, the rule ignores this cause it changes the position
        // of newly created nodes
        Network net = new Network(new NoNodeNearby(
                new RadialBasisFunction(sigma, sigma)));
        net.setIsNormalized(true);

        assertEquals(net.size(), 0);

        net.getFeatures( new DenseDoubleMatrix1D(new double[]{0.0}) );
        assertEquals(net.size(), 1);

        net.getFeatures( new DenseDoubleMatrix1D(new double[]{2*sigma[0]-0.3}) );
        assertEquals(net.size(), 1);

        net.getFeatures( new DenseDoubleMatrix1D(new double[]{2*sigma[0]}) );
        assertEquals(net.size(), 2);

    }



}
