/**
 *
 * $Id: TabularValueFunction.java 475 2009-12-15 09:10:57Z Markus Schneider $
 *
 * @version   $Rev: 475 $
 * @author    $Author: Markus Schneider $
 * @date      $Date: 2009-12-15 10:10:57 +0100 (Tue, 15 Dec 2009) $
 *
 */

package org.hswgt.teachingbox.core.rl.tabular;

import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.ValueFunction;

/**
 * Interface for tabular representation of value functions.
 * With no function approximation we can directly set the
 * values without the need of gradient decent methods o.s.
 */
public interface TabularValueFunction extends ValueFunction
{
    /**
     * Sets a value for a certain State
     * @param s The state
     * @param value The value to set for the state
     */
    public void setValue(State s, double value);
}
