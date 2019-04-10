/**
 *
 * $Id: ActionFilterVector.java 677 2010-06-11 08:45:17Z twanschik $
 *
 * @version $Rev: 677 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-11 10:45:17 +0200 (Fr, 11 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.datastructures;

import java.util.Vector;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * Extended version of teachingbox's ActionFilter. Applies multiple
 * filters.
 */
public class ActionFilterVector extends Vector<ActionFilter> implements ActionFilter {

    public boolean isPermitted(State state, Action action) {
        for (ActionFilter filter : this) {
            if (filter.isPermitted(state, action) == false)
                return false;
        }
        return true;
    }
}
