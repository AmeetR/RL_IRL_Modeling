/**
 *
 * $Id: ExperimentObserver.java 475 2009-12-15 09:10:57Z Markus Schneider $
 *
 * @version   $Rev: 475 $
 * @author    $Author: Markus Schneider $
 * @date      $Date: 2009-12-15 10:10:57 +0100 (Tue, 15 Dec 2009) $
 *
 */

package org.hswgt.teachingbox.core.rl.experiment;

import java.io.Serializable;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * The ExperimentObserver will be notified on all important
 * events that occur during an experiment. Also a learner
 * will typically implement this interface. 
 */
public interface ExperimentObserver extends Serializable
{
    /**
     * This method will be called in every step of the experiment
     * @param s State at time t
     * @param a Action at time t
     * @param sn State at time t+1
     * @param an Action at time t+1
     * @param r Reward for doing action a in state s
     * @param terminalState True if sn is a terminal state
     */
    public void update(State s, Action a, State sn, Action an, double r, boolean terminalState);
    
    /**
     * This method will be called at if a new Episode starts
     * @param initialState The initial state
     */
    public void updateNewEpisode(State initialState);

    /**
     * This method will be called if the experiments is stopped
     */
    public void updateExperimentStop();

    /**
     * This method will be called if the experimetns starts 
     */
    public void updateExperimentStart();


    
}
