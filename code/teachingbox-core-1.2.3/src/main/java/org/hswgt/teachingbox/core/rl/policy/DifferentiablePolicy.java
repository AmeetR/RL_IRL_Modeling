package org.hswgt.teachingbox.core.rl.policy;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.DifferentiableFunction;

import cern.colt.matrix.DoubleMatrix1D;

public interface DifferentiablePolicy extends Policy, DifferentiableFunction {
    /**
     * Return the Gradient of the Function in a given state and action
     * @param state The state
     * @param action The action
     * @return The gradient in s
     */
    public DoubleMatrix1D getGradient(final State state, final Action action);

    /**
     * @return Length of gradient
     */
    public int getGradientSize();
}
