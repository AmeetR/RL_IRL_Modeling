package org.hswgt.teachingbox.usecases.poleswingup;

import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.PoleSwingupEnvironment;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;
import org.hswgt.teachingbox.core.rl.viz.pole.PoleSwingUp2dWindow;


/**
 * <pre>
 * Environment: Pole swing-up
 * Function: Replay of a previously learned policy of the pole swing-up task. The  
 * 			 function has been learned by Q-learning with Tile-Coding approximation.
 * Author: Michel Tokic 
 * </pre>
 */
public class Replay_PoleSwingUpTileCoding {

    public static void main(String[] args) throws Exception
    {

        // load Q-function
        QFeatureFunction Q = ObjectSerializer.load("data/pole-swingup-tileCoding.Q");
        
        // setup environment
        PoleSwingupEnvironment env = new PoleSwingupEnvironment();

        // setup policy with a little bit of exploration
        EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy(Q, PoleSwingupEnvironment.ACTION_SET,0.00);

        // create agent
        Agent agent = new Agent(pi);

        // experiment setups
        final int MAX_EPISODES = 3;
        final int MAX_STEPS    = 1000;

        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);

        PoleSwingUp2dWindow window = new PoleSwingUp2dWindow("pole swing-up", 10);
        experiment.addObserver(window);

        // run experiment
        experiment.run();

    }

}
