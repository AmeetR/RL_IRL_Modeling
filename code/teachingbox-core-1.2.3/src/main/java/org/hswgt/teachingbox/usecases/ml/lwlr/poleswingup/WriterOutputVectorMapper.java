package org.hswgt.teachingbox.usecases.ml.lwlr.poleswingup;

import org.hswgt.teachingbox.core.rl.datastructures.VectorMapper;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/**
 * Maps the output vector (vector later to be predicted) in the
 * VectorDatasetWriter when writing PoleSwingUpEnvironment data sets.
 *  
 * @author Richard Cubek
 *
 */
public class WriterOutputVectorMapper implements VectorMapper
{
	/**
	 * @param vector State(t+1) vector of PoleSwingUpEnvironment as [theta, thetad].
	 * @return The state vector as used for the prediction later as 
	 * [sin(theta), cos(theta), thetad].
	 */
	public DenseDoubleMatrix1D getMappedVector(DenseDoubleMatrix1D vector) 
	{
		DenseDoubleMatrix1D outputVector = new DenseDoubleMatrix1D(3);
		// map theta to sin(theta) and cos(theta)
		outputVector.set(0, Math.sin(vector.get(0)));
		outputVector.set(1, Math.cos(vector.get(0)));
		// thetad moves in the position
		outputVector.set(2, vector.get(1));
		
		return outputVector;
	}
}
