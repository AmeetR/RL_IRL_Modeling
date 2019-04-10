package org.hswgt.teachingbox.core.rl.nfq.features.type;

import org.apache.log4j.Logger;
import org.encog.mathutil.BoundNumbers;
import org.hswgt.teachingbox.core.rl.nfq.features.InputFeatures;
import org.hswgt.teachingbox.core.rl.nfq.util.NormUtil;

import com.google.common.base.Preconditions;

/**
 * This method scales a given double variable into the interval [MIN_NEURON_ACT, MAX_NEURON_ACT] 
 * @author Michel Tokic
 */
public class NormalizedFeatures implements DimensionFeatures {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4869859734975711982L;
	    

	// Logger
    private final static Logger log4j = Logger.getLogger("NormalizedFeatures");
    
    protected double minValue; 
    protected double maxValue;
    protected NormUtil norm;
    
    /** 
     * Constructor that configures the normalization object
     * @param minValue The minimum value
     * @param maxValue The maximum value
     */
    public NormalizedFeatures (double minValue, double maxValue) {
    	this.minValue = minValue;
    	this.maxValue = maxValue;
    	this.norm = new NormUtil(minValue, maxValue, InputFeatures.MIN_NEURON_ACT, InputFeatures.MAX_NEURON_ACT);
    }
    
    /** 
     * Constructor that configures the normalization object assuming values between 0 and 1
     */
    public NormalizedFeatures () {
    	this(0, 1);
    }

	@Override
	public double[] getFeatures(double variable) {
		
		Preconditions.checkArgument(variable >= minValue && variable <= maxValue,
				"variable=%s out of bounds [%s, %s]", variable, minValue, maxValue);
		
		return new double[] {
				Math.min(
						Math.max(this.norm.normalize(BoundNumbers.bound(variable)), InputFeatures.MIN_NEURON_ACT),
						InputFeatures.MAX_NEURON_ACT
				)
		};
	}

	@Override
	public int getNumFeatures() {
		return 1;
	}
	
	public static void main(String[] args) {
		NormalizedFeatures features = new NormalizedFeatures(-32, 32);
		
		System.out.println ("-32 => " + features.getFeatures(-32)[0]);
		System.out.println ("0 => " + features.getFeatures(0)[0]);
		System.out.println ("32 => " + features.getFeatures(32)[0]);
	}
}
