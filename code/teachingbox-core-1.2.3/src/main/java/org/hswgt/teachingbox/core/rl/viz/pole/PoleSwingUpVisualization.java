package org.hswgt.teachingbox.core.rl.viz.pole;

import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.Environment;
import org.hswgt.teachingbox.core.rl.env.PoleSwingupEnvironment;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.learner.Learner;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.viz.Visualization;

/**
 * The PoleSwingUpVisualization is a visualization of the pole swing-up
 * benchmark. It can attached to an experiment or its static method 
 * can be used to simulate without any previous setup.
 * 
 * Autor: Michel Tokic
 */
public class PoleSwingUpVisualization extends Visualization {

    /**
     *
     */
    private static final long serialVersionUID = -4542463829283768402L;

    // the agent to simulate
    protected Agent agent;
    
    // number of steps to simulate
    protected int nSteps;
    
    // the start state
    protected State startState = new State (new double[]{Math.PI,0});

    // use Environment instead of PoleSwingupEnvironment since the environment used
    // can be some wrapped PoleSwingupEnvironment like DelayedRewardEnv(PoleSwingupEnvironment)
    // and we want to be able to visualize such an environment too!
    Environment env;
    // used to switch off learning while visualizing
    protected Learner learner;

    /**
     * Constructor
     * @param agent The @Agent so simulate
	 * @param learner The @Learner to disable during simulation
     * @param mode The simulation mode (e.g. every $interval steps, every $interval episodes ... )
     * @param interval The plotting interval. (See mode)
     * @param nSteps The number of steps to simulate
     */
    public PoleSwingUpVisualization(Agent agent, Learner learner, Mode mode,
            int interval, int nSteps) {
        super(mode, interval);
        this.agent = agent;
        this.learner = learner;
        this.mode = mode;
        this.plot_interval = interval;
        this.nSteps = nSteps;
        this.env = new PoleSwingupEnvironment();
    }
    
    /**
     * Constructor
     * Run an agent simulation every 10 episodes
     * @param agent The @Agent so simulate
	 * @param learner The @Learner to disable during simulation
     * @param nSteps The number of steps to simulate
     */
    public PoleSwingUpVisualization(Agent agent, Learner learner, int nSteps) {
        this(agent, learner, Mode.EPISODE, 10, nSteps);
    }
    
    /**
     * Constructor
     * Run an agent simulation every 10 episodes and make 500 simulation steps
     * @param agent The @Agent so simulate
	 * @param learner The @Learner to disable during simulation
     */
    public PoleSwingUpVisualization(Agent agent, Learner learner) {
        this(agent, learner, Mode.EPISODE, 10, 500);
    }
    
    public void setStartState (State s) {
    	this.startState = s;
    }
	
	
    public void visualize() {
        // get initial state and action
        env.init(startState);
        // switch off learning while visualizing
        this.agent.removeObserver(learner);

        // The PoleSwingUp2dWindow object, create one locally so it's possibl to
        // close the window after visualizing
        PoleSwingUp2dWindow window = new PoleSwingUp2dWindow("pole swing-up visualization", this.delayTime);
        // initialize episode
        window.updateNewEpisode(startState);
        State s = env.getState();
        Action a = agent.start(s);

        log4j.debug("Start Pole Swing-Up simulation over "+nSteps+" steps");
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
        window.close();
        // switch on learning again
        this.agent.addObserver(learner);
    }

    // getter and setter

    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }
}
