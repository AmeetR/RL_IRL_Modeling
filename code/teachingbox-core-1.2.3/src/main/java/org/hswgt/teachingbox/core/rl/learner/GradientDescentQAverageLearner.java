package org.hswgt.teachingbox.core.rl.learner;

import cern.colt.function.DoubleFunction;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.DifferentiableQFunction;

import cern.colt.matrix.DoubleMatrix1D;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.feature.FeatureFunction;


/**
 * Gradient descent QAverageLearning. See "Convergence and Divergence in
 * Standard and Averaging Reinforcement Learning" from Marco A. Wiering
 */
public class GradientDescentQAverageLearner extends GradientDescentTdLearner
{
    private static final long serialVersionUID = 1316859261056571809L;

    // The QFunction that will be estimated
    protected DifferentiableQFunction Q;


    /**
     * Constructs a new QLearner that uses gradient descent
     * to learn a Q-Function
     * @param Q The Q-Function to learn
     * @param featureFunction The feature function
     * @param actionSet The action set
     */
    public GradientDescentQAverageLearner(DifferentiableQFunction Q,
            FeatureFunction featureFunction, ActionSet actionSet)
    {
        super(Q, featureFunction, actionSet);
        this.Q = Q;
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.GradientDescentControlLearner#getTdError(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public DoubleMatrix1D getTdErrors(State s, Action a, State sn, Action an, double r, boolean isTerminalState)
    {
        // get Q-Value for actual state and next state
        double qn = 0;

        // the Q-Value of a terminal state is 0
        if ( !isTerminalState )
        {
            // Q-Learning uses the best action to perform an update
            // and the best action is the one with best q-value
            qn = Q.getMaxValue(sn);
        }

        // get weights after Q.getMaxValue(sn) was calculated cause Q.getMaxValue(sn)
        // can result in adding a RBFs
        // calculates the tdError for each weight, can be more stable, see
        // "Convergence and Divergence in
        // Standard and Averaging Reinforcement Learning" from Marco A. Wiering
        DoubleMatrix1D weights = Q.getWeights();
        final double tmp = r + gamma*qn;

        DoubleMatrix1D tdErrors = weights.copy().assign(new DoubleFunction() {
            public double apply(double weight) {
                return tmp - weight;
            }
        });

        return tdErrors;
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.GradientDescentTdLearner#getGradient(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public DoubleMatrix1D getGradient(State s, Action a, State sn, Action an, double r, boolean isTerminalState)
    {
        return Q.getGradient(s, a);
    }
}
