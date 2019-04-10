/**
 *
 * $Id: TestVectorUtils.java 989 2015-06-17 20:16:58Z micheltokic $
 *
 * @version   $Rev: 989 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 22:16:58 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.unittests;

import static org.junit.Assert.*;

import org.hswgt.teachingbox.core.rl.tools.MathUtils;
import org.hswgt.teachingbox.core.rl.tools.VectorUtils;
import org.junit.Test;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.jet.random.Uniform;


public class TestVectorUtils
{
    // Test resize method
    @Test
    public void testResize()
    {
        final double DELTA = 0.000001;
        DoubleMatrix1D v = new DenseDoubleMatrix1D(new double[]{5,2});
        v = VectorUtils.resize(v, 5);
        
        assertTrue(MathUtils.equalsDelta(v.toArray(), new double[]{5,2,0,0,0}, DELTA));
        assertEquals(v.size(),5);
        
        v = VectorUtils.resize(v, 2);
        assertTrue(MathUtils.equalsDelta(v.toArray(), new double[]{5,2}, DELTA));
        assertEquals(v.size(),2);
    }
    
    // Test normalization
    @Test
    public void testNormalizeDense()
    {
        final double DELTA = 0.000001;
        DoubleMatrix1D v = new DenseDoubleMatrix1D(50);
        for(int i=0; i<v.size(); i++){
            v.set(i, Uniform.staticNextDouble());
        }
                
        VectorUtils.normalise(v);
        assertEquals(1.0, v.zSum(),DELTA);
    }
    
    // Test normalization
    @Test
    public void testNormalizeSparse()
    {
        final double DELTA = 0.000001;
        DoubleMatrix1D v = new SparseDoubleMatrix1D(5000000);
        for(int i=0; i<50; i++){
            v.set(Uniform.staticNextIntFromTo(0, v.size()-1), Uniform.staticNextDouble());
        }
                
        VectorUtils.normalise(v);
        assertEquals(1.0, v.zSum(),DELTA);
    }
}
