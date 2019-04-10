package org.hswgt.teachingbox.core.rl.experiment;

import java.io.Serializable;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * This interface defines a function for observing dynamic parameters 
 * from policies, learners etc. Attached to the experiment and in combination 
 * with the ParameterAverager, the ParmameterObserver returns at each time step   
 * the "parameter" which is averaged over episodes.
 */
public interface ParameterObserver extends Serializable
{
	/**
	 * returns the double value of "parameter"
	 * @param s The @State
	 * @param a The @Action
	 * @param parameter The name of the parameter to observe
	 * @return The current value of the parameter to observe
	 */
	public double getParameter (State s, Action a, String parameter);
}