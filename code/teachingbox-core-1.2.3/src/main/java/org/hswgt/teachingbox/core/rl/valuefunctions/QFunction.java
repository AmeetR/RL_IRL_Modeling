/**
 * \file QFunction.java
 *
 * $Id: QFunction.java 988 2015-06-17 19:48:01Z micheltokic $
 * $URL: https://svn.code.sf.net/p/teachingbox/code/tags/teachingbox-core-1.2.3/src/main/java/org/hswgt/teachingbox/core/rl/valuefunctions/QFunction.java $
 
 * \version   $Rev: 988 $
 * \author    Markus Schneider
 * \date      Sep 16, 2008
 *
 */

package org.hswgt.teachingbox.core.rl.valuefunctions;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * This QFunction interface
 */
public interface QFunction extends java.io.Serializable {
    /**
     * Return the value of a given action a in state s
     * @param state The state
     * @param action The action
     * @return The value of a in s
     */
    public double getValue(final State state, final Action action);

    /**
     * Get the maximal Q-Value in State s
     * @param state The State to evaluate
     * @return the maximal Q-Value
     */
    public double getMaxValue(final State state);
}
