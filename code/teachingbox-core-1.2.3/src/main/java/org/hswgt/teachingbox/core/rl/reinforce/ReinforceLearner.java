package org.hswgt.teachingbox.core.rl.reinforce;

/**
 * This interfaces defines functions that need to be implemented when
 * inheriting a class from EpisodicReinforceLearner or LocalReinforceLearner.
 * 
 * @author Michel Tokic
 *
 */
public interface ReinforceLearner {

	/**
	 * sets the control parameter to the ReinforceLearner (e.g. the exploration rate of a policy, the learning rate of a learner, etc...)
	 * @param parameter The value of the control parameter
	 */
	public void setParameter (double parameter);
	
	/**
	 * returns a scaled value (or the identity) of the parameter, such as "mean" or Math.exp("mean") ... 
	 * @param parameter The name of the parameter
	 * @param value The value of the parameter
	 * @return The scaled value
	 */
	public double getScaledMetaParameter (String parameter, double value);
}
