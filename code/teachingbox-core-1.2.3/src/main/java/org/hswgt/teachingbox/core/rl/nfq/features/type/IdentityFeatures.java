package org.hswgt.teachingbox.core.rl.nfq.features.type;


public class IdentityFeatures implements DimensionFeatures {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5916386495315055261L;
	
	private double minValue = Double.POSITIVE_INFINITY;
	private double maxValue = Double.NEGATIVE_INFINITY;
	
	@Override
	public double[] getFeatures(double variable) {

		if (variable < minValue) {
			minValue = variable;
		}
		if (variable > maxValue) {
			maxValue = variable;
		}
		return new double[]{variable};
	}

	@Override
	public int getNumFeatures() {		
		return 1;
	}
}
