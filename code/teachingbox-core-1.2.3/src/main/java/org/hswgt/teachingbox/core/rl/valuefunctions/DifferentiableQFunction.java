/**
 *
 * $Id: DifferentiableQFunction.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */


package org.hswgt.teachingbox.core.rl.valuefunctions;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

import cern.colt.matrix.DoubleMatrix1D;

/**
 * This Qfunction is differentiable with respect to a given State and Action
 */
public interface DifferentiableQFunction  extends QFunction, DifferentiableFunction {
    /**
     * Return the Gradient of the Function in a given state and action
     * @param state The state
     * @param action The action
     * @return The gradient in "state"
     */
    public DoubleMatrix1D getGradient(final State state, final Action action);

    /**
     * @return Length of gradient
     */
    public int getGradientSize();
	
}
