/**
 *
 * $Id: TestFeatureCache.java 989 2015-06-17 20:16:58Z micheltokic $
 *
 * @version   $Rev: 989 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 22:16:58 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.unittests;

import static org.junit.Assert.*;

import org.hswgt.teachingbox.core.rl.feature.FeatureCache;
import org.junit.Test;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.jet.random.Uniform;


public class TestFeatureCache
{
    @Test
    public void testDenseDoubleMatrix1D()
    {
        DenseDoubleMatrix1D a = new DenseDoubleMatrix1D(new double[]{
                Uniform.staticNextDouble(),Uniform.staticNextDouble(),Uniform.staticNextDouble()
        });
        
        DenseDoubleMatrix1D b = new DenseDoubleMatrix1D(new double[]{
                Uniform.staticNextDouble(),Uniform.staticNextDouble(),Uniform.staticNextDouble()
        });
        
        DoubleMatrix1D c = a.copy();
        
        FeatureCache cache = new FeatureCache(10);
        cache.put(a, null);
        
        assertTrue(cache.containsKey(a));
        assertFalse(cache.containsKey(b));
        assertFalse(cache.containsKey(c));
    }
}
