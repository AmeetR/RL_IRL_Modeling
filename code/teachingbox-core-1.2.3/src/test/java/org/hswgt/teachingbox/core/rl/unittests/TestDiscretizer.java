/**
 *
 * $Id: TestDiscretizer.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.unittests;

import org.hswgt.teachingbox.core.rl.feature.Discretizer;
import org.junit.Test;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import static org.junit.Assert.*;

public class TestDiscretizer 
{
    @Test 
    public void testSimpleValue() 
    {
        // discretize a variable x \in [0,1] with 5 buckets
        for(double i=0; i<=1; i=i+0.01){
            int disc = Discretizer.getDiscreteValue(i, 0, 1, 5);
            if( i <= 0.2 ) assertEquals(0, disc);
            if( i > 0.2 && i <= 0.4 ) assertEquals(1, disc);
            if( i > 0.4 && i <= 0.6 ) assertEquals(2, disc);
            if( i > 0.6 && i <= 0.8 ) assertEquals(3, disc);
            if( i > 0.8 && i <= 1.0 ) assertEquals(4, disc);
        }   
        
    }
    
    @Test
    public void testSimpleValue2() 
    {
        // discretize a variable x \in [-2,3] with 4 buckets
        for(double i=-3; i<=4; i=i+0.1){
            int disc = Discretizer.getDiscreteValue(i, -2, 3, 4);
            if( i <= -0.75 ) assertEquals(0, disc);
            if( i > -0.75 && i <= 0.5 ) assertEquals(1, disc);
            if( i > 0.5 && i <= 1.75 ) assertEquals(2, disc);
            if( i > 1.75 ) assertEquals(3, disc);
        } 
    }
    
    @Test
    public void testNegativeRange() 
    {
        // discretize a variable x \in [-2,-1] with 4 buckets
        for(double i=-3; i<=0; i=i+0.1){
            int disc = Discretizer.getDiscreteValue(i, -2, -1, 4);
            if( i <= -1.75 )             assertEquals(0, disc);
            if( i > -1.75 && i <= -1.5 ) assertEquals(1, disc);
            if( i > -1.5 && i <= -1.25 ) assertEquals(2, disc);
            if( i > -1.25 )              assertEquals(3, disc);
        } 
    }
    
    @Test
    public void test3DimensionalVector()
    {
        final double DELTA = 0.00001;
        double[][] config = new double[][]{
                { 0, 1, 5}, // [0,1] in 5 discrete values
                {-2, 2, 4}, // [-2,2] in 4 discrete values
                {-2, -1, 2} // [-2, -1] in 2 discrete values
        };
        
        Discretizer discretizer = new Discretizer(config);
        for(double i=-3; i<6; i=i+0.1){
            for(double k=-3; k<6; k=k+0.2){
                for(double l=-3; l<6; l=l+0.1){
                    DoubleMatrix1D s = new DenseDoubleMatrix1D(new double[]{i,k,l});
                    DoubleMatrix1D d = discretizer.getFeatures(s);
                    
                    // test 1. dimension
                    double d0 = d.get(0);
                    if( i <= 0.2 )           assertEquals(0, d0,DELTA);
                    if( i > 0.2 && i <= 0.4) assertEquals(1, d0,DELTA);
                    if( i > 0.4 && i <= 0.6) assertEquals(2, d0,DELTA);
                    if( i > 0.6 && i <= 0.8) assertEquals(3, d0,DELTA);
                    if( i > 0.8)             assertEquals(4, d0,DELTA);
                    
                    // test 2. dimension
                    double d1 = d.get(1);
                    if( k <= -1)           assertEquals(0, d1,DELTA);
                    if( k > -1 && k <= 0)  assertEquals(1, d1,DELTA);
                    if( k > 0 && k <= 1)   assertEquals(2, d1,DELTA);
                    if( k > 1)             assertEquals(3, d1,DELTA);
                    
                    // test 3. dimension
                    double d2 = d.get(2);
                    if( l <= -1.5)          assertEquals(0, d2,DELTA);
                    if( l > -1.5)           assertEquals(1, d2,DELTA);
                }
            }
        }
    }
    
    @Test 
    public void testOutOfRange()
    {
        int disc;
        // check values out of range
        disc = Discretizer.getDiscreteValue(-0.1, 0, 1, 5);
        assertEquals(0, disc);
        disc = Discretizer.getDiscreteValue(1.1, 0, 1, 5);
        assertEquals(4, disc);
    }
    
    
    
    @Test(expected=IllegalArgumentException.class) 
    public void testFromGEto()
    {
        Discretizer.getDiscreteValue(1.1, 0, -1, 5);
    }
    
    @Test(expected=IllegalArgumentException.class) 
    public void testNegativeNumberOfDiscreteValues()
    {
        Discretizer.getDiscreteValue(1.1, 0, 100, -1);
    }
    
    @Test(expected=IllegalArgumentException.class) 
    public void testNullDiscreteValues()
    {
        Discretizer.getDiscreteValue(1.1, 0, 100, 0);
    }
}
