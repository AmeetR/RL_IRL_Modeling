package org.hswgt.teachingbox.core.rl.experiment;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * This class averages cumulative double data over episodes. 
 * @author tokicm
 *
 */
public class EpisodicTerminalStateDataAverager extends
	EpisodicCumulativScalarAverager {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1354818908586144469L;
	
	private ParameterObserver parameterObserver;
	private String parameter;

	/**
	 * the constructor with optional setting of parameterObserver and parameter
	 * @param maxEpisodes The maximum episodes to average
	 * @param configString The config string used for the plot title
	 * @param po The ParameterObserver object
	 * @param parameter The parameter name
	 */
	public EpisodicTerminalStateDataAverager(int maxEpisodes,
			String configString, ParameterObserver po, String parameter) {
		
		super(maxEpisodes, configString);
		this.parameterObserver = po;
		this.parameter = parameter;		
	}
		
    /*
     * (non-Javadoc)
     * @see org.hswgt.teachingbox.core.rl.experiment.DataAverager#update(org.hswgt.teachingbox.core.rl.env.State, org.hswgt.teachingbox.core.rl.env.Action, org.hswgt.teachingbox.core.rl.env.State, org.hswgt.teachingbox.core.rl.env.Action, double, boolean)
     */
    public void update(State s, Action a, State sn, Action an, double r,
            boolean terminalState) {
    	
        //if (terminalState) {
     	this.episodicData = parameterObserver.getParameter(s, a, parameter);
     	//System.out.println ("size=" + episodicData);
     	//}
        
        //System.out.println ("Data: =" + this.dataAccumulator);
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
