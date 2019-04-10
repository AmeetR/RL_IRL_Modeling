package org.hswgt.teachingbox.usecases.poleswingup;

import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.PoleSwingupEnvironment;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.plot.PolicyPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.QFunctionPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;
import org.hswgt.teachingbox.core.rl.viz.pole.PoleSwingUp2dWindow;


/**
 * <pre>
 * Environment: Pole swing-up
 * Function: Replay of a previously learned policy of the pole swing-up task 
 * 			 using Q-learning approximated by adaptive NRBF network.
 * Author: Michel Tokic 
 * </pre>
 */
public class Replay_PoleSwingUpANRBF {

    public static void main(String[] args) throws Exception
    {
    	// setup Q-Function
    	QFeatureFunction Q = ObjectSerializer.load("data/pole-swingup-anrbf.Q");
        //QFeatureFunction Q = new QFeatureFunction(net, PoleSwingupEnvironment.ACTION_SET);
    	
        // setup environment
        PoleSwingupEnvironment env = new PoleSwingupEnvironment();

        // setup policy with a little bit of exploration
        EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy(Q, PoleSwingupEnvironment.ACTION_SET, 0.00);

        // create agent
        Agent agent = new Agent(pi);

        // experiment setups
        final int MAX_EPISODES = 3;
        final int MAX_STEPS    = 1000;

        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);

	    // Initialize Q-Function 3D-Plotter
		QFunctionPlotter3D qPlotter = new QFunctionPlotter3D(Q);
		qPlotter.setBounds(
				new double[] { PoleSwingupEnvironment.MIN_POS, PoleSwingupEnvironment.MAX_POS}, 
				new double[] { PoleSwingupEnvironment.MIN_VEL, PoleSwingupEnvironment.MAX_VEL});
		qPlotter.setLabels("Position", "Velocity", "Costs");
		qPlotter.setTitle("PoleSwingUp QFunction");
		experiment.addObserver(new RuntimePlotter(qPlotter, Mode.EPISODE, 1, null));
		
		// Initialize Policy plotter
		PolicyPlotter3D policyPlotter = new PolicyPlotter3D(pi);
		policyPlotter.setBounds(
				new double[] { PoleSwingupEnvironment.MIN_POS, PoleSwingupEnvironment.MAX_POS}, 
				new double[] { PoleSwingupEnvironment.MIN_VEL, PoleSwingupEnvironment.MAX_VEL});
		policyPlotter.setLabels("Position", "Velocity", "Action");
		policyPlotter.setTitle("PoleSwingUp Policy");
		experiment.addObserver(new RuntimePlotter(policyPlotter, Mode.EPISODE, 1, null));

        
        // visualize pole
        PoleSwingUp2dWindow poleViz = new PoleSwingUp2dWindow("PoleSwingup ANRBF", 10);
        experiment.addObserver(poleViz);

        // run experiment
        experiment.run();

    }
}
