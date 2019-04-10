package org.hswgt.teachingbox.core.rl.datastructures;

import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.DifferentiableVFunction;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/**
 * Calculates the numerical gradient for any VFunction
 */
public class NumericalGradientCalculator
{
    protected DifferentiableVFunction f; // TODO: Change to ParameterizedFunction
    protected double alpha;
    
    /**
     * Constructor
     * @param f The function to calculate the num. gradient for
     * @param alpha The stepsize param. for calculation
     */
    public NumericalGradientCalculator(DifferentiableVFunction f, double alpha)
    {
        this.f = f;
        this.alpha = alpha;
    }
        
    /**
     * Constructor 
     * @param f A @DifferentiableVFunction
     */
    public NumericalGradientCalculator(DifferentiableVFunction f)
    {
        this(f, 0.0001);
    }
    
    /**
     * Calulate the numerical gradient at position x
     * @param x The @State
     * @return The gradient
     */
    public DoubleMatrix1D getGradient(final State x)
    {
        return getGradient(f, x, alpha);
    }
    
    /**
     * Calulate the numerical gradient at position x
     * @param f The function to calculate the num. gradient for
     * @param alpha The stepsize param. for calculation
     * @param x The input vector
     * @return The gradient
     */
    public static DoubleMatrix1D getGradient(DifferentiableVFunction f, final State x, double alpha)
    {
        DoubleMatrix1D w = f.getWeights();
        DoubleMatrix1D grad = new DenseDoubleMatrix1D(w.size());

        // TODO: make weight update faster
        
        for(int i=0;  i<w.size(); i++){
            // calculate x = x + ei*alpha
            w.set(i, w.get(i) + alpha);
            f.setWeights(w);
            double yp = f.getValue(x);
            
            // calculate x = x - ei*alpha
            w.set(i, w.get(i) - 2*alpha);
            f.setWeights(w);
            double ym = f.getValue(x);
            
            // reset x
            w.set(i, w.get(i) + alpha);
            f.setWeights(w);
            
            // calculate gradient
            double y = (yp - ym)/(2*alpha);
            grad.set(i,y);
        }
                    
        return grad;
    }
}
