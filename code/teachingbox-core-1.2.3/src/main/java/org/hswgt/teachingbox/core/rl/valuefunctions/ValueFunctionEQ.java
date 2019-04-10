/**
 *
 * $Id: ValueFunctionEQ.java 772 2010-11-07 22:06:37Z twanschik $
 *
 * @version   $Rev: 772 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-11-07 23:06:37 +0100 (Sun, 07 Nov 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.valuefunctions;

import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.policy.Policy;

/**
 * A ValueFunction that encapsulates QFunction.
 * If action is specified only the values for this action will be "visible".
 * If policy and actionSet are specified the corresponding VFunction calculated
 * from the QFunction will be "visible".
 * If only the QFunction is specified the maximum value for this QFunction will
 * be "visible".
 */
public class ValueFunctionEQ implements ValueFunction {
    private static final long serialVersionUID = -9153794733622866029L;
    protected QFunction Q;
    protected Action action;
    protected Policy policy;
    protected ActionSet actionSet;
    
    /**
     * If costfunction == true, the getValue(State s) will be
     * multiplied with -1;
     */
    public boolean costfunction = false;
    
    /**
     * Creates action ValueFunction that will return max_a Q(s, action)
     * @param Q The QFunction to use
     */
    public ValueFunctionEQ(QFunction Q) {
        this(Q, null, null, null);
    }

    /**
     * Creates action ValueFunction that will return the corresponding V function
     * from the given Q function
     * @param Q The QFunction to use
     * @param policy The policy used by the agent
     * @param actionSet The actionSet of the used environment
     */
    public ValueFunctionEQ(QFunction Q, Policy policy, ActionSet actionSet) {
        this(Q, null, policy, actionSet);
    }

    /**
     * Creates action ValueFunction that will return Q(s, action)
     * @param Q The QFunction to use
     * @param action The action to use for value calculation
     */
    public ValueFunctionEQ(QFunction Q, Action action) {
        this(Q, action, null, null);
    }
    
    /**
     * Creates action ValueFunction that will return Q(s, action)
     * @param Q The QFunction to use
     * @param action The action to use for value calculation
     * @param policy The policy used by the agent
     * @param actionSet The actionSet of the used environment
     */
    protected ValueFunctionEQ(QFunction Q, Action action, Policy policy, ActionSet actionSet) {
        this.Q = Q;
        this.action = action;
        this.policy = policy;
        this.actionSet = actionSet;
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.valuefunctions.ValueFunction#getValue(org.hswgt.teachingbox.env.State)
     */
    public double getValue(State state) {
        double v = 0.0;
        
        // if action specified use corresponding value
        if(action != null)
            v = Q.getValue(state, action);
        // if policy and actionSet provided, calculate corresponding V function
        else if (this.policy != null && this.actionSet != null)
            // calculate the V function from the Q function (see "A Theoretical and
            // Empirical Analysis of Expected Sarsa", Harm van Seijen, Hado van Hasselt,
            // Shimon Whiteson and Marco Wiering
            for (Action tmpAction : this.actionSet.getValidActions(state))
                v += this.policy.getProbability(state, tmpAction)*Q.getValue(
                        state, tmpAction);
        else
            v = Q.getMaxValue(state);
        
        if( costfunction )
            return -v;
        else
            return v;
    }

    // getter and setter

    public Action getAction() {
        return action.copy();
    }

    public void setAction(Action action) {
        this.action = action.copy();
    }

    public ActionSet getActionSet() {
        return actionSet;
    }

    public void setActionSet(ActionSet actionSet) {
        this.actionSet = actionSet;
    }

    public boolean isCostfunction() {
        return costfunction;
    }

    public void setCostfunction(boolean costfunction) {
        this.costfunction = costfunction;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }
}
