package org.hswgt.teachingbox.core.rl.rlglue;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver;
import org.rlcommunity.rlglue.codec.RLGlue;
import org.rlcommunity.rlglue.codec.types.Reward_observation_action_terminal;

/**
 * The experiment puts all the parts together. The agent
 * chooses actions and which are then executed in the environment.
 */
public class RlGlueExperiment implements Serializable
{
    private static final long serialVersionUID = 8176399275831332232L;
    
    // Logger
    private final static Logger log4j = Logger.getLogger("RlGlueExperiment");
    
    protected int maxEpisodes;
    protected int maxSteps;
    protected List<ExperimentObserver> observers = new LinkedList<ExperimentObserver>();
    
    public static final int INIT_STATE = 1;
    public static final int INIT_RANDOM = 2;
    
    // default: each episode is initialized with a random state
    protected int initializeEpisodeState = INIT_RANDOM;
    protected State initState;
    
    RlGlueAgent agent; 
    
    /**
     * Experiment Constructor
     * @param agent The agent
     * @param maxEpisodes The maximal number of episodes
     * @param maxSteps The maximal number of states
     */
    public RlGlueExperiment(RlGlueAgent agent, int maxEpisodes, int maxSteps)
    {
        this.maxEpisodes = maxEpisodes;
        this.maxSteps = maxSteps;
        this.agent = agent;
    }

    /**
     * Run experiment with given initialization messages for the environment
     */
    public void run()
    {
        log4j.info("Starting new experiment over "+maxEpisodes+" episodes");
        this.notifyExperimentStart();

        // initialize components if not done yet
        if (!RLGlue.isInited()) {
        	System.out.println ("!!!!!initializing environment!!!!!");
            RLGlue.RL_init();        	
        }

        // for each episode
        for (int episode = 0; episode < maxEpisodes; episode++) {

            RLGlue.RL_start(); // calls environment.rl_start() which initializes the environment
         
        	log4j.debug("Starting episode " + episode + " of "
                    + maxEpisodes + " ...");

            // TODO: implement initial state for RLGlue experiments
            /*
            // initialize the environment with a specific start state 
            if (this.initializeEpisodeState == INIT_STATE) {
            	env.init(initState);
            	
            // initialize the environment with a random start state
            } else {
                env.initRandom();            	
            }
            */
            
            // get initial state
            State s = agent.getTbState();
            Action a = agent.getTbAction();
            
            // send a notification to all observer to 
            // inform then that a new episode has started
            this.notifyNewEpisode(s);
            
            // Repeat(for each step of episode)
            for (int step = 0; ; step++) {
                
            	Reward_observation_action_terminal roa = RLGlue.RL_step();
            	double r = roa.getReward();
                boolean isTerminalState = roa.isTerminal();
                
                //State sn = ObservationHandler.getTbState(roa.getObservation());
                State sn = agent.getTbState(); // faster because object already exists
            	Action an = agent.getTbAction();
            	
            	// send a notification to all observers
                this.notify(s, a, sn, an, r, isTerminalState);
                
                // is a terminal state reached?
                if( isTerminalState ) {
                    log4j.debug("Reached terminal state after " + step + " steps");
                    break;
                }
                
                // max steps reached?
                if( step >= maxSteps ) {
                    log4j.debug("Maxstep limit of " + step + " exceeded");
                    break;
                }
                
                s = sn.copy();
                a = an.copy();
            }
        }

        //RLGlue.RL_cleanup();
        //RLGlue.resetGlueProxy();

        log4j.info("Experiment stopped");
        this.notifyExperimentStop();
    }
    
    /**
     * Sends a notification to all observers
     * @param s State at time t
     * @param a Action at time t
     * @param sn State at time t+1
     * @param an Action at time t+1
     * @param r Reward for doing action a in state s
     * @param isTerminalState True if sn is a terminal state
     */
    public void notify(State s, Action a, State sn, Action an, double r, boolean isTerminalState)
    {
        for(ExperimentObserver observer : observers )
        {
            observer.update(s, a, sn, an, r, isTerminalState);
        }
    }

    /**
     * This method will notify all observer, that a new episode has started
     * @param initialState The initial state
     */
    public void notifyNewEpisode(State initialState)
    {
        for(ExperimentObserver observer : observers )
        {
            observer.updateNewEpisode(initialState);
        }
    }
    
    /**
     * This method will notify all observer, that a experiment has stopped
     */
    public void notifyExperimentStop()
    {
        for(ExperimentObserver observer : observers )
        {
            observer.updateExperimentStop();
        }
    }
    
    /**
     * This method will notify all observer, that a new experiment has started
     */
    public void notifyExperimentStart()
    {
        for(ExperimentObserver observer : observers )
        {
            observer.updateExperimentStart();
        }
    }
    
    /**
     * Attaches an observer to this experiment
     * @param obs The observer to attach
     */
    public void addObserver(ExperimentObserver obs)
    {
        log4j.info("New Observer added: "+obs.getClass());
        this.observers.add(obs);   
    }

    // getter and setter
    /**
     * Sets the init state for each episode
     * @param s The start state for each episode
     */
    public void setInitState (State s) {
    	this.initState = s;
    	this.initializeEpisodeState = INIT_STATE;
    }

    /**
     * Set the maximum number of episodes being iterated.
     * @param maxEpisodes Maximum number of episodes.
     */
    public void setMaxEpisodes(int maxEpisodes)
    {
    	this.maxEpisodes = maxEpisodes;
    }

    /**
     * Set the maximum number of steps being iterated per episode.
     * @param maxSteps Maximum number of steps.
     */
    public void setMaxSteps(int maxSteps)
    {
    	this.maxSteps = maxSteps;
    }

    /**
     * Get the maximum number of episodes being iterated.
     * @return Maximum number of episodes.
     */
    public int getMaxEpisodes()
    {
    	return this.maxEpisodes;
    }

    /**
     * Get the maximum number of steps being iterated per episode.
     * @return Maximum number of steps.
     */
    public int getMaxSteps()
    {
    	return this.maxSteps;
    }

}
