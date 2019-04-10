package org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.transformer;

/**
 * Transforms data according to a logistic function
 * 
 * @author Michel Tokic
 *
 */
public class LogisticTransformer extends Transformer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2257199948692749036L;

	@Override
	public void computeParameters(double[][] data) {
		// TODO Auto-generated method stub
	}

	@Override
	public void transformDataVector(double[] data) {
		for (int i=0; i<data.length; i++) {
			data[i] = 1. / (1. + Math.exp(-data[i]));
		}
	}

	@Override
	public void backTransformDataVector(double[] data) {
		for (int i=0; i<data.length; i++) {
			data[i] = -Math.log((1./data[i]) - 1.);
		}
	}

	@Override
	public void init(int dimensions) {
		// TODO Auto-generated method stub
	}
}
