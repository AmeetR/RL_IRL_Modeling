/**
 *
 * $Id: TestQFeatureFunction.java 989 2015-06-17 20:16:58Z micheltokic $
 *
 * @version   $Rev: 989 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 22:16:58 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.unittests;


import static org.junit.Assert.*;

import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.feature.FeatureFunction;
import org.hswgt.teachingbox.core.rl.tools.MathUtils;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;


public class TestQFeatureFunction
{
    QFeatureFunction Q;
    static final ActionSet as = new ActionSet();
    static final DenseDoubleMatrix1D ones = new DenseDoubleMatrix1D(12);
    static
    {
        as.add(new Action(new double[]{1}));
        as.add(new Action(new double[]{2}));
        as.add(new Action(new double[]{3}));
        as.add(new Action(new double[]{4}));
        
        ones.assign(1);
    }
    
    /**
     * @throws java.lang.Exception The Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
      
    }

    /**
     * @throws java.lang.Exception The Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    /**
     * @throws java.lang.Exception The Exception
     */
    @Before
    public void setUp() throws Exception
    {
        Q = new QFeatureFunction(new DummyFeatureFunction(),as);
    }

    /**
     * @throws java.lang.Exception The Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * Check for vector sizes
     */
    @Test
    public void testVectorLength()
    {
        assertEquals(Q.getWeightVectorSize(), 4*3);
        assertEquals(Q.getGradientSize(), 4*3);
    }
    
    /** 
     * Check for correct gradient calculation
     */
    public void testGradient()
    {
        double[] correct;
        double[] grad;
        
        // gradient for 1. action
        grad = Q.getGradient(null, as.get(0)).toArray();
        correct = new double[] {1,1,1,0,0,0,0,0,0,0,0,0};
        assertTrue(MathUtils.equalsDelta(grad, correct, 0.000000001));
        
        // gradient for 2. action
        grad = Q.getGradient(null, as.get(1)).toArray();
        correct = new double[] {0,0,0,1,1,1,0,0,0,0,0,0};
        assertTrue(MathUtils.equalsDelta(grad, correct, 0.000000001));
        
        // gradient for 3. action
        grad = Q.getGradient(null, as.get(2)).toArray();
        correct = new double[] {0,0,0,0,0,0,1,1,1,0,0,0};
        assertTrue(MathUtils.equalsDelta(grad, correct, 0.000000001));
        
        // gradient for 4. action
        grad = Q.getGradient(null, as.get(3)).toArray();
        correct = new double[] {0,0,0,0,0,0,0,0,0,1,1,1};
        assertTrue(MathUtils.equalsDelta(grad, correct, 0.000000001));
        
        // false check
        grad = Q.getGradient(null, as.get(3)).toArray();
        correct = new double[] {1,0,1,0,0,0,0,0,0,1,1,1};
        assertTrue(!MathUtils.equalsDelta(grad, correct, 0.000000001));
    }
    
    /**
     * Test weight behavior
     */
    @Test
    public void testWeights()
    {
        final double DELTA = 0.0000001;
        DoubleMatrix1D w;
        double[] correct;
        
        // set weights to 0...11
        w = Q.getWeights();
        for( int i=0; i<w.size(); i++)
            w.set(i, i);
        Q.setWeights(w);
        
        // check for correct value calculation
        assertEquals(Q.getValue(null, as.get(0)), (double) 0+1+2, DELTA);
        assertEquals(Q.getValue(null, as.get(1)), (double) 3+4+5, DELTA);
        assertEquals(Q.getValue(null, as.get(2)), (double) 6+7+8, DELTA);
        assertEquals(Q.getValue(null, as.get(3)), (double) 9+10+11, DELTA);
        

        // increment all weights by 1
        Q.updateWeights(ones);
        w = Q.getWeights();        
        correct = new double[]{1,2,3,4,5,6,7,8,9,10,11,12};
        assertTrue(MathUtils.equalsDelta(w.toArray(), correct, 0.000001));
        
        // increment all weights by 2
        Q.updateWeightsScaled(2,ones);
        w = Q.getWeights();        
        correct = new double[]{3,4,5,6,7,8,9,10,11,12,13,14};
        assertTrue(MathUtils.equalsDelta(w.toArray(), correct, 0.000001));
        
    }
}

class DummyFeatureFunction extends FeatureFunction
{
    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureModifier#getFeatureVectorSize()
     */
    public int getFeatureVectorSize()
    {
        return 3;
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureModifier#getFeatures(cern.colt.matrix.DoubleMatrix1D)
     */
    public DoubleMatrix1D getFeatures(DoubleMatrix1D feat)
    {
        return new DenseDoubleMatrix1D(new double[]{1,1,1});
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureModifier#copy()
     */
    public FeatureFunction copy()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureFunction#hasVariableFeatureVectorSize()
     */
    @Override
    public boolean hasVariableFeatureVectorSize()
    {
        return false;
    }
    
}