/**
 *
 * $Id: $
 *
 * @version   $Rev: $
 * @author    $Author: $
 * @date      $Date: $
 *
 */
package org.hswgt.teachingbox.core.rl.learner.stepsize;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.learner.Learner;


/**
 * @author Richard Cubek, Michel Tokic
 *
 */
public interface StepSizeCalculator extends Learner
{
	/**
	 * Get the step size parameter alpha.
	 * @param state The actual state (for the case, alpha depends on it). 
	 * @param action The actual action (for the case, alpha depends on it). 
	 * @return Value of alpha.
	 */
	public double getAlpha(State state, Action action);
}
