/**
 *
 * $Id: TestRadialBasisFunction.java 989 2015-06-17 20:16:58Z micheltokic $
 *
 * @version   $Rev: 989 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 22:16:58 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.unittests;

import static org.junit.Assert.*;

import org.hswgt.teachingbox.core.rl.network.rbf.RadialBasisFunction;
import org.junit.Test;

import cern.jet.random.Uniform;


public class TestRadialBasisFunction
{
    static final double DELTA = 0.0000001;
    
    public double correctFunction(final double[] x, final double[] mu, final double[] sigma)
    {
        double sum = 0;
        for(int i=0; i<mu.length; i=i+1){
            double dist = mu[i] - x[i];
            sum += (dist*dist) / (2*sigma[i]*sigma[i]);
        }
        return Math.exp(-sum);
    }
    
    @Test
    public void testCorrectValues()
    {
        double[] mu = new double[] {0,0};
        double[] sigma = new double[] {0.5,0.5};
        RadialBasisFunction rbf = new RadialBasisFunction(mu, sigma);
        
        for(int i=0; i<100; i++)
        {
            double[] x = new double[]{ Uniform.staticNextDouble(), Uniform.staticNextDouble() };
            assertEquals(rbf.getValue(x), correctFunction(x, mu, sigma), DELTA);
        }
    }
}
