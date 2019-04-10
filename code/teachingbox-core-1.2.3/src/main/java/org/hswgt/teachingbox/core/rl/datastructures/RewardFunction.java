/**
 *
 * $Id: RewardFunction.java 745 2010-10-14 08:51:49Z twanschik $
 *
 * @version   $Rev: 745 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-10-14 10:51:49 +0200 (Thu, 14 Oct 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.datastructures;

import java.io.Serializable;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * The reward function for an environment
 */
public interface RewardFunction extends Serializable
{
    /**
     * Returns the reward for executing action a in state s
     * @param state The State
     * @param action The action
     * @param nextState  The successor state
     * @return The reward for executing action a in state s
     */
    public double getReward(final State state, final Action action,
            final State nextState);
}
