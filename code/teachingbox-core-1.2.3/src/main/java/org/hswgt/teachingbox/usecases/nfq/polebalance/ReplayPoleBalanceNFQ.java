package org.hswgt.teachingbox.usecases.nfq.polebalance;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.env.PoleSwingupEnvironment;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQPoleBalanceEnvironment;
import org.hswgt.teachingbox.core.rl.plot.PolicyPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.QFunctionPlotter3D;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter;
import org.hswgt.teachingbox.core.rl.plot.TrajectoryPlotter2d;
import org.hswgt.teachingbox.core.rl.plot.RuntimePlotter.Mode;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;
import org.hswgt.teachingbox.core.rl.viz.pole.PoleBalanceVisualization;

/**
 * A usecase running the PoleBalance-environment (without learning) 
 * five times from random starting positions. 
 * An already learned value function is used instead of learning from scratch.
 * The policy is Greedy, i.e 0% random actions
 * @author tokicm
 */
public class ReplayPoleBalanceNFQ {
	
    
    // experiment setups
	public static int MAX_EPISODES = 1;
    public static int MAX_STEPS    = 1;

    // plot the value function after n episodes
	public static int PLOT_EPISODES = 1;
	public static int EVAL_STEPS = 50000;
	public static final String Q_FILENAME = "data/piEval-NFQ-PoleBalance.Q";
	
		
	public static void main(String[] args) throws Exception
	{
	
		Logger.getRootLogger().setLevel(Level.OFF);
		
        // setup environment        	
		NFQPoleBalanceEnvironment env = new NFQPoleBalanceEnvironment();
	     
	    QFunction Q = ObjectSerializer.load(Q_FILENAME);
	        	            
	    // action selection with 5% random actions
	    EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy (Q, PoleSwingupEnvironment.ACTION_SET, 0.0);
	             
	    // create agent
	    Agent agent = new Agent(pi);
	        
		// setup experiment
		Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);	        	      
		        
		// Initialize Plotter
		QFunctionPlotter3D qPlotter = new QFunctionPlotter3D(Q);
		qPlotter.setBounds(
				new double[] { NFQPoleBalanceEnvironment.MIN_POS, NFQPoleBalanceEnvironment.MAX_POS}, 
				new double[] { NFQPoleBalanceEnvironment.MIN_VEL, NFQPoleBalanceEnvironment.MAX_VEL});
    	qPlotter.setLabels("Position", "Velocity", "Q");
		qPlotter.setFilename("mc-policy.svg");
		qPlotter.setTitle("Pole-Balance QFunction");
		qPlotter.plot();
     		
        
    	PolicyPlotter3D policyPlotter = new PolicyPlotter3D(pi);
    	policyPlotter.setBounds(
				new double[] { NFQPoleBalanceEnvironment.MIN_POS, NFQPoleBalanceEnvironment.MAX_POS}, 
				new double[] { NFQPoleBalanceEnvironment.MIN_VEL, NFQPoleBalanceEnvironment.MAX_VEL});
    	policyPlotter.setLabels("Position", "Velocity", "Action");
    	policyPlotter.setFilename("pb-policy.gnuplot");
    	policyPlotter.setTitle("Pole-Balance Policy");
    	//policyPlotter.setView(0.01, 0.01);
    	policyPlotter.plot();
    	
    	// pole visualization
	    PoleBalanceVisualization viz = new PoleBalanceVisualization(agent, null, 
	    		Mode.EPISODE, PLOT_EPISODES, EVAL_STEPS);
	    experiment.addObserver(viz);
	    
		// TrajectoryPlotter
		//TrajectoryPlotter2d trajectoryPlotter = new TrajectoryPlotter2d();
		//trajectoryPlotter.setLabel("Position", "Velocity");
		//trajectoryPlotter.setTitle("PoleBalance NFQ trajectories");
		//agent.addObserver(trajectoryPlotter); // observes step/episode updates
		//experiment.addObserver(new RuntimePlotter(trajectoryPlotter, Mode.EPISODE, 1, null)); // calls plot() after each episode
		    
		// run experiment
	    experiment.run();
	}
}
