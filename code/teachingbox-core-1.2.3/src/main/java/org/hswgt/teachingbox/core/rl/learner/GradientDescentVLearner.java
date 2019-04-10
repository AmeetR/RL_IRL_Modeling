package org.hswgt.teachingbox.core.rl.learner;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.DifferentiableVFunction;

import cern.colt.matrix.DoubleMatrix1D;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.feature.FeatureFunction;

/**
 * Gradient Descent Temporal Difference Learning
 */
public class GradientDescentVLearner extends GradientDescentTdLearner
{
    private static final long serialVersionUID = 7467532262637317367L;
    protected DifferentiableVFunction V;
    
    /**
     * Constructor
     * @param valueFunction The value function
     * @param featureFunction The feature function
     * @param actionSet The action set 
     */
    public GradientDescentVLearner(DifferentiableVFunction valueFunction,
            FeatureFunction featureFunction, ActionSet actionSet)
    {
        super(valueFunction, featureFunction, actionSet);
        this.V = valueFunction;
    }

    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.GradientDescentTdLearner#getGradient(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public DoubleMatrix1D getGradient(State s, Action a, State sn, Action an,
            double r, boolean isTerminalState)
    {
        return V.getGradient(s);
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.GradientDescentTdLearner#getTdError(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public DoubleMatrix1D getTdErrors(State s, Action a, State sn, Action an, double r,
            boolean isTerminalState)
    {
        // get value for actual state and next state
        double v = V.getValue(s);
        double vn = 0;
        
        // the value of a terminal state is 0
        if ( !isTerminalState ) 
        {
            vn = V.getValue(sn);
        }
        
        // calcuate td-error
        return V.getWeights().like().assign(r + gamma*vn - v);
    }

}
