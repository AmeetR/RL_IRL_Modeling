package org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.transformer;

import org.hswgt.teachingbox.core.rl.nfq.util.ZTransformation;

import com.google.common.base.Preconditions;

/**
 * transforms data according to a Z-score transformation
 * @author Michel Tokic
 *
 */
public class ZTransformer extends Transformer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2590332417975041355L;
	
	// memory for minimum and maximum bounds
	private double means[] = null;
	private double std[] = null;
	
	@Override
	public void computeParameters(double[][] data) {
		// compute mean and standard deviation
		this.means = ZTransformation.getMean(data).clone();
		this.std = ZTransformation.getStd(data, means).clone();
		//System.out.println("mean=" + ArrayUtils.toString(means) + ", std=" + ArrayUtils.toString(std));
	}

	@Override
	public void transformDataVector(double[] data) {
		Preconditions.checkNotNull(data, "data is null");
		for (int i=0; i<data.length; i++) {
			//data[i] = ZTransformation.transformValue(data[i], means[i], std[i]);
			data[i] = (data[i] - means[i]) / std[i];
		}
	}

	@Override
	public void backTransformDataVector(double[] data) {
		for (int i=0; i<data.length; i++) {
			//data[i] = ZTransformation.transformBackValue(data[i], means[i], std[i]);
			data[i] = (data[i]*std[i]) + means[i];
		}
	}

	@Override
	public void init(int dimensions) {
		if (this.means == null || this.means.length != dimensions) {		
			this.means = new double[dimensions];
			this.std = new double[dimensions];
			
			for (int i=0; i< dimensions; i++) {
				this.means[i] = 0;
				this.std[i] = 1;
			}
		}
	}
}