
package org.hswgt.teachingbox.usecases.ml.lwlr.poleswingup;

import org.hswgt.teachingbox.core.rl.datastructures.VectorMapper;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.viz.pole.PoleSwingUp2dWindow;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/**
 * Maps known PoleSwingUp state vector (+ action) to vector used for prediction 
 * in the TransitionFunction AND maps incoming states in the VectorDatasetWriter
 * (for writing the data set file).
 * 
 * @author Richard Cubek
 *
 */
public class QueryVectorMapper implements VectorMapper
{
	/**
	 * Map vector as described in class description.
	 * @param vector State in the known format [theta, thetad, force] 
	 * @return State + action in format for LWLR classifier or date set writer 
	 * [sin(theta), cos(theta), thetad, force]
	 */
	public DenseDoubleMatrix1D getMappedVector(DenseDoubleMatrix1D vector)
	{
		DenseDoubleMatrix1D incomingVector = new DenseDoubleMatrix1D(4);
		// map theta to sin(theta) and cos(theta)
		incomingVector.set(0, Math.sin(vector.get(0)));
		incomingVector.set(1, Math.cos(vector.get(0)));
		// thetad and force move in their index
		incomingVector.set(2, vector.get(1));
		// instead of the force, we write the corresponding action index (nominal class)!
		incomingVector.set(3, PoleSwingUp2dWindow.ACTION_SET.indexOf(
										new Action(new double[]{vector.get(2)})));
		
		return incomingVector;
	}
}