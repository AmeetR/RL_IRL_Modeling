package org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.transformer;

import java.io.Serializable;

import com.google.common.base.Preconditions;

/**
 * This interface specifies relevant functions for a data transformer
 * 
 * @author Michel Tokic
 *
 */
public abstract class Transformer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3646566864558970604L;
	
	/**
	 * Allocates any data arrays (min/max/std/mean etc...) with default values 
	 * @param dimensions The amount of data columns
	 */
	abstract public void init(int dimensions); 
	
	/**
	 * Computes any transformation relevant parameters. 
	 * @param data The data array
	 */
	abstract public void computeParameters(double data[][]);
	
	/**
	 * transforms a single vector of data 
	 * @param data The data vector to process (in place)
	 */
	abstract public void transformDataVector (double data[]);
	
	/**
	 * transforms an array of data in place
	 * @param data The array of data to process
	 */
	public void transformDataArray (double data[][]) {
		Preconditions.checkNotNull(data, "data array is null");
		Preconditions.checkArgument(data.length > 0, "data arrays has no rows");
		
		for (int row=0; row<data.length; row++) {
			transformDataVector(data[row]);
		}
	}

	/**
	 * back transforms a single vector of data 
	 * @param data The data vector to process (in place)
	 */
	abstract public void backTransformDataVector (double data[]);
	
	/**
	 * back transforms an array of data in place
	 * @param data The array of data to process
	 */
	public void backTransformDataArray (double data[][]) {
		Preconditions.checkNotNull(data, "data array is null");
		Preconditions.checkArgument(data.length > 0, "data arrays has no rows");
		
		for (int row=0; row<data.length; row++) {
			backTransformDataVector(data[row]);
		}
	}
}
