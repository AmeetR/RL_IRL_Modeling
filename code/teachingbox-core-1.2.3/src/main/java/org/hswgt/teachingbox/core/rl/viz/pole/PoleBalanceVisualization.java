package org.hswgt.teachingbox.core.rl.viz.pole;

import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.PoleBalanceEnvironment;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.learner.Learner;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;


public class PoleBalanceVisualization extends PoleSwingUpVisualization {

	private static final long serialVersionUID = 5584604857743863047L;

	public PoleBalanceVisualization(Agent agent, Learner learner, Mode mode,
			int interval, int nSteps) {
		super(agent, learner, mode, interval, nSteps);
		
		this.env = new PoleBalanceEnvironment();
		this.startState = null;
		// TODO Auto-generated constructor stub
	}

    public void visualize() {
        // if start state was configured use it 
    	if (startState != null) {
    		env.init(startState);
    	// configure random position if no start state was configured 
    	} else {
    		env.initRandom();
    	}
        // switch off learning while visualizing
        if (learner != null) {
        	this.agent.removeObserver(learner);
        }

        // The PoleSwingUp2dWindow object, create one locally so it's possible to
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
            Action inverseA = new Action(new double[]{a.get(0)*-1});
            window.update (s, inverseA, null, null, r, isTerminalState);

            // exit when terminal state is arrived
            if (isTerminalState) {
                break;
            }
        }
        window.close();
        // switch on learning again
        if (learner != null) {
        	this.agent.addObserver(learner);
        }
    }
}
