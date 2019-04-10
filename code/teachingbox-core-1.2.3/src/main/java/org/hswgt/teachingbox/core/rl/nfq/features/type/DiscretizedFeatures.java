package org.hswgt.teachingbox.core.rl.nfq.features.type;

import java.io.Serializable;

import org.hswgt.teachingbox.core.rl.nfq.features.InputFeatures;

import com.google.common.base.Preconditions;

/**
 * This class discretizes a given state or action variable. The cell containing the state or 
 * action variable is set to MAX_NEURON_ACT, all others are set to MIN_NEURON_ACT.
 * @author Michel Tokic
 */
public class DiscretizedFeatures implements DimensionFeatures, Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -6732610447795717240L;
	
	protected double minValue; 
    protected double maxValue;
    protected final int numDiscs;
    protected final double discWidth;
    
    /**
     * Constructor that initializes 
     * @param minValue The minimum value this dimension can have
     * @param maxValue The maximum value this dimension can have
     * @param numDiscs The amount of partitions within [minValue, maxValue]
     */
    public DiscretizedFeatures (double minValue, double maxValue, int numDiscs) {
    	this.minValue = minValue;
    	this.maxValue = maxValue;
    	this.numDiscs = numDiscs;
    	this.discWidth = (maxValue - minValue) / (double)numDiscs;
    }

	@Override
	public double[] getFeatures(double variable) {

		Preconditions.checkArgument(variable >= minValue && variable <= maxValue, "variable=%s not in range [%s, %s]", variable, minValue, maxValue);

		double features[] = new double[numDiscs];
		for (int j=0; j<numDiscs; j++) {
			features[j] = InputFeatures.MIN_NEURON_ACT;
		}

		// determine grid position
		int gridPos = Math.max(Math.min((int)((variable-minValue) / (discWidth)), (numDiscs-1)), 0);
		
		//System.out.println ("GridPos: " + gridPos);
		features[gridPos] = InputFeatures.MAX_NEURON_ACT;
		
		return features;
	}

	@Override
	public int getNumFeatures() {
		return this.numDiscs;
	}
	
	/** 
	 * This function debugs the features "variable" to the console
	 * @param variable The variable to debug
	 */
	public void debugFeatures(double variable) {
		double[] features = this.getFeatures(variable);
		System.out.println ("Debugging features for variable '" + variable + 
						 	"' (min=" + minValue + ", max=" + maxValue + ", discs=" + numDiscs + ")");
		for (int i=0; i<this.numDiscs; i++) {
			System.out.println ("Feature " + i + ": " + features[i]);
		}
		System.out.println ();
	}
	
	
	public static void main(String[] args) {
		DiscretizedFeatures df = new DiscretizedFeatures(-1, 1, 5);
		df.debugFeatures(-1);
		df.debugFeatures(0);
		df.debugFeatures(1);
	}
}
