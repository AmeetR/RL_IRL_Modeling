package org.hswgt.teachingbox.core.rl.experiment;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;


/**
 * This class averages the episodic cumulative reward over multiple experiments. 
 * @author twanschik
 */
public class EpisodicCumulativeRewardAverager extends
	EpisodicCumulativScalarAverager {

    private static final long serialVersionUID = -3710523402820972816L;

    /**
     * The constructor.
     * @param maxEpisodes The maximum number of episodes
     * @param configString The config string used as plot title
     */
    public EpisodicCumulativeRewardAverager(int maxEpisodes,
            String configString) {
        super(maxEpisodes, configString);
    }

    /*
     * (non-Javadoc)
     * @see org.hswgt.teachingbox.core.rl.experiment.DataAverager#update(org.hswgt.teachingbox.core.rl.env.State, org.hswgt.teachingbox.core.rl.env.Action, org.hswgt.teachingbox.core.rl.env.State, org.hswgt.teachingbox.core.rl.env.Action, double, boolean)
     */
    public void update(State s, Action a, State sn, Action an, double r,
            boolean terminalState) {
        // average the reward
        this.episodicData += r;
//        System.out.println ("cumulating reward " + (this.episodicData-r) + " + " + r + " = " + this.episodicData);
    }
}
