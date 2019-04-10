
package org.hswgt.teachingbox.usecases.ml.lwlr.poleswingup;

import org.hswgt.teachingbox.core.rl.datastructures.VectorMapper;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/**
 * Maps predicted state vector from the TransitionFunction in the 
 * LWLR_PoleSwingUpModel to the State used in PoleSwingUpEnvironment.
 * 
 * @author Richard Cubek
 *
 */
public class PredictionOutputVectorMapper implements VectorMapper 
{
	/**
	 * @param vector Predicted PoleSwingUp state from LWLR classifier
	 * as [sin(theta), cos(theta), thetad]
	 * @return State in the known PoleSwingUp State format [theta, thetad].
	 */
	public DenseDoubleMatrix1D getMappedVector(DenseDoubleMatrix1D vector) 
	{
		DenseDoubleMatrix1D outputVector = new DenseDoubleMatrix1D(2);
		outputVector.set(0, convertToTheta(vector.get(0), vector.get(1)));
		outputVector.set(1, vector.get(2));
		
		return outputVector;
	}

	// convert sin(theta) and cos(theta) back to theta
	private double convertToTheta(double sinTheta, double cosTheta)
	{
		/* lwr can predict cosTheta > 1 or < -1, which will lead to an undefined theta !
		 * KDTree will through Exceptions "missing values", so... */
		if (cosTheta > 1)
			cosTheta = 1;
		if (cosTheta < -1)
			cosTheta = -1;
		
		/* to understand this, type: "plot [-pi:pi] sin(x),cos(x),asin(sin(x)),acos(cos(x))"
		 * in gnuplot (or where else) */
		if (sinTheta < 0) // if sin(theta) < 0
		{
			return -Math.acos(cosTheta); // theta = -arccos(cos(theta))
		}
		else // if sin(theta) >= 0
		{
			return Math.acos(cosTheta); // theta = arccos(cos(theta))			
		}
	}
}
