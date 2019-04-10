package org.hswgt.teachingbox.core.rl.nfq.features.type;

import org.hswgt.teachingbox.core.rl.nfq.features.InputFeatures;

/**
 * This class discretizes a given state or action variable. Features from index 0 until the 
 * feature of the queried state or action variable are set to MAX_NEURON_ACT. All others are 
 * set to MIN_NEURON_ACT.
 * @author Michel Tokic
 */
public class DiscretizedTemperatureFeatures extends DiscretizedFeatures {

	private static final long serialVersionUID = 6741431492178845836L;

	public DiscretizedTemperatureFeatures(double minValue, double maxValue,
			int numDiscs) {
		super(minValue, maxValue, numDiscs);
	}
	
	@Override
	public double[] getFeatures(double variable) {
		
		double features[] = new double[numDiscs];
	
		// determine grid position
		int gridPos = Math.max(Math.min((int)((variable-minValue) / (discWidth)), numDiscs-1), 0);
	
		for (int j=0; j<numDiscs; j++) {
			
			if (j <= gridPos) {
				features[j] = InputFeatures.MAX_NEURON_ACT;
			} else {
				features[j] = InputFeatures.MIN_NEURON_ACT;				
			}
		}
		
		return features;
	}
	
	public static void main(String[] args) {
		DiscretizedTemperatureFeatures df = new DiscretizedTemperatureFeatures(-1, 1, 5);
		df.debugFeatures(-5);
		df.debugFeatures(-1);
		df.debugFeatures(0);
		df.debugFeatures(1);
		df.debugFeatures(5);
	}
}
