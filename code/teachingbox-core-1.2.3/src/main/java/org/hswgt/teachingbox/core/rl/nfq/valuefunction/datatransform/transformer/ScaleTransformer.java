package org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.transformer;

/**
 * Scales data into a desired interval.
 * 
 * @author Michel Tokic
 *
 */
public class ScaleTransformer extends Transformer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6576622696010822543L;
	
	private double scaleA[]; 
	private double scaleB[];
	
	private double targetMinimum;
	private double targetMaximum;
	
	public ScaleTransformer(double targetMinimum, double targetMaximum) {
		this.targetMinimum = targetMinimum;
		this.targetMaximum = targetMaximum;
	}
	
	double min[] = null;
	double max[] = null;
	
	/**
	 * Computes any transformation relevant parameters. 
	 * @param data The data array
	 */
	@Override
	public void computeParameters (double data[][]) {
		
		// init min/max arrays
		this.min = new double[data[0].length];
		this.max = new double[data[0].length];
		for (int col=0; col<data[0].length; col++) {
			this.min[col] = Double.POSITIVE_INFINITY;
			this.max[col] = Double.NEGATIVE_INFINITY;
		}
		
		// compute min/max values
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				if (data[row][col] < min[col]) {
					min[col] = data[row][col];
				}
				if (data[row][col] > max[col]) {
					max[col] = data[row][col];
				}
			}
		}
	
		scaleA = new double[data[0].length];
		scaleB = new double[data[0].length];
		
		// compute scaling parameters
		final double actDiff = targetMaximum-targetMinimum; 
		for (int i=0; i<data[0].length; i++) {
			this.scaleA[i] = actDiff / (max[i] - min[i]);
			this.scaleB[i] = -((min[i] * actDiff) / (max[i]-min[i])) + targetMinimum;
		}
	}
	
	@Override
	public void transformDataVector(double[] data) {
		for (int i=0; i<data.length; i++) {
			data[i] = (data[i]*scaleA[i]) + scaleB[i];
		}
	}

	@Override
	public void backTransformDataVector(double[] data) {
		for (int i=0; i<data.length; i++) {
			data[i] = (data[i]-scaleB[i]) / scaleA[i];
		}
	}

	@Override
	public void init(int dimensions) {
		if (this.scaleA == null || this.scaleA.length != dimensions) {
			this.scaleA = new double[dimensions];
			this.scaleB = new double[dimensions];
			for (int i=0; i< dimensions; i++) {
				this.scaleA[i] = 1;
				this.scaleB[i] = 0;
			}
		}
	}
}
