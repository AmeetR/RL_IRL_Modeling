/**
 *
 * $Id: GridWorld2x3Tabular.java 636 2010-02-10 08:54:52Z Tobias Fromm $
 *
 * @version   $Rev: 636 $
 * @author    $Author: Tobias Fromm $
 * @date      $Date: 2010-02-10 09:54:52 +0100 (Wed, 10 Feb 2010) $
 *
 */

package org.hswgt.teachingbox.usecases.rlglue;

import java.text.DecimalFormat;

import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.Grid2x3Environment;
import org.hswgt.teachingbox.core.rl.learner.TabularQLearner;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.rlglue.RlGlueAgent;
import org.hswgt.teachingbox.core.rl.rlglue.RlGlueEnvironment;
import org.hswgt.teachingbox.core.rl.tabular.HashQFunction;
import org.hswgt.teachingbox.core.rl.tabular.TabularQFunction;
import org.rlcommunity.rlglue.codec.RLGlue;
import org.rlcommunity.rlglue.codec.util.AgentLoader;
import org.rlcommunity.rlglue.codec.util.EnvironmentLoader;

/**
 * The 2x3 GridWorld Q-Learning example: the RL-Glue Experiment is represented by this class, the Agent
 * and Environment are both Teachinbox components, but they communicate by RL-Glue with the help of the
 * adapters RlGlueAgent and RlGlueEnvironment.
 * The program order has to be (see the items in the source again):
   <pre>
   1) create and start Teachingbox-Environment and RL-Glue-Environment-Adapter
   2) create and start RlGlue-Agent-Adapter
   3) initialize RL-Glue (to let the RlGlue-Agent-Adapter get the ActionSet)
   4) create Teachingbox-Agent
   5) set the Teachingbox-Agent in the RL-Glue-Agent-Adapter
   6) run RL-Glue episode(s)
   </pre>
 * @author Richard Cubek
 *
 */
public class GridWorld2x3Tabular
{
	public static void main(String[] args)
	{
		/* CREATE AND START TEACHINGBOX-ENVIRONMENT AND RL-GLUE-ENVIRONMENT-ADAPTER*/
		
		// create an TB GridWorld environment and adapt it with RlGlue
		RlGlueEnvironment environment = new RlGlueEnvironment(new Grid2x3Environment(), Grid2x3Environment.ACTION_SET);
		// create an EnvironmentLoader that will start the RL-Glue environment when its run method is called
		EnvironmentLoader environmentLoader = new EnvironmentLoader(environment);
		// create thread so that the agent and environment can run asynchronously 		
		Thread environmentThread = new Thread(environmentLoader);
		// start the thread
		environmentThread.start();

		/* CREATE AND START RL-GLUE-AGENT-ADAPTER */
		
		RlGlueAgent rlGlueAgent = new RlGlueAgent();		
		// create an agentLoader that will start the agent when its run method is called
		AgentLoader agentLoader = new AgentLoader(rlGlueAgent);
		// create thread so that the agent and environment can run asynchronously 		
		Thread agentThread = new Thread(agentLoader);
		// start the thread
		agentThread.start();
		
		/* INIT RL-GLUE */
		
		// call RL_init first (!) to let the RlGlueAgent get the ActionSet (by task specification)
		String taskSpec = RLGlue.RL_init();
		System.out.println("RL_init called, the environment sent task spec: " + taskSpec);

		/* CREATE TEACHINGBOX-AGENT */
		
		TabularQFunction Q = new HashQFunction(0);
		// the ActionSet for the policy is read out from the rlGlueAgent (RL_init must have been called before!)
		EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy(Q, rlGlueAgent.getTeachingboxActionSet(), 0.9);
		TabularQLearner learner = new TabularQLearner(Q);
		learner.setAlpha(1.0);
		learner.setGamma(0.9);
		learner.setLambda(0.9);
		Agent tbAgent = new Agent(pi);		
		tbAgent.addObserver(learner);
		
		/* SET THE TEACHINGBOX-AGENT IN THE RL-GLUE-AGENT-ADAPTER */
		
		rlGlueAgent.setTeachingBoxAgent(tbAgent);
		
		/* RUN AN RL-GLUE EPISODE OF 1000 STEPS (MAX) */
		
		RLGlue.RL_episode(1000);
		
		// show a typical RL-Glue summary
		System.out.println("\n\n----------Summary----------");
		int totalSteps = RLGlue.RL_num_steps();
		double totalReward = RLGlue.RL_return();
		System.out.println("It ran for " + totalSteps + " steps, total reward was: " + totalReward);

		// Display the Q-Function (within the gridworld) with the helper method 
		Display(Q);
		
		// quit Java, including stopping the other threads
		System.exit(1);
	}
	
	// a simple helper function to display the tabular Q-Function within the grid-world
	private static void Display(TabularQFunction Q)
	{
		org.hswgt.teachingbox.core.rl.env.Action LEFT = new org.hswgt.teachingbox.core.rl.env.Action(new double[]{0});
		org.hswgt.teachingbox.core.rl.env.Action RIGHT = new org.hswgt.teachingbox.core.rl.env.Action(new double[]{1});
		org.hswgt.teachingbox.core.rl.env.Action UP = new org.hswgt.teachingbox.core.rl.env.Action(new double[]{2});
		org.hswgt.teachingbox.core.rl.env.Action DOWN = new org.hswgt.teachingbox.core.rl.env.Action(new double[]{3});
		
		DecimalFormat f = new DecimalFormat(" 0.00;-0.00");
		StringBuilder str= new StringBuilder();
		str.append("+---------------+---------------+---------------+\n");
		str.append("|     "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(3), UP))+"     |     "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(4), UP))+"     |     "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(5), UP))+"     |\n");
		str.append("| "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(3), LEFT))+"   "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(3), RIGHT))+" | "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(4), LEFT))+"   "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(4), RIGHT))+" | "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(5), LEFT))+"   "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(5), RIGHT))+" |\n");
		str.append("|     "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(3), DOWN))+"     |     "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(4), DOWN))+"     |     "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(5), DOWN))+"     |\n");
		str.append("+---------------+---------------+---------------+\n");
		str.append("|     "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(0), UP))+"     |     "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(1), UP))+"     |     "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(2), UP))+"     |\n");
		str.append("| "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(0), LEFT))+"   "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(0), RIGHT))+" | "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(1), LEFT))+"   "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(1), RIGHT))+" | "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(2), LEFT))+"   "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(2), RIGHT))+" |\n");
		str.append("|     "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(0), DOWN))+"     |     "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(1), DOWN))+"     |     "+f.format(Q.getValue(Grid2x3Environment.STATE_SET.get(2), DOWN))+"     |\n");
		str.append("+---------------+---------------+---------------+\n");
		System.out.println(str);
	}	
}
