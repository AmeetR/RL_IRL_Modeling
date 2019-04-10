/**
 *
 * $Id: GradientPolicy.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.policy;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.DifferentiableFunction;

import cern.colt.matrix.DoubleMatrix1D;


public interface GradientPolicy extends Policy, DifferentiableFunction {
    /**
     * Return the gradient of the org.hswgt.teachingbox.policy for an Action a in a State s
     * @param state The State
     * @param action The Action
     * @return dpi/dw
     */
    public DoubleMatrix1D getGradient(final State state, final Action action);

    /**
     * Returns grad log pi of the org.hswgt.teachingbox.policy for an Action a in a State s
     * @param state The State
     * @param action The Action
     * @return (grad pi) / (pi)
     */
    public DoubleMatrix1D getLogGradient(final State state, final Action action);
	
}
