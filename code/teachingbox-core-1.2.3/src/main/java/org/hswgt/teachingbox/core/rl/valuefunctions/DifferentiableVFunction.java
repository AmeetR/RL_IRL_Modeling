/**
 *
 * $Id: DifferentiableVFunction.java 475 2009-12-15 09:10:57Z Markus Schneider $
 *
 * @version   $Rev: 475 $
 * @author    $Author: Markus Schneider $
 * @date      $Date: 2009-12-15 10:10:57 +0100 (Tue, 15 Dec 2009) $
 *
 */


package org.hswgt.teachingbox.core.rl.valuefunctions;

import java.io.Serializable;

import org.hswgt.teachingbox.core.rl.env.State;

import cern.colt.matrix.DoubleMatrix1D;

/**
 * This Valuefunction is differentiable with respect to a given State
 */
public interface DifferentiableVFunction extends ValueFunction, DifferentiableFunction, Serializable
{
	/**
	 * Return the Gradient of the Function in a given state
	 * 
	 * @param s
	 *            The state
	 * @return The gradient in s
	 */
	public DoubleMatrix1D getGradient(final State s);
	
    /**
     * @return Length of gradient
     */
    public int getGradientSize();
}
