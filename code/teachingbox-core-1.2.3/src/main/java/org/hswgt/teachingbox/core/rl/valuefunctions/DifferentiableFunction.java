package org.hswgt.teachingbox.core.rl.valuefunctions;

import cern.colt.matrix.DoubleMatrix1D;

public interface DifferentiableFunction
{
	/**
	 * Return a copy of the weights
	 * @return The weight matrix
	 */
	public DoubleMatrix1D getWeights();
	
	/**
	 * Set weights for the function
	 * @param weights The weight matrix
	 */
	public void setWeights(final DoubleMatrix1D weights);
	
	/**
	 * Performs weights = weights + delta
	 * @param delta The delta
	 */
	public void updateWeights(final DoubleMatrix1D delta);
	
	/**
     * Performs weights = weights + scalefactor*delta
     * @param delta The delta
     * @param scalefactor The scale factor
     */
    public void updateWeightsScaled(double scalefactor, final DoubleMatrix1D delta);
	
	/**
	 * @return Length of weights vector
	 */
	public int getWeightVectorSize();
	
	/**
     * @return Length of gradient
     */
    public int getGradientSize();
}
