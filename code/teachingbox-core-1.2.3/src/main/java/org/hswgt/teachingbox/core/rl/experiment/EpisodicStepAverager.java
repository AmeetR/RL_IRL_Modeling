package org.hswgt.teachingbox.core.rl.experiment;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.EpisodicCumulativeRewardAverager;


/**
 * This class counts the amount of taken actions per episode.
 * @author Michel Tokic
 *
 */
public class EpisodicStepAverager extends EpisodicCumulativeRewardAverager {
	private static final long serialVersionUID = 111831282388831823L;
	
	public EpisodicStepAverager(int maxEpisodes, String configString) {
		super(maxEpisodes, configString);
	}
	
	public void update(State s, Action a, State sn, Action an, double r, boolean terminalState)
	{
		this.episodicData += 1;
	}
}