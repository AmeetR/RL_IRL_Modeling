package org.hswgt.teachingbox.core.rl.experiment;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * This class averages (over experiments) double data at the end of each episode. 
 * @author tokicm
 *
 */
public class EpisodicParameterAverager extends
		ScalarAverager {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1354818908586144469L;
	
	private ParameterObserver parameterObserver;
	private String parameter;

	/**
	 * the constructor with optional setting of parameterObserver and parameter
	 * @param maxEpisodes The maximum number of episodes
	 * @param configString The config string used as plot title
	 * @param po The ParameterObserver object
	 * @param parameter The parameter name to observe
	 */
	public EpisodicParameterAverager(int maxEpisodes,
			String configString, ParameterObserver po, String parameter) {
		
		super(maxEpisodes, configString);
		this.parameterObserver = po;
		this.parameter = parameter;		
	}
	
	/**
	 * the constructor that requires manual setting of parameterObserver and parameter
	 * @param maxEpisodes The maximum number of episodes 
	 * @param configString The config string used as plot title
	 */
	public EpisodicParameterAverager(int maxEpisodes,
			String configString) {
		
		super(maxEpisodes, configString);
		this.parameterObserver = null;
		this.parameter = null;		
	}

	@Override
    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean terminalState) {
        // do nothing at each step
    }
	
    @Override
    public void updateNewEpisode(State initialState) {

    	// Average parameter of last episode
    	this.updateAverage(this.parameterObserver.getParameter(initialState, null, parameter));
    	
        // update episode and t only if last episode
        if (t > 0) {
            this.episode++;
            this.t = 0;
            this.dataAccumulator = 0.0;
            //log4j.debug("  starting new episode (no. " + episode + ")");
        }
    }

	
	/**
	 * @param po the ParameterObserver to set
	 */
	public void setParameterObserver(ParameterObserver po) {
		this.parameterObserver = po;
	}

	/**
	 * @return the parameter
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * @param parameter the parameter to set
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}  
}
