
package org.hswgt.teachingbox.core.rl.agent;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.policy.Policy;

/**
 * "The reinforcement learning problem is meant to be a straightforward framing of the 
 * problem of learning from interaction to achieve a goal. The learner and decision-maker 
 * is called the agent. The thing it interacts with, comprising everything outside the 
 * agent, is called the environment. These interact continually, the agent selecting 
 * actions and the environment responding to those actions and presenting new 
 * situations to the agent"
 * @see <a href="http://www.cs.ualberta.ca/~sutton/book/ebook/node28.html">http://www.cs.ualberta.ca/~sutton/book/ebook/node28.html</a>
 * 
 */
public class Agent implements Serializable {
    private static final long serialVersionUID = 4710685566559177793L;

    // Logger
    private static final Logger log4j = Logger.getLogger("Agent");
    
	protected Policy policy;
    protected List<AgentObserver> observers = new LinkedList<AgentObserver>();
    protected State lastState;
    protected Action lastAction;
    
    /**
     * Agent constructor.
     * @param policy The @Policy that the agent will follow during the experiment
     */
    public Agent(Policy policy) {
        this.policy = policy;
    }
    
    /**
     * Tells the Agent that a new episode begun and get the 
     * first action to execute
     * @param initialState The initial state
     * @return the @Action to execute
     */
    public Action start(final State initialState) {
    	notifyNewEpisode(initialState);
    	lastState = initialState.copy();
    	lastAction = policy.getAction(initialState);
    	return lastAction.copy();
    }
    
    /**
     * Informs the agent about the next state and reward. The agent will
     * return the next action to execute
     * @param nextState The next state
     * @param reward The reward for the last action executed
     * @param isTerminalState Indicates if the nextState is a terminal state
     * @return The next action to execute
     */
    public Action nextStep(final State nextState, double reward, boolean isTerminalState) {
    	Action nextAction = policy.getAction(nextState);
    	notify(lastState, lastAction, nextState, nextAction, reward, isTerminalState);
    	lastState = nextState.copy();
    
    	lastAction = nextAction;
    	
    	return nextAction.copy();
    }
    
    /**
     * Sends a notification to all observers
     * @param state State at time t
     * @param action Action at time t
     * @param nextState State at time t+1
     * @param nextAction Action at time t+1
     * @param reward Reward for doing action a in state s
     * @param isTerminalState True if sn is a terminal state
     */
    public void notify(State state, Action action, State nextState, Action nextAction,
            double reward, boolean isTerminalState) {
        for(AgentObserver observer : observers ) {
            observer.update(state, action, nextState, nextAction, reward, isTerminalState);
        }
    }

    /**
     * This method will notify all observer, that a new episode has started
     * @param initialState The initial @State
     */
    public void notifyNewEpisode(final State initialState) {
        for(AgentObserver observer : observers ) {
            observer.updateNewEpisode(initialState);
        }
    }
    
    /**
     * Attaches an observer to this experiment
     * @param observer The @AgentObserver to attach
     */
    public void addObserver(final AgentObserver observer) {
        // => VDBE needs to be attached twice (before and after Learner in order to observe the value difference! VDBE code will be checked in soon.) (Michel Tokic, 06.03.2011)
        // don't add an observer twice! (Thomas Wanschik)
    	//if (this.observers.contains(observer))
        //	return;
        
    	log4j.info("New Observer added: "+observer.getClass());
        this.observers.add(observer);
    }

    /**
     * Remove an observer to this experiment
     * @param observer The @AgentObserver to remove
     */
    public void removeObserver(final AgentObserver observer) {
        log4j.info("Removed Observer: "+observer.getClass());
        this.observers.remove(observer);
    }
}
