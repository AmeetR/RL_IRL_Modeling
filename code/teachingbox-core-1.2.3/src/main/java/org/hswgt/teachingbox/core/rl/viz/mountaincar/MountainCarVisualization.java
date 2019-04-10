package org.hswgt.teachingbox.core.rl.viz.mountaincar;

import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.agent.AgentObserver;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.Environment;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver;
import org.hswgt.teachingbox.core.rl.learner.Learner;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.viz.Visualization;


/**
 * The MountainCarVisualization is a visualization of the mountaincar
 * benchmark. It can attached to an experiment or its static method 
 * can be used to simulate without any previous setup.
 * 
 * Autor: Michel Tokic
 */
public class MountainCarVisualization extends Visualization implements AgentObserver {

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 6385065490754920549L;

	// the agent to simulate
    protected Agent agent;
    
    // number of steps to simulate
    protected int nSteps;

    // window
    MountainCar2dWindow window = new MountainCar2dWindow("MountainCar Visualization", 30);

    Environment env;
    
    // used to switch off learning while visualizing
    protected Learner learner;
    
	
    /**
     * Constructor
     * @param agent The @Agent so simulate
     * @param learner The @Learner object
     * @param env The @Environment
     * @param mode The simulation mode (e.g. every $interval steps, every $interval episodes ... )
     * @param interval The plotting interval. (See mode)
     * @param nSteps The number of steps to simulate
     */
    public MountainCarVisualization(Agent agent, Learner learner, Environment env, Mode mode,
            int interval, int nSteps) {
        super(mode, interval);
        this.agent = agent;
        this.learner = learner;
        this.mode = mode;
        this.plot_interval = interval;
        this.nSteps = nSteps;
        this.env = env;
    }
    
    /**
     * Constructor
     * Run an agent simulation every 10 episodes
     * @param agent The @Agent so simulate
     * @param learner The @Learner object
     * @param env The @Environment
     * @param nSteps The number of steps to simulate
     */
    public MountainCarVisualization(Agent agent, Learner learner,  Environment env, int nSteps) {
        this(agent, learner, env, Mode.EPISODE, 10, nSteps);
    }
    
    /**
     * Constructor
     * Run an agent simulation every 10 episodes and make 500 simulation steps
     * @param agent The @Agent so simulate
     * @param learner The @Learner object
     * @param env The @Environment
     */
    public MountainCarVisualization(Agent agent, Learner learner,  Environment env) {
        this(agent, learner, env, Mode.EPISODE, 10, 500);
    }
    

    public void visualize() {

        // switch off learning while visualizing
    	if (learner != null) {
    		this.agent.removeObserver(learner);
    	}
    	
        // get initial state and action
        env.initRandom();
        State s = env.getState();
        Action a = agent.start(s);
        
        log4j.debug("Start MountainCar simulation over maximum of "+nSteps+" steps");
        window.updateNewEpisode(s);
        for(int step=1; step<=nSteps; step++)
        {
            // perform action, observe r, sn
            double r = env.doAction(a);
            boolean isTerminalState = env.isTerminalState();
            s = env.getState();
            a = agent.nextStep(s, r, isTerminalState);

            // update visualization model
            window.update (s, a, null, null, r, isTerminalState);
            
            // exit when terminal state is arrived
            if (isTerminalState) {
            	break;
            }
        }
        // switch on learning again
    	if (learner != null) {
    		this.agent.addObserver(learner);
    	}
    }

    // getter and setter

    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }
}
