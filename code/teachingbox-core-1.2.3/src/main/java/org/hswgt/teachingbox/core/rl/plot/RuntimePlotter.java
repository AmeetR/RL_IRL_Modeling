/**
 *
 * $Id: RuntimePlotter.java 1058 2016-10-12 21:16:37Z micheltokic $
 *
 * @version   $Rev: 1058 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2016-10-12 23:16:37 +0200 (Wed, 12 Oct 2016) $
 *
 */

package org.hswgt.teachingbox.core.rl.plot;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver;
import org.hswgt.teachingbox.core.rl.network.Network;
import org.hswgt.teachingbox.core.rl.network.adaption.AdaptionRule;
import org.hswgt.teachingbox.core.rl.network.adaption.DoNothing;

/**
 * Helper class to create plots every n episodes/steps
 */
public class RuntimePlotter implements Plotter, ExperimentObserver {
    // if net != null we have to switch the adaption rule off, otherwise
    // plotting migth result in changing (adding, deleting, ... networknodes)
    // the network!
    protected Network net = null;

    /**
     * Plotting Mode
     */
    public static enum Mode {
        /** Creates a new plot after every n steps */
        STEP,       
        /** Creates a new plot after every n episodes */
        EPISODE,
        /** Creates a new plot at the end of the experiment */
        EXPERIMENT
    }
    
    private static final long serialVersionUID = 4694625138761180416L;
    private final static Logger log4j = Logger.getLogger("RuntimePlotter");
    protected int step;
    protected int episode;
    protected int plot_interval;
    protected Mode mode;
    protected Plotter plotterToDelegateTo;
    
    /**
     * Constructor
     * @param plotter The plotter to delegate to
     * @param mode The plotting mode (e.g. every $interval steps, every $interval episodes ... )
     * @param interval The plotting interval. (See mode)
     */
    public RuntimePlotter(Plotter plotter, Mode mode,  int interval)
    {
        this.plotterToDelegateTo = plotter;
        this.mode = mode;
        this.plot_interval = interval;
    }

    public RuntimePlotter(Plotter plotter, Mode mode,  int interval, Network net)
    {
        this(plotter, mode, interval);
        this.net = net;
    }
    
    /**
     * Will be called every n episodes/steps depending on configuration.
     * Turns off the adaption rule of the network when a network is given in
     * order to avoid changes to the network due to plotting.
     */
    public void plot()
    {
        //log4j.debug("Generating plot ...");
        if (this.net != null) {
            // shift of adaption rule and caching
            AdaptionRule adaptionRule = this.net.getAdaptionRule();
            this.net.setAdaptionRule(new DoNothing());
            this.net.setCacheEnabled(false);
            plotterToDelegateTo.plot();
            this.net.setCacheEnabled(true);
            this.net.setAdaptionRule(adaptionRule);
        }
        else {
            plotterToDelegateTo.plot();
        }
        
        //log4j.debug("finished");
    }

    
    /**
     * This method will be called in every step of the experiment
     * @param s State at time t
     * @param a Action at time t
     * @param sn State at time t+1
     * @param an Action at time t+1
     * @param r Reward for doing action a in state s
     * @param terminalState True if sn is a terminal state
     */
    public void update(State s, Action a, State sn, Action an, double r, boolean terminalState)
    {
        step = step + 1;
        if( mode == Mode.STEP && (step % plot_interval) == 0 )
            plot();
    }
    


    /**
     * This method will be called at if a new Episode starts
     * @param initialState The initial state
     */
    public void updateNewEpisode(State initialState)
    {
        episode = episode + 1;
        
        if( mode == Mode.EPISODE&& (episode % plot_interval ) == 0 ){
            plot();
            return;
        }
    }

    /**
     * This method will be called if the experiments is stopped
     */
    public void updateExperimentStop()
    {
        plot();
    }

    /**
     * This method will be called if the experimetns starts 
     */
    public void updateExperimentStart()
    {
        // nothing to plot at the beginning
    }

    // getter and setter
    public Network getNet() {
        return net;
    }

    public void setNet(Network net) {
        this.net = net;
    }
}
