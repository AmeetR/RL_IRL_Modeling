package org.hswgt.teachingbox.core.rl.nfq.features.type;

import java.io.Serializable;

/**
 * This interfaces provides necessary methods for creating features of state or action dimensions. 
 * @author Michel Tokic
 */
public interface DimensionFeatures extends Serializable {

	/**
	 * returns an array of input activations for the given state or action variable
	 * @param variable The variable
	 * @return The features
	 */
	public double[] getFeatures(double variable);
	
	/**
	 * returns the amount of features for this dimension
	 * @return The amount of features
	 */
	public int getNumFeatures();
}
