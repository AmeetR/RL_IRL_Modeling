/**
 *
 * $Id: TabularQFunction.java 475 2009-12-15 09:10:57Z Markus Schneider $
 *
 * @version   $Rev: 475 $
 * @author    $Author: Markus Schneider $
 * @date      $Date: 2009-12-15 10:10:57 +0100 (Tue, 15 Dec 2009) $
 *
 */

package org.hswgt.teachingbox.core.rl.tabular;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;

/**
 * Interface for tabular representation of Q-functions.
 * With no function approximation we can directly set the
 * values without the need of gradient decent methods o.s.
 */
public interface TabularQFunction extends QFunction
{
    /**
     * Set the value for a State/Action
     * @param s The state
     * @param a The Action
     * @param value The value to set for the state/action pair
     */
    public void setValue(State s, Action a, double value);
}
