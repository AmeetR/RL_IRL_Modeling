package org.hswgt.teachingbox.core.rl.nfq.features;

import java.io.Serializable;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.nfq.features.type.DimensionFeatures;

import com.google.common.base.Preconditions;

/**
 * This is a generic interface for working with neural network input features.
 * @author Michel Tokic
 */
public class InputFeatures implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3682439856750375995L;
	
	private LinkedList <DimensionFeatures> stateFeatures = new LinkedList<DimensionFeatures>();
	private LinkedList <DimensionFeatures> actionFeatures = new LinkedList<DimensionFeatures>();
	private int numInputFeatures = 0;
		
	/**
	 * The minimum activation of a neuron
	 */
	public static final double MIN_NEURON_ACT = 0.05;
	
	/**
	 * The maximum activation of a neuron
	 */
	public static final double MAX_NEURON_ACT = 0.95;
	
    // Logger
    private final static Logger log4j = Logger.getLogger("InputFeatures");
	
	/**
	 * This method adds a new DimensionFeature to the set of state features
	 * @param dimensionFeatures The DimensionFeatures object
	 */
	public void addStateFeatures (DimensionFeatures dimensionFeatures) {
		this.stateFeatures.add(dimensionFeatures);
		this.numInputFeatures += dimensionFeatures.getNumFeatures();
		log4j.debug("numInputFeatuers=" + this.numInputFeatures);
	}

	/**
	 * This method adds a new DimensionFeature to the set of action features
	 * @param dimensionFeatures The DimensionFeatures object
	 */
	public void addActionFeatures (DimensionFeatures dimensionFeatures) {
		this.actionFeatures.add(dimensionFeatures);
		this.numInputFeatures += dimensionFeatures.getNumFeatures();
		log4j.debug("numInputFeatuers=" + this.numInputFeatures);
	}


	/**
	 * This method returns the neural network input features for a given state/action pair 
	 * @param s The state
	 * @param a The action
	 * @return The feature vector
	 */
	public double[] getInputFeatures(State s, Action a) {
		// generate features 
		double features[] = new double[this.numInputFeatures];
		this.getInputFeatures (s, a, features);
		return features;
	}

	/**
	 * This method writes the neural network input features for a given 
	 * state/action pair into the featureVector 
	 * @param s The state
	 * @param a The action
	 * @param featureVector The feature vector
	 */
	public void getInputFeatures(State s, Action a, double featureVector[]) {

		Preconditions.checkNotNull(featureVector, "feature vector should not be null");
		Preconditions.checkArgument(featureVector.length == this.numInputFeatures, 
				"expected length of featureVector=%s, but is %s", this.numInputFeatures, featureVector.length);

		int offset = 0;
		
		// add all state features
		for (int i=0; i<stateFeatures.size(); i++) {
			double features[] = stateFeatures.get(i).getFeatures(s.get(i));
			//System.out.println ("processing features of dimension " + i + ", numFeatures=" + stateFeatures.get(i).getNumFeatures());
			for (int j=0; j<stateFeatures.get(i).getNumFeatures(); j++) {
				
				featureVector[offset+j] = features[j];
			}
			offset += stateFeatures.get(i).getNumFeatures();
		}
		
		// add all action features
		for (int i=0; i<actionFeatures.size(); i++) {
			double features[] = actionFeatures.get(i).getFeatures(a.get(i));
			for (int j=0; j<actionFeatures.get(i).getNumFeatures(); j++) {
				featureVector[offset+j] = features[j];
			}
			offset += actionFeatures.get(i).getNumFeatures();
		}
	}

	
	public int getNumInputFeatures() {
		return this.numInputFeatures;
	}
	
	/**
	 * This method debugs the features of s and a to the console
	 * @param state The state
	 * @param action The action
	 */
	public void debugFeatures(State state, Action action) {
		
		int offset = 0;
		double features[] = new double[this.numInputFeatures];
		this.getInputFeatures(state, action, features);
		
		for (int i=0; i<stateFeatures.size(); i++) {
			System.out.println ("State dimension " + i + ": ");
			for (int j=0; j<stateFeatures.get(i).getNumFeatures(); j++) {
				System.out.println ("  " + (offset+j) + ": " + features[offset+j]);
			}			
			offset += stateFeatures.get(i).getNumFeatures();
		}
		
		for (int i=0; i<actionFeatures.size(); i++) {
			System.out.println ("Action dimension " + i + ": ");
			for (int j=0; j<actionFeatures.get(i).getNumFeatures(); j++) {
				System.out.println ("  " + (offset+j) + ": " + features[offset+j]);
			}
			offset += actionFeatures.get(i).getNumFeatures();
		}
	}
	
	/**
	 * returns the amount of state dimensions
	 * @return The amount of state dimensions
	 */
	public int getNumStateDimensions() {
		return stateFeatures.size();
	}
	
	/**
	 * returns the amount of action dimensions
	 * @return The amount of action dimensions
	 */
	public int getNumActionDimensions() {
		return actionFeatures.size();
	}
}
