package org.hswgt.teachingbox.core.rl.nfq.features.type;

import org.hswgt.teachingbox.core.rl.nfq.features.InputFeatures;

/**
 * This class constructs binary input features for learning in multi-layer perceptrons. 
 * The dimension is assumed to be an integer value (even though represented
 * by a double variable in the Teachingbox). Therefore the continuous valued variable
 * is casted to an integer, and a specified amount of bits 
 * is used for generating the features. 
 *   
 * @author Michel Tokic
 */
public class BinaryIntegerFeatures implements DimensionFeatures {
	
	/**
	 * Object serialization id 
	 */
	private static final long serialVersionUID = 6689470420592926046L;

	private int bits;
	
	/**
	 * The constructor
	 * @param bits The amount of bits used for feature generation
	 */
	public BinaryIntegerFeatures(int bits) {
		
		this.bits = bits;
	}
	

	@Override
	public double[] getFeatures(double variable) {
		
		double features[] = new double[bits];
		String sBits = Integer.toBinaryString((int)variable);
			
		for (int j=0; j<bits; j++) {
			if (j >= sBits.length()) {
				features[j] = InputFeatures.MIN_NEURON_ACT;
			} else {
				features[j] = sBits.toCharArray()[sBits.length()-j-1] == '0' ? InputFeatures.MIN_NEURON_ACT : InputFeatures.MAX_NEURON_ACT ;
			}
		}
		return features;
	}

	@Override
	public int getNumFeatures() {
		return this.bits;
	}
	
	public void debugFeatures(double variable) {
		
		double features[] = getFeatures(variable);
		
		// debug state features
		for (int j=0; j<bits; j++) {
			System.out.println ("  " + j + ": " + features[j]);
		}
	}
	
	public static void main(String[] args) {
		BinaryIntegerFeatures features = new BinaryIntegerFeatures (4);
		features.debugFeatures(4);
	}
}
