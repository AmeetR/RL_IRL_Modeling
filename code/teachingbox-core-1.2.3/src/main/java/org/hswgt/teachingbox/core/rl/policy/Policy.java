/**
 *
 * $Id: Policy.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.policy;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;


public interface Policy extends java.io.Serializable {
    /**
     * Returns an action for every given state
     * @param state The state in which an action should be chosen
     * @return The action to take in state s
     */
    public Action getAction(final State state);
	
	/**
     * Returns the best action for every given state
     * @param state The state in which an action should be chosen
     * @return The action to take in state s
     */
    public Action getBestAction(State state);
    
    /**
     * Returns the probability of selection action a in state s
     * @param state The state
     * @param action The action 
     * @return The probability of selection action a in state s
     */
    public double getProbability(State state, Action action);
}
