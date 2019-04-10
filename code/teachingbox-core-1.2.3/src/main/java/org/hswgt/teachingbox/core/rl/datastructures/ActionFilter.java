
package org.hswgt.teachingbox.core.rl.datastructures;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;



/**
 * An ActionFilter must decide if it is permitted to perform
 * an action in a state s
 * 
 * For example an ActionFilter that allows all actions
 * in every state:
 * 
 * <code>
 * 	public ActionFilter filter = new ActionFilter() {
 *		public boolean isPermitted(final State s, final Action a){
 *			return true;
 *		}
 *	};
 * </code>
 */
public interface ActionFilter extends java.io.Serializable {
    /**
     *
     * @param state the current @State
     * @param action the current @Action
     * @return true if the given action is valid in the state
     */
    public boolean isPermitted(final State state, final Action action);
}
