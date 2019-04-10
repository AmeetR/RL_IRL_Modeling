
package org.hswgt.teachingbox.core.rl.datastructures;

import org.hswgt.teachingbox.core.rl.feature.FeatureFunction;
import org.hswgt.teachingbox.core.rl.feature.FeatureFunctionObserver;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/*
 * WeightsVector represents the structure which is used by the
 * QFeatureFunction or an eligibility trace to store values/weights for each
 * action of an action set. Weights/values for an action are stored in chunks.
 * That is the first chunk contains weights for the first action for all features,
 * the second chunk contains weigths fot the second action for all features,... .
 * The advantage of WeightsVector over a DoubleMatrix1D is that WeightsVector
 * will resize itself correctly when needed. This is needed for adaptive methods.
 *
 * Example: If you have two actions and a feature vector of size 3, then the
 * first three values of the weights vector correspond to the first action (each weight
 * corresponds to one feature entry) and the next 3 to the second action.
 */
public class WeightsVector implements FeatureFunctionObserver {
    /**
	 * 
	 */
	private static final long serialVersionUID = -9165106051808991819L;

	protected DoubleMatrix1D weights;

    /**
     * WeightsVector needs a fixed ActionSet.
     */
    protected ActionSet actionSet = new ActionSet();

    /**
     * The feature function that is used to calculate the feature vector for all
     * actions
     */
    protected FeatureFunction featureFunction;

    /**
     * Constructor, you have to pass in the FeatureFunction and the ActionSet!
     * @param featureFunction The @FeatureFunction
     * @param actionSet The @ActionSet
     */
    public WeightsVector(FeatureFunction featureFunction, ActionSet actionSet) {
        this.featureFunction = featureFunction;
        this.featureFunction.addObserver(this);

        this.actionSet.addAll(actionSet);
        this.weights = new DenseDoubleMatrix1D(
                actionSet.size() * featureFunction.getFeatureVectorSize());
    }

    
    public void updateFeatureVectorSize(int newSize) {
        // This function does nothing because updateFeatureAdded and
        // updateFeatureRemoved take care of resizing the weights vector.
    }

    // TODO: updateFeatureAdded and updateFeatureRemoved look pretty symmetric,
    // maybe these two functions can be refactored to use some common code
    // TODO: Test updateFeatureAdded and updateFeatureRemoved in unittests!

    // updateFeatureAdded resizes the weights correctly so that weights
    // will be located at the correct index with respect to the feature (net)
    public void updateFeatureAdded(int index) {
        // for all actions add a matrix-element at the corresponding index
        int featureSize = featureFunction.getFeatureVectorSize();
        // create new weight vector
        DoubleMatrix1D nWeights = this.weights.like(actionSet.size() * featureSize);
        
        for (int oldWeightsIndex=0, newWeightsIndex=0; oldWeightsIndex<this.weights.size();
                oldWeightsIndex+=featureSize-1,newWeightsIndex+=featureSize) {
            DoubleMatrix1D oldWeights = this.weights.viewPart(oldWeightsIndex, featureSize - 1);
            DoubleMatrix1D newWeights = nWeights.viewPart(newWeightsIndex, featureSize);

            // feature added to the end of the featureVector
            if (index + 1 == newWeights.size()) {
                newWeights.viewPart(0, index).assign(oldWeights);
            }
            // feature added to the beginning of the featureVector
            else if(index == 0) {
                newWeights.viewPart(1, newWeights.size()-1).assign(oldWeights);
            }
            // feature added somewhere inside the feature vector
            else {
                // copy old values to the corresponding indexes
                newWeights.viewPart(0, index).assign(oldWeights.viewPart(0, index));
                newWeights.viewPart(index+1, newWeights.size()-(index+1)).assign(
                        oldWeights.viewPart(index, oldWeights.size()-index));
            }
        }
        this.weights = nWeights;
    }

    // updateFeatureRemoved resizes the weights correctly so that weights
    // will be located at the correct index with respect to the feature (net)
    public void updateFeatureRemoved(int index) {
        int featureSize = featureFunction.getFeatureVectorSize();
        DoubleMatrix1D nWeights = this.weights.like(actionSet.size() * featureSize);
        
        for (int i=0,j=0; i<this.weights.size(); i+=featureSize+1,j+=featureSize) {
            DoubleMatrix1D oldWeights = this.weights.viewPart(i, featureSize + 1);
            DoubleMatrix1D newWeights = nWeights.viewPart(j, featureSize);

            // feature removed from the end of the featureVector
            if (index == newWeights.size()) {
                newWeights.assign(oldWeights.viewPart(0, index));
            }
            // feature removed from the beginning of the featureVector
            else if(index == 0) {
                newWeights.assign(oldWeights.viewPart(1, oldWeights.size()-1));
            }
            // feature removed somewhere inside the feature vector
            else {
                // copy old values to the corresponding indexes
                newWeights.viewPart(0, index).assign(oldWeights.viewPart(0, index));
                newWeights.viewPart(index, newWeights.size()-index).assign(
                        oldWeights.viewPart(index+1, oldWeights.size()-(index+1)));
            }
        }
        this.weights = nWeights;
    }

    // getter and setter

    public DoubleMatrix1D getWeights() {
        return this.weights.copy();
    }

    public void setWeights(DoubleMatrix1D newWeights) {
        this.weights.assign(newWeights.copy());
    }
}