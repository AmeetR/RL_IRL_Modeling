package org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.transformer;

/**
 * Clips data to a specified boundaries.
 * 
 * @author Michel Tokic
 *
 */
public class ClipTransformer extends Transformer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7259207777227744148L;
	
	private double minimum;
	private double maximum;
	
	public ClipTransformer(double minimum, double maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}
	
	@Override
	public void computeParameters(double[][] data) {
		// TODO Auto-generated method stub
	}

	@Override
	public void transformDataVector(double[] data) {
		for (int i=0; i<data.length; i++) {
			data[i] = Math.min(Math.max(data[i], minimum), maximum);
		}
	}

	@Override
	public void backTransformDataVector(double[] data) {
		for (int i=0; i<data.length; i++) {
			data[i] = Math.min(Math.max(data[i], minimum), maximum);
		}
	}

	@Override
	public void init(int dimensions) {
		// TODO Auto-generated method stub
		
	}
}
