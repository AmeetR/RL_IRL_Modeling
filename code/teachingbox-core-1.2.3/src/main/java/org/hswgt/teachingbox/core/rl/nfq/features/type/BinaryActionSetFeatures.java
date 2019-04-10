package org.hswgt.teachingbox.core.rl.nfq.features.type;

import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.nfq.features.InputFeatures;

/**
 * This class associates each Action from the ActionSet to a single neuron. 
 * The activitiy of the neuron for the taken action (assuming one action variable) is set to 
 * "MAX_NEURON_ACT". All other neurons have activity "MIN_NEURON_ACT".
 * @author Michel Tokic
 */
public class BinaryActionSetFeatures implements DimensionFeatures {

	private static final long serialVersionUID = 7892467497195410105L;
	protected final ActionSet actionSet;
	
	/**
	 * The constructor
	 * @param actionSet The action set
	 */
	public BinaryActionSetFeatures (ActionSet actionSet) {
		this.actionSet = actionSet;
		
		System.out.println ("Constructing BinaryActionSetFeatures with " + this.actionSet.size() + " features. ");		
	}
	

	@Override
	public double[] getFeatures(double action) {
		
		double features[] = new double[actionSet.size()];
		
		for (int i=0; i< actionSet.size(); i++) {
			if (actionSet.get(i).get(0) == action) {
				features[i] = InputFeatures.MAX_NEURON_ACT;
			} else {
				features[i] = InputFeatures.MIN_NEURON_ACT;
			}
		}
		
		return features;
	}
	
	/**
	 * debugs features of an action
	 * @param action The action
	 */
	public void debugFeatures (Action action) {
		
		double features[] = getFeatures(action.get(0));
		
		// debug state features
		for (int j=0; j<getNumFeatures(); j++) {
			System.out.println ("  " + j + ": " + features[j]);
		}
	}

	@Override
	public int getNumFeatures() {
		return actionSet.size();
	}
}
