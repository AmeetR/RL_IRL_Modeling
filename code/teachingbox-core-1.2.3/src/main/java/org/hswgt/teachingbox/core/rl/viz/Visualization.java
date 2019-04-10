/**
 *
 * $Id: Visualization.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.viz;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;

/**
 * Abstract class that helps to implement a  visualization of an environment
 * The derived classes must only implement the visualize() method.
 * See MountainCarVisualization for an example.
 */
public abstract class Visualization implements ExperimentObserver, java.io.Serializable {
    private static final long serialVersionUID = 5679241257394979622L;
    
    // Logging
    protected static Logger log4j = Logger.getLogger(Visualization.class.getSimpleName());
    
    // the simulation mode
    protected Mode mode;
    
    // internal counter for episodes
    protected int episode = 0;
    
    // internal counter for steps
    protected int step = 0;
    
    // plot every n steps
    protected int plot_interval;
    
    // delay time after performing an action in [ms]
    protected int delayTime;
    
    
    /**
     * Constructor
     * @param mode The simulation mode (e.g. every $interval steps, every $interval episodes ... )
     * @param interval The plotting interval. (See mode)
     */
    public Visualization(Mode mode,  int interval) {
        this.mode = mode;
        this.plot_interval = interval;
    }
    
    /**
     * Will be called every $interval steps or every $interval episodes
     * depending on the configuration given by the constructor
     */
    public abstract void visualize();

    
    
    /**
     * Sets the delay time in [ms]. After performing an action in the environment, 
     * the window is paused for delayTime milliseconds.
     * @param delayTime The delay in milliseconds
     */
    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }
    
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#update(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public void update(State state, Action action, State nextState, Action nextAction,
            double reward, boolean terminalState) {
        step = step + 1;
        if( mode == Mode.STEP && (step % plot_interval) == 0 )
            visualize();
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateExperimentStart()
     */
    public void updateExperimentStart() {
        // nothing to do at the beginning
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateExperimentStop()
     */
    public void updateExperimentStop() {
        visualize();
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateNewEpisode(org.hswgt.teachingbox.env.State)
     */
    public void updateNewEpisode(State initialState) {
        episode = episode + 1;
       
        if( mode == Mode.EPISODE&& (episode % plot_interval ) == 0 )
            visualize();
    }
}
