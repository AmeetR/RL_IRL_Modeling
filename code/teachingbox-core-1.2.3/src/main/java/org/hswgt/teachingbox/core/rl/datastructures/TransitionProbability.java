/**
 *
 * $Id: TransitionProbability.java 475 2009-12-15 09:10:57Z Markus Schneider $
 *
 * @version   $Rev: 475 $
 * @author    $Author: Markus Schneider $
 * @date      $Date: 2009-12-15 10:10:57 +0100 (Tue, 15 Dec 2009) $
 *
 */

package org.hswgt.teachingbox.core.rl.datastructures;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * The probability of being in state sn after action s
 */
public class TransitionProbability implements java.io.Serializable
{
    private static final long serialVersionUID = -7307262573593590988L;
    
    State state;
    Action action;
    State nextState;
    double probability;
    
    /**
     * Constructor
     * @param state The state
     * @param action The action 
     * @param nextState The successor state
     * @param probability p(s'|s,a)
     */
    public TransitionProbability(State state, Action action, State nextState, double probability)
    {
        this.state = state;
        this.action = action;
        this.nextState = nextState;
        this.probability = probability;
    }
    
    /**
     * @return the state
     */
    public State getState()
    {
        return state;
    }
    /**
     * @param state the state to set
     */
    public void setState(State state)
    {
        this.state = state;
    }
    /**
     * @return the action
     */
    public Action getAction()
    {
        return action;
    }
    /**
     * @param action the action to set
     */
    public void setAction(Action action)
    {
        this.action = action;
    }
    /**
     * @return the nextState
     */
    public State getNextState()
    {
        return nextState;
    }
    /**
     * @param nextState the nextState to set
     */
    public void setNextState(State nextState)
    {
        this.nextState = nextState;
    }
    /**
     * @return the probability
     */
    public double getProbability()
    {
        return probability;
    }
    /**
     * @param probability the probability to set
     */
    public void setProbability(double probability)
    {
        this.probability = probability;
    }
}
