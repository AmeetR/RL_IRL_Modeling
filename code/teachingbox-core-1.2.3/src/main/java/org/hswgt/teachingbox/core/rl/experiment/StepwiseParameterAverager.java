package org.hswgt.teachingbox.core.rl.experiment;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;


/**
 * This class averages arbitrary double data, in a step-wise manner, observed from a ParameterObserver.
 * @author tokicm
 *
 */
public class StepwiseParameterAverager extends ScalarAverager {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7038779208817590631L;
	private ParameterObserver parameterObserver = null;
	private String parameter = null;

	public StepwiseParameterAverager(int maxSteps, String configString) {
		super(maxSteps, configString);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * the constructor with optional setting of parameterObserver and parameter
	 * @param maxSteps The maximum number of steps
	 * @param configString The config string used as plot title
	 * @param po The ParameterObserver object
	 * @param parameter The parameter to observe
	 */
	public StepwiseParameterAverager(int maxSteps,
			String configString, ParameterObserver po, String parameter) {
		
		super(maxSteps, configString);
		this.parameterObserver = po;
		this.parameter = parameter;		
	}
	
	/**
	 * average the double data 
	 */
    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean terminalState) {

    	this.updateAverage(parameterObserver.getParameter(state, action, parameter));
    }
    
	/**
	 * Defines the ParameterObserver for obtaining our data  
	 * @param po the ParameterObserver to set
	 */
	public void setParameterObserver(ParameterObserver po) {
		this.parameterObserver = po;
	}

	/**
	 * Returns the parameter to be observed 
	 * @return the parameter
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * Defines the parameter to be observed
	 * @param parameter the parameter to set
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}  

}
