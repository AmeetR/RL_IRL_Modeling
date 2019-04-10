/**
 *
 * $Id: RlGlueEnvironment.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.rlglue;

import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Environment;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

/**
 * An Adapter to make a Teachingbox-Environment to be used as RL-Glue environment.
 * @author Richard Cubek
 *
 */
public class RlGlueEnvironment implements EnvironmentInterface
{
	/** The Teachingbox environment to be adapted */
	private Environment tbEnvironment;
	/** The Teachingbox environment ActionSet */
	private ActionSet tbActionSet;
	/** The actual RL-Glue Reward_observation_terminal object (to avoid creating the variable each step) */
	private Reward_observation_terminal rlGlueRewObsTerm;
	
	/**
	 * Constructor.
	 * @param tbEnvironment The Teachingbox environment to be adapted to RL-Glue.
	 * @param tbActionSet The ActionSet to be used for the Teachingbox environment.
	 */
	public RlGlueEnvironment (Environment tbEnvironment, ActionSet tbActionSet)
	{
		this.tbEnvironment = tbEnvironment;
		this.tbActionSet = tbActionSet;
		this.rlGlueRewObsTerm = new Reward_observation_terminal();
	}

	/**
	 * Forced by RL-Glue (EnvironmentInterface).
	 * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_init()
	 * @return The task specification string.
	 */
	public String env_init()
	{
		// create the task specification string, it is limited to state dimensions and action range for now
		TaskSpecVRLGLUE3 taskSpec = new TaskSpecVRLGLUE3();
		
		// first environment step has to be done here to read out state dimensions
		this.tbEnvironment.initRandom(); // TODO not only random?
		
		// iterate the state dimensions and add them in the task specification
		for (int i = 0; i < this.tbEnvironment.getState().size(); i++)
		{
			taskSpec.addContinuousObservation(new DoubleRange());
		}
		
		// define ActionSet as one discrete action range of 0...n actions (TODO continous actions?)
		taskSpec.addDiscreteAction(new IntRange(0, this.tbActionSet.size() - 1));
		
		// initialize the observation in rlGlueRewObsTerm with 0 int values and n double values
		this.rlGlueRewObsTerm.o = new Observation(0, this.tbEnvironment.getState().size());
		
		return taskSpec.toTaskSpec();
	}

	/**
	 * Forced by RL-Glue (EnvironmentInterface).
	 * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_start()
	 * @return The environments state.
	 */
	public Observation env_start() 
	{
		// map from Teachingbox state to RL-Glue observation		
		for (int i = 0; i < this.tbEnvironment.getState().size(); i++)
		{
			this.rlGlueRewObsTerm.o.setDouble(i, this.tbEnvironment.getState().get(i));
		}
		
		return this.rlGlueRewObsTerm.o;
	}

	/**
	 * Forced by RL-Glue (EnvironmentInterface).
	 * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_step(org.rlcommunity.rlglue.codec.types.Action)
	 * @param arg0 The Action to take on the environment.
	 * @return The Reward_observation_terminal object.
	 */
	public Reward_observation_terminal env_step(Action arg0) 
	{
		/* perform a step in the Teachingbox environment (take action, observe reward r and next state)
		 * and map the variables (next state, reward, terminal) to RL-Glue */
		
		this.rlGlueRewObsTerm.r = this.tbEnvironment.doAction(this.tbActionSet.get(arg0.getInt(0)));		

		// map from Teachingbox environment next state to RL-Glue observation		
		for (int i = 0; i < this.tbEnvironment.getState().size(); i++)
		{
			this.rlGlueRewObsTerm.o.setDouble(i, this.tbEnvironment.getState().get(i));
		}
		
		// set the terminal variable (from Teachingbox to RL-Glue again)
		this.rlGlueRewObsTerm.setTerminal(this.tbEnvironment.isTerminalState());
		
		return this.rlGlueRewObsTerm;
	}

	/**
	 * Forced by RL-Glue (EnvironmentInterface).
	 * @see org.rlcommunity.rlglue.codec.EnvironmentInterface#env_message(java.lang.String)
	 * @param message The RL-Glue message String.
	 * @return The answer String.
	 */
	public String env_message(String message) 
	{
		return null;
	}

	/**
	 * Forced by RL-Glue (EnvironmentInterface).
	 */
	public void env_cleanup() 
	{
	}	
}
