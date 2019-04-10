package org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform;

import java.io.Serializable;
import java.util.ArrayList;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.transformer.Transformer;

import com.google.common.base.Preconditions;

/**
 * This class transforms an array of data according to configured
 * transformation functions. 
 * 
 * @author Michel Tokic
 *
 */
public class DataTransformer implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8632627642044192863L;

	final private int dimensions;
	
	// transformation chain
	final ArrayList<Transformer> transformationChain = new ArrayList<Transformer>();

	/**
	 * Default constructor without data
	 * @param dimensions The amount of data columns
	 */
	public DataTransformer (int dimensions) {
		this.dimensions = dimensions;
	}
	
	/**
	 * Default constructor with data array
	 * @param dimensions The amount of data columns
	 * @param data The data array
	 */
	public DataTransformer (int dimensions, double data[][]) {
		this(dimensions);
		computeParameters(data);
	}
	
	/**
	 * computes the minimum and maximum values of the data array
	 * @param data The data array
	 */
	public void computeParameters(double data[][]) {
		
		Preconditions.checkNotNull(data, "data should not be null.");
		Preconditions.checkArgument(data.length>0, "data has zero rows.");
		
		double dataTransformed[][] = data.clone();
		
		// set bounds to all transformers
		for (int i=0; i< transformationChain.size(); i++) {
			transformationChain.get(i).computeParameters(dataTransformed);
		}
	}
	
	/**
	 * adds a data transformer to the transformation chain
	 * @param transformer The data transformer
	 */
	public void addTransformer (Transformer transformer) {
		this.transformationChain.add(transformer);
		transformer.init(dimensions);
	}
	
	/**
	 * transforms a single vector of data (in place)
	 * @param data The data vector to process (in place)
	 */
	public void transformDataVector (double data[]) {
		Preconditions.checkNotNull(data, "data array is null");
		Preconditions.checkArgument(data.length > 0, "data arrays has no columns");
		for (int i=0; i<transformationChain.size(); i++) {
			transformationChain.get(i).transformDataVector(data);
		}		
	}
	
	/**
	 * transforms an array of data (in place)
	 * @param data The array of data (in place)
	 */
	public void transformDataArray (double data[][]) {
		Preconditions.checkNotNull(data, "data array is null");
		Preconditions.checkArgument(data.length > 0, "data arrays has no rows");
		for (int i=0; i<transformationChain.size(); i++) {
			transformationChain.get(i).transformDataArray(data);
		}
	}

	/**
	 * back transforms a single vector of data 
	 * @param data The data vector to process (in place)
	 */
	public void backTransformDataVector (double data[]) {
		Preconditions.checkNotNull(data, "data array is null");
		Preconditions.checkArgument(data.length > 0, "data arrays has no columns");
		for (int i=transformationChain.size()-1; i>=0; i--) {
			transformationChain.get(i).backTransformDataVector(data);
		}		
	}
	
	/**
	 * back transforms an array of data in place
	 * @param data The array of data to process
	 */
	public void backTransformDataArray (double data[][]) {
		Preconditions.checkNotNull(data, "data array is null");
		Preconditions.checkArgument(data.length > 0, "data arrays has no rows");
		for (int i=transformationChain.size()-1; i>=0; i--) {
			transformationChain.get(i).backTransformDataArray(data);
		}		
	}
	
	/**
	 * returns the transformation chain
	 * @return the transformation chain
	 */
	public ArrayList<Transformer> getTransformationChain() {
		return this.transformationChain;
	}
}
