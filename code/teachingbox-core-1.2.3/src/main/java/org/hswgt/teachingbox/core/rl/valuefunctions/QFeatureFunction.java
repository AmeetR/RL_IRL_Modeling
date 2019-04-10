/**
 * 
 * $Id: QFeatureFunction.java 988 2015-06-17 19:48:01Z micheltokic $
 * 
 * @version $Rev: 988 $
 * @author $Author: micheltokic $
 * @date $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 * 
 */

package org.hswgt.teachingbox.core.rl.valuefunctions;

import org.hswgt.teachingbox.core.rl.datastructures.ActionFilter;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.linalg.SeqBlas;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.datastructures.WeightsVector;
import org.hswgt.teachingbox.core.rl.feature.FeatureFunction;

/**
 * The QFeatureFunction is a linear function approximation. It uses 1
 * FeatureModifer for all actions. Each action has it's "own" weights.
 */
public class QFeatureFunction extends WeightsVector implements DifferentiableQFunction,
        LinearFunctionApproximator, java.io.Serializable {

    private static final long serialVersionUID = -815698813758067671L;
    protected double valueBeforeWeightsExist = 0;

    public QFeatureFunction(FeatureFunction featureFunction, ActionSet actionSet) {
        super(featureFunction, actionSet);
    }

    public QFeatureFunction(FeatureFunction featureFunction, ActionSet actionSet,
            double valueBeforeWeightsExist) {
        super(featureFunction, actionSet);
        this.setValueBeforeWeightsExist(valueBeforeWeightsExist);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.hswgt.teachingbox.valuefunctions.DifferentiableQFunction#getGradient
     * (org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action)
     */
    public DoubleMatrix1D getGradient(final State state, final Action action) {
        // getFeatureVector
        DoubleMatrix1D feat = featureFunction.getFeatures(state);

        // allocate memory for gradient
        // it is feature vector size times number of actions
        DoubleMatrix1D grad = feat.like(actionSet.size() * feat.size());

        // get the index of the specific action
        int actionIndex = actionSet.getActionIndex(action);

        // copy the feature vector to the prev. allocated memory
        grad.viewPart(actionIndex * feat.size(), feat.size()).assign(feat);

        return grad;
    }

    /**
     * Get the maximal Q-Value in State s
     * 
     * @param state The State to evaluate
     * @return the maximal Q-Value
     */
    public double getMaxValue(final State state) {
        double max = Double.NEGATIVE_INFINITY;
        DoubleMatrix1D feat = featureFunction.getFeatures(state);
        int featureSize = feat.size();
        
        if (featureSize != 0) {
            // iterate only through weights whose correponding action is
            // permitted in this state!
            ActionSet validActions = actionSet.getValidActions(state);
            for (Action action : validActions) {
                max = Math.max(max, feat.zDotProduct(this.weights.viewPart(
                        actionSet.getActionIndex(action)*featureSize,
                        featureSize)));
            }
        }
        else {
            // if we don't have any features yet return no qValue at all that is ZERO
            // otherwise we can get into problems (when plotting the QFunction for
            // example, or when doing some calculations with the value returned)
            // because we have to deal with NEGATIVE_INFINITY then
            return this.valueBeforeWeightsExist;
        }
        
        return max;
    }

    /**
     * Return the value of a given action a in state s
     * 
     * @param state The state
     * @param action The action
     * @return The value of "action" in "state"
     */
    public double getValue(final State state, final Action action) {
        ActionFilter filter = this.actionSet.getFilter();
        if (filter != null && filter.isPermitted(state, action) == false) {
            return Double.NEGATIVE_INFINITY; 
        }

        int actionIndex = actionSet.getActionIndex(action);
        int featSize = featureFunction.getFeatureVectorSize();
        
        return featureFunction.getFeatures(state).zDotProduct(
                weights.viewPart(actionIndex * featSize, featSize));
    }

    /*
     * (non-Javadoc)
     * @see
     * org.hswgt.teachingbox.valuefunctions.DifferentiableQFunction#getGradientSize
     * ()
     */
    public int getGradientSize() {
        return actionSet.size() * featureFunction.getFeatureVectorSize();
    }

    /*
     * (non-Javadoc)
     * @seeorg.hswgt.teachingbox.valuefunctions.ParameterizedFunction#
     * getWeightVectorSize()
     */
    public int getWeightVectorSize() {
        return getGradientSize();
    }

    public void updateWeights(DoubleMatrix1D delta) {
        SeqBlas.seqBlas.daxpy(1, delta, this.weights);
    }

    public void updateWeightsScaled(double scalefactor, DoubleMatrix1D delta) {
        SeqBlas.seqBlas.daxpy(scalefactor, delta, this.weights);
    }

    public double getValueBeforeWeightsExist() {
        return valueBeforeWeightsExist;
    }

    public void setValueBeforeWeightsExist(double valueBeforeWeightsExist) {
        this.valueBeforeWeightsExist = valueBeforeWeightsExist;
    }
}