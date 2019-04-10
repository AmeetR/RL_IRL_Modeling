package org.hswgt.teachingbox.core.rl.feature;

import org.hswgt.teachingbox.core.rl.tools.MathUtils;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/**
 * The Discretizer takes an vector with continuous  values
 * and returns the discrete values of them.
 * 
 * It takes an n by 3 double array as configuration.
 * For each of the (n) dimension you have to specify the
 * range of the continuous variable and the number 
 * discrete values to use for this variable.
 * 
 * Usage Example:
 * <pre>
       double[][] config = new double[][]{
                { 0, 1, 5}, // [0,1] in 5 discrete values
                {-2, 2, 4}, // [-2,2] in 4 discrete values
                {-2, -1, 2} // [-2, -1] in 2 discrete values
        };
        
        Discretizer discretizer = new Discretizer(config);
 * </pre>
 */
public class Discretizer extends FeatureFunction
{
    private static final long serialVersionUID = -8549640514366528265L;
    protected int nDimensions;
    protected double[][] config;
    protected final int FROM  = 0;
    protected final int TO    = 1;
    protected final int DISCS = 2;
    
    /**
     * Constructor
     * It takes an n by 3 double array as configuration.
     * For each of the (n) dimension you have to specify the
     * range of the continuous variable and the number 
     * discrete values to use for this variable.
     * @param config The configuration
     */
    public Discretizer(double[][] config)
    {
        this.nDimensions = config.length;
        this.config = config.clone();
    }
    
    
    /**
     * Copy Constructor
     * @param other The Discretizer to copy from
     */
    public Discretizer(Discretizer other)
    {
        this.nDimensions = other.nDimensions;
        this.config = other.config.clone();
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureFunction#copy()
     */
    public FeatureFunction copy()
    {
        return new Discretizer(this);
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureFunction#getFeatureVectorSize()
     */
    public int getFeatureVectorSize()
    {
        return nDimensions;
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureFunction#getFeatures(cern.colt.matrix.DoubleMatrix1D)
     */
    public DoubleMatrix1D getFeatures(DoubleMatrix1D feat)
    {
        DoubleMatrix1D s = new DenseDoubleMatrix1D(getFeatureVectorSize());
        for(int dim=0; dim<nDimensions; dim++){
            final double continousValue = feat.get(dim);
            final int value = getDiscreteValue(continousValue, config[dim][FROM], config[dim][TO], (int) config[dim][DISCS]);
            s.set(dim, value);
        }
        return s;
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureFunction#hasVariableFeatureVectorSize()
     */
    public boolean hasVariableFeatureVectorSize()
    {
        return false;
    }

    /**
     * Discretizes a continuous variable
     * @param continousValue The continuous value
     * @param from The range were the continuous variable starts
     * @param to The range were the continuous variable ends
     * @param discs Number of discrete values
     * @return the discrete value
     */
    public static int getDiscreteValue(double continousValue, double from, double to, int discs)
    {
        if( from > to )
            throw new IllegalArgumentException("The `from` must not be > than `to`");
        if( discs <= 0 )
            throw new IllegalArgumentException("The number of discrete values must be >0");
        
        final double intervall = (to-from)/discs;
        double index = (continousValue-from)/intervall;
        index = MathUtils.setLimits(index, 0, discs-1);
        return (int) index;
    }
}
