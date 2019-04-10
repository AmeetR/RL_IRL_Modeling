package org.hswgt.teachingbox.core.rl.nfq.util;

import java.io.Serializable;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.agent.AgentObserver;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.Environment;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.plot.TrajectoryPlotter2d;
import org.hswgt.teachingbox.core.rl.policy.Policy;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;


/**
 * This class evaluates a policy after every "N" episodes for "M" randomly drawn start positions.
 * @author tokicm
 *
 */
public class PolicyEvaluator implements AgentObserver, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4619339994423408787L;

	/**
	 * Logger
	 */
    private final static Logger log4j = Logger.getLogger("PolicyEvaluator");

	/**
	 * after testEpisodes the Policy is evaluated  
	 */
	private int testEpisode; 
	
	/**
	 * the amount of random start positions for evaluating the policy
	 */
	private int randStartPositions;
	
	/**
	 * The maximum number of steps
	 */
	private int maxSteps;
	
	/**
	 * the environment object
	 */
	private Environment env;
	
	/**
	 * the Experiment object to stop 
	 */
	private Experiment extExperiment;
	
	/**
	 * the policy object
	 */
	private Policy pi;
	
	private State initState = null;
	private State[] randStates = null;
	
	/**
	 * the local episode counter
	 */
	protected int iEpisode;
	
	
	private boolean visualize2d = false;
	private TrajectoryPlotter2d trajectoryPlotter; 
	private PolicyEvaluatorDataAverager dataAverager = new PolicyEvaluatorDataAverager();
	
	
	/**
	 * stuff for terminating the experiment
	 */
	public enum STOP_CRITERION {LOWER_OR_EQUAL_THAN, HIGHER_OR_EQUAL_THAN, NONE};
	private STOP_CRITERION stopCriterion = STOP_CRITERION.NONE;
	protected double terminationGoalStateRate = Double.POSITIVE_INFINITY;
	

	/**
	 * The constructor.
	 * @param pi The policy to be evaluated
	 * @param env The environment to be evaluated
	 * @param experiment The external experiment to stop when stop-criterion is achieved 
	 * @param testEpisode After every testEpisodes episodes, the learned Q-function is evaluated 
	 * @param randStartPositions The amount of random start positions to be evaluated 
	 * @param maxSteps The maximum number of steps per episode
	 * @param initState The initial state
	 */
	public PolicyEvaluator (Policy pi, Environment env, Experiment experiment, int testEpisode, int randStartPositions, int maxSteps, State initState) {
		this.initState = initState;
		this.pi = pi;
		this.env = env;
		this.extExperiment = experiment; 
		this.testEpisode = testEpisode;
		this.randStartPositions = randStartPositions;
		this.maxSteps = maxSteps;
		this.stopCriterion = STOP_CRITERION.NONE;
	}
	
	
	/**
	 * The constructor.
	 * @param pi The policy to be evaluated
	 * @param env The environment to be evaluated
	 * @param experiment The external experiment to stop when stop-criterion is achieved
	 * @param testEpisode After every testEpisodes episodes, the learned Q-function is evaluated 
	 * @param randStartPositions The amount of random start positions to be evaluated 
	 * @param maxSteps The maximum number of steps per episode
	 */
	public PolicyEvaluator (Policy pi, Environment env, Experiment experiment, int testEpisode, int randStartPositions, int maxSteps) {
		this.pi = pi;
		this.env = env;		
		this.extExperiment = experiment; 
		this.testEpisode = testEpisode;
		this.randStartPositions = randStartPositions;
		this.maxSteps = maxSteps;
		this.stopCriterion = STOP_CRITERION.NONE;
		
		// since no starting state was given, draw 'randStartPositions' random states
		randStates = new State[randStartPositions];
		
		for (int i=0; i< randStartPositions; i++) {
			env.initRandom();
			randStates[i] = env.getState();
		}
	}

	/**
	 * This function evaluates the policy after each learning episode
	 */
	@Override
	public void updateNewEpisode(State initState) { 
		
		if ((iEpisode%testEpisode)==0 && iEpisode > 0) {
			
			String startString = this.initState == null ? " random " : " fixed ";
			
			// create agent for given policy
			log4j.debug  ("EVALUATING Q-FUNCTION IN EPISODE=" + iEpisode + 
					" for " + randStartPositions + startString + " start positions");
			Agent agent = new Agent (pi);
		
			// memorize logging level
			Level oldLevel = Logger.getRootLogger().getLevel();
			Logger.getRootLogger().setLevel(Level.OFF);
			dataAverager.reset();
			trajectoryPlotter.clearTrajectories();
			
			// always use the same starting states!!! (for better comparison of results)
			for (int episode=0; episode < randStartPositions; episode++) {
			
				Experiment experiment = new Experiment (agent, env, 1, maxSteps);
				
				if (this.initState != null) {
					experiment.setInitState(initState);
				} else {
					experiment.setInitState(randStates[episode]);
				}
				experiment.addObserver(dataAverager);
				
				if (visualize2d) {
					experiment.addObserver(trajectoryPlotter);
				}
				
				// run experiment
				experiment.run();
			}
			
			Logger.getRootLogger().setLevel(oldLevel);
			
			log4j.debug ("TEST RESULTS OF EPISODE " + iEpisode + ":");
			log4j.debug ("  - AVERAGE STEPS: " + dataAverager.getAverageEpisodeLength());
			log4j.debug ("  - GOAL STATE RATE: " +  dataAverager.getGoalStateRate());
			//log4j.debug ("  - EPISODES: " +  dataAverager.getEpisodes());
			log4j.debug ("  - TERMINATIONS: " +  dataAverager.getCumTermination());
			
			trajectoryPlotter.setTitle(	"TrajectoryPlotter: episode=" + iEpisode + 
										", goalStateRate=" + dataAverager.getGoalStateRate() + 
										", avgSteps=" + dataAverager.getAverageEpisodeLength());
			trajectoryPlotter.plot();
			/**
			 * CHECK FOR TERMINATION CRITERION
			 */
			if (stopCriterion != STOP_CRITERION.NONE) {
				
				if (stopCriterion == STOP_CRITERION.HIGHER_OR_EQUAL_THAN) {
				
					if (dataAverager.getGoalStateRate() >= terminationGoalStateRate) {
						
						log4j.debug ("EXPERIMENT STOPPED BECAUSE TERMINATION CRITERION IS FULLFILLED: " + 
											stopCriterion.name() + " " + terminationGoalStateRate);
												
						log4j.debug ("FINAL PERFORMANCE OF LEARNING EPISODE '" + iEpisode + "':");
						log4j.debug ("  - FINAL AVERAGE STEPS: " + dataAverager.getAverageEpisodeLength());
						log4j.debug ("  - FINAL GOAL STATE RATE: " +  dataAverager.getGoalStateRate());
						//log4j.debug ("  - FINAL EPISODES: " +  dataAverager.getEpisodes());
						log4j.debug ("  - FINAL TERMINATIONS: " +  dataAverager.getCumTermination());
						
						this.extExperiment.stop();
					}
				}
				
				if (stopCriterion == STOP_CRITERION.LOWER_OR_EQUAL_THAN) {
					
					if (dataAverager.getGoalStateRate() <= terminationGoalStateRate) {
						
						log4j.debug ("EXPERIMENT STOPPED BECAUSE TERMINATION CRITERION IS FULLFILLED: " + 
								stopCriterion.name() + " " + terminationGoalStateRate);

						log4j.debug ("FINAL PERFORMANCE OF LEARNING EPISODE '" + iEpisode + "':");
						log4j.debug ("  - FINAL AVERAGE STEPS: " + dataAverager.getAverageEpisodeLength());
						log4j.debug ("  - FINAL GOAL STATE RATE: " +  dataAverager.getGoalStateRate());
						log4j.debug ("  - FINAL EPISODES: " +  dataAverager.getEpisodes());
						log4j.debug ("  - FINAL TERMINATIONS: " +  dataAverager.getCumTermination());
						
						this.extExperiment.stop();
					}
				}
			}
		}

		this.iEpisode++;
	}
	
	
	@Override
	public void update(State state, Action action, State arg2, Action arg3,
			double arg4, boolean arg5) {
	}
	
	/**
	 * This function configures termination of the learning process if a desired goal state rate is achieved. 
	 * @param stopCriterion The desired termination criterion
	 * @param terminationGoalStateRate Desired goal state (0 &lt; goalStateRate &lt; 1) rate for terminating the NFQ usecase. 
	 */
	public void terminateOnGoalStateRate (STOP_CRITERION stopCriterion, double terminationGoalStateRate) {
		this.stopCriterion = stopCriterion;
		this.terminationGoalStateRate = terminationGoalStateRate;
	}
	
	public void set2dVisualization(boolean visualize, String xLabel, String yLabel) {
		this.visualize2d = visualize;
		this.trajectoryPlotter = new TrajectoryPlotter2d();
		this.trajectoryPlotter.setLabel(xLabel, yLabel);
	}
	
}
