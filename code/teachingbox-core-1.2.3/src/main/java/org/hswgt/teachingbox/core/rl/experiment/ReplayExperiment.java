/**
 *
 * $Id: ReplayExperiment.java 731 2010-07-17 13:44:31Z twanschik $
 *
 * @version   $Rev: 731 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-07-17 15:44:31 +0200 (Sa, 17 Jul 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.experiment;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;

/**
 * The ReplayExperiment loads experimentData from an experiment and "re-runs" it.
 * Doing so, we can use already written experiment observer to analyise
 * experimentData of already run experiments i.e. caculate the average reward
 * per step.
 */
public class ReplayExperiment
{
    private final static Logger log4j = Logger.getLogger("ReplayExperiment");
    protected List<ExperimentObserver> observers = new LinkedList<ExperimentObserver>();
    protected ExperimentData experimentData;

    public ReplayExperiment(String filename) {
        this.experimentData = ObjectSerializer.load(filename);
    }

    public void run() {
        log4j.info("Starting new experiment over " + this.getMaxEpisodes() + " episodes");
        this.notifyExperimentStart();

        int episode = -1, step = 0;
        State state, nextState;
        double reward;
        boolean isTerminalState;

        for (ExperimentStepData data : this.experimentData) {
            state = data.getState();
            isTerminalState = data.getIsTerminalState();

            if (episode < data.getEpisode()) {
            	// for each episode
                log4j.info("Starting episode " + data.getEpisode() + " of "
                    + this.experimentData.getMaxEpisodes() + " ...");
                // send a notification to all observer to
                // inform then that a new episode has started
                this.notifyNewEpisode(state);
            }

            episode = data.getEpisode();
            step = data.getStep();
            
            nextState = data.getNextState();
            reward = data.getReward();

            // send a notification to all observers
            this.notify(state, data.getAction(), nextState,
                    data.getNextAction(), reward, isTerminalState);

            // is a terminal state reached?
            if( isTerminalState )
                log4j.debug("Reached terminal state after " + step + " steps");

            if( step >= this.experimentData.getMaxSteps())
                log4j.debug("Maxstep limit of " + step + " exceeded");
        }

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
    public void notify(State s, Action a, State sn, Action an, double r,
            boolean isTerminalState) {
        for(ExperimentObserver observer : observers ) {
            observer.update(s, a, sn, an, r, isTerminalState);
        }
    }

    /**
     * This method will notify all observer, that a new episode has started
     * @param initialState The initial state
     */
    public void notifyNewEpisode(State initialState) {
        for(ExperimentObserver observer : observers ) {
            observer.updateNewEpisode(initialState);
        }
    }

    /**
     * This method will notify all observer, that a experiment has stopped
     */
    public void notifyExperimentStop() {
        for(ExperimentObserver observer : observers ) {
            observer.updateExperimentStop();
        }
    }

    /**
     * This method will notify all observer, that a new experiment has started
     */
    public void notifyExperimentStart() {
        for(ExperimentObserver observer : observers ) {
            observer.updateExperimentStart();
        }
    }

    /**
     * Attaches an observer to this experiment
     * @param obs The observer to attach
     */
    public void addObserver(ExperimentObserver obs) {
        log4j.info("New Observer added: "+obs.getClass());
        this.observers.add(obs);
    }

    public int getMaxEpisodes() {
    	return this.experimentData.getMaxEpisodes();
    }

    public int getMaxSteps() {
    	return this.experimentData.getMaxSteps();
    }

    public ExperimentData getExperimentData() {
        return experimentData;
    }
}
