/**
 *
 * $Id: ErrorObserver.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.learner;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.learner.stepsize.StepSizeCalculator;

/**
 * The ErrorObserver will be notified on significant learning parameters: 
 *   td-error, reward, learning rate (alpha), discounting factor (gamma), terminalState, (state(t), action(t)) and (state(t+1), action(t+1))
 */
public interface ErrorObserver
{

	/**
	 * 
	 * @param tderror The temporal difference error
	 * @param reward The reward
	 * @param alpha The stepsize parameter
	 * @param gamma The discounting rate
	 * @param s The state
	 * @param a The action
	 * @param isTerminalState Is terminal state yes/no
	 */
    public void learnerUpdate(double tderror, double reward, StepSizeCalculator alpha, double gamma, State s, Action a, boolean isTerminalState);
}
