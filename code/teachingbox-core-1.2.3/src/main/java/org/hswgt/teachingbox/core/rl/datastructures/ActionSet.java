
package org.hswgt.teachingbox.core.rl.datastructures;

import java.util.Collection;
import java.util.Vector;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;


/**
 * An ActionSet holds all possible actions in an environment
 */
public class ActionSet extends Vector<Action> implements java.io.Serializable {
    private static final long serialVersionUID = -6517169232560215258L;

    /**
     * Per default all actions are always allowed
     */
    protected ActionFilterVector filter = null;

    /**
     * Default Constructor
     */
    public ActionSet() {
        // nothing to do here
    }

    /**
     * Constructor for a given list of actions
     * @param otherActions An array of Action
     */
    public ActionSet(Action[] otherActions) {
        for(int i=0; i<otherActions.length; i++){
                add(otherActions[i]);
        }
    }

    /**
     * Constructor for a collection of actions
     * @param otherActionSet An array of Action
     */
    public ActionSet(Collection<? extends Action> otherActionSet) {
        super(otherActionSet);
    }

    /**
     * Constructor with initial capacity and capacity increment
     * @param initialCapacity The initial capacity
     * @param capacityIncrement The capacity increment
     */
    public ActionSet(int initialCapacity, int capacityIncrement) {
        super(initialCapacity, capacityIncrement);
    }

    /**
     * Constructor with initial capacity
     * @param initialCapacity The initial capacity
     */
    public ActionSet(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Returns a new ActionSet that holds only actions valid in the
     * given State
     * @param state The current State
     * @return All actions valid in State s
     */
    public ActionSet getValidActions(final State state) {
        if( filter == null )
            return this;

        ActionSet valid = new ActionSet();
        for(int i=0; i<this.size(); i++){
            final Action action = this.get(i);
            if( filter.isPermitted(state, action) ){
                    valid.add(action);
            }
        }
        return valid;
    }

    /**
     * @return The actual ActionFilter
     */
    public ActionFilter getFilter() {
        return filter;
    }

    /**
     * @param filter The ActionFilter to use
     */
    public void addFilter(ActionFilter filter) {
        if( this.filter == null )
            this.filter = new ActionFilterVector();

        this.filter.add(filter);
    }

    /**
     * Returns an index to an action
     * @param action The Action
     * @return The index of action a in the set
     */
    public int getActionIndex(Action action) {
        return this.indexOf(action);
    }
}
