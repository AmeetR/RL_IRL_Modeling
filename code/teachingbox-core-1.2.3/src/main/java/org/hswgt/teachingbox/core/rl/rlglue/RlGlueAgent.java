/**
 *
 * $Id: RlGlueAgent.java 1052 2015-10-15 15:10:41Z micheltokic $
 *
 * @version   $Rev: 1052 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-10-15 17:10:41 +0200 (Thu, 15 Oct 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.rlglue;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.State;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * An Adapter to make a Teachingbox-Agent to be used as RL-Glue agent.
 * @author Richard Cubek, Michel Tokic
 *
 */
public class RlGlueAgent implements AgentInterface
{
	
	
    /** Logger */
    private final static Logger log4j = Logger.getLogger("RlGlueAgent");	
	/** The Teachingbox agent to be adapted */
	private Agent tbAgent;
	/** The Teachingbox ActionSet to be used */
	private ActionSet tbActionSet = new ActionSet();
	/** The actual Teachingbox state (to avoid creating the variable each step) */
	private State tbState;
	/** The actual RL-Glue Action (to avoid creating the variable each step) */
	private Action rlGlueAction;
	/** The actual Teachingbox Action (to avoid creating the variable each step) */
	private org.hswgt.teachingbox.core.rl.env.Action tbAction;
	
	/** TODO: number of discrete action dimensions */
	private int numDiscreteActionDims= 0;
	/** TODO: number of continuous action dimensions */
	private int numContinuousActionDims = 0;
	
	/** number of discrete state dimensions */
	private int numDiscreteStateDims = 0;
	/** number of continuous state dimensions */
	private int numContinuousStateDims = 0;
		
	/** flag for avoiding to reinit twice */
	private boolean isInited = false;

	
	/**
	 * Constructor.
	 */
	public RlGlueAgent()	
	{
		System.out.println ("RLGlueAgent()");
	}

	/**
	 * Forced by RL-Glue (AgentInterface).
	 * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_init(java.lang.String)
	 * @param taskSpecification The task specification string (coming from the environment).
	 */
	public void agent_init(String taskSpecification) 
	{
		log4j.info ("agent_init(): taskSpec='" + taskSpecification + "' isInited=" + this.isInited);
	
		if (!this.isInited) {
			
			// create a taskSpec object from the specification string
			TaskSpecVRLGLUE3 taskSpec = new TaskSpecVRLGLUE3(taskSpecification);
			
			log4j.debug("#discreteActions=" + taskSpec.getNumDiscreteActionDims());
			log4j.debug("#continuousActions=" + taskSpec.getNumContinuousActionDims());
			log4j.debug("#discreteObsDims=" + taskSpec.getNumDiscreteObsDims());
			log4j.debug("#continuousObsDims=" + taskSpec.getNumContinuousObsDims());
			
			// extract action dimensions
			this.numDiscreteActionDims = taskSpec.getNumDiscreteActionDims();
			this.numContinuousActionDims = taskSpec.getNumContinuousActionDims();
			this.rlGlueAction = new Action(this.numDiscreteActionDims,0);
	
			
			// extract state dimensions 
			this.numDiscreteStateDims = taskSpec.getNumDiscreteObsDims();
			this.numContinuousStateDims = taskSpec.getNumContinuousObsDims();
			
			// read out the continous observation dimensions and create a corresponding Teachingbox state
			this.tbState = new State(numDiscreteStateDims + numContinuousStateDims);
			// TODO: create the rlGlueAction (discrete action range -> only 1 dimension for ints, 0 for doubles)
			//this.rlGlueAction = new Action(numDiscreteActions, numContinuousActions);
	
	
			// TODO: 1) at present only one discrete action dimension is supported. 2) ADD SUPPORT FOR CONTINUOUS ACTIONS
			/** add one discrete action of present */
			if (this.numDiscreteActionDims > 0) {
				for (int i = taskSpec.getDiscreteActionRange(0).getMin(); i <= taskSpec.getDiscreteActionRange(0).getMax(); i++) {				
					org.hswgt.teachingbox.core.rl.env.Action newAction = new org.hswgt.teachingbox.core.rl.env.Action(new double[]{i});
					this.tbActionSet.add(newAction);
					log4j.info("adding discrete action " + newAction);			
				}
			}
			
			/**
			 * add continuous actions 
			 */
			if (this.numContinuousActionDims > 0) {
				for (int i=0; i<this.numContinuousActionDims; i++) {				
					org.hswgt.teachingbox.core.rl.env.Action newAction = new org.hswgt.teachingbox.core.rl.env.Action(new double[]{-i-1});
					this.tbActionSet.add(newAction);
					log4j.info("adding continuous action " + newAction);			
				}
			}
			
			this.isInited = true;
		} else {
			log4j.info("skipping reinitialization");
		}
	}
	
	/**
	 * Forced by RL-Glue (AgentInterface).
	 * @param observation The environments state.
	 * @return The next Action to take.
	 */
	public Action agent_start(Observation observation)
	{
		//log4j.debug ("agent_start()");
		
		// map from RL-Glue observation to Teachingbox state
		this.tbState = ObservationHandler.getTbState(observation);
		//log4j.debug ("Initial State: " + this.tbState);
		
		// get the first TB Action from the TB Agent
		//log4j.debug("state=" + tbState);
		this.tbAction = this.tbAgent.start(this.tbState);
		//log4j.debug("first TB action: " + tbAction);
		
		// map the TB action to an RL-Glue action (TODO: add support for multiple action dimensions)
		//this.rlGlueAction.setInt(0, this.tbActionSet.getActionIndex(this.tbAction));
		this.rlGlueAction.setInt(0, (int)tbAction.get(0));
		//log4j.debug ("RlGlue-Action: " + this.rlGlueAction);
				
		return this.rlGlueAction;
	}

	/**
	 * Forced by RL-Glue (AgentInterface).
	 * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_step(double, org.rlcommunity.rlglue.codec.types.Observation)
	 * @param reward The reward from the environment.
	 * @param observation The environments state.
	 * @return The next Action to take.
	 */
	public Action agent_step(double reward, Observation observation) 
	{
		
		//log4j.debug  ("agent_step():");

		// map from RL-Glue observation to Teachingbox state
		this.tbState = ObservationHandler.getTbState(observation);

		// perform TB step and read out the TB Action
		this.tbAction = this.tbAgent.nextStep(this.tbState, reward, false);
		
		//log4j.debug ("nextStep() finished");
		// map the TB action to an RL-Glue action (TODO: add support for multiple action dimensions)
		this.rlGlueAction.setInt(0, this.tbActionSet.getActionIndex(this.tbAction));
		
		//log4j.debug ("sending action=" + this.rlGlueAction.getInt(0));
		
		//log4j.debug ("... finish");
		return this.rlGlueAction;
	}

	/**
	 * Forced by RL-Glue (AgentInterface).
	 * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_end(double)
	 * @param reward The reward from the environment.
	 */
	public void agent_end(double reward) 
	{
		/* if an RL-Glue environment reaches a terminal state, RL-Glue calls only agent_end 
		 * in the agent, not agent_step, so we have to call TB agents nextStep with the old state */
		this.tbAction = this.tbAgent.nextStep(this.tbState, reward, true);
	}

	/**
	 * Forced by RL-Glue (AgentInterface).
	 * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_message(java.lang.String)
	 * @param message The RL-Glue message String.
	 * @return The answer String.
	 */
	public String agent_message(String message) {
		log4j.info("agent_message: " + message);
		return message;
	}

	/**
	 * Forced by RL-Glue (AgentInterface).
	 * @see org.rlcommunity.rlglue.codec.AgentInterface#agent_cleanup()
	 */
	public void agent_cleanup() {
		
	}

	/**
	 * In Teachingbox code, one need a reference to the environments ActionSet, since we are connected
	 * only over RL-Glue (in case of binding the components with RL-Glue), the RlGlueAgent adapter is
	 * providing the ActionSet. It can't be built before calling agent_init, so far it is only null! 
	 * @return Reference to the Teachingbox environment ActionSet or null, if agent_init was not called.
	 */
	public ActionSet getTeachingboxActionSet()
	{
		return this.tbActionSet;
	}
	
	/**
	 * Set the Teachingbox agent. This can't be done before, simply in the Constructor,
	 * because the Agent is created when the environments ActionSet is known, and via RL-Glue,
	 * it's not known before agent_init was not called.
	 * @param tbAgent The Teachingbox agent to be adapted.
	 */
	public void setTeachingBoxAgent(Agent tbAgent)
	{
		log4j.debug("adding tbAgent to RlGlueAgent");
		this.tbAgent = tbAgent;
	}	
	
	/**
	 * Returns the current internal Teaching-Box state
	 * @return the current internal Teaching-Box state
	 */
	public State getTbState() {
		return this.tbState;
	}
	
	/**
	 * Returns the current internal teachingbox action
	 * @return the current internal teachingbox action
	 */
	public org.hswgt.teachingbox.core.rl.env.Action getTbAction() {
		return this.tbAction;
	}
}