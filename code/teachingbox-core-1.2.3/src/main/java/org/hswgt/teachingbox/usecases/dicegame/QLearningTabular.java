	/**
	 *
	 * $Id: QLearningTabular.java 35 2009-05-27 09:45:27Z Markus Schneider $
	 *
	 * @version   $Rev: 35 $
	 * @author    $Author: Markus Schneider $
	 * @date      $Date: 2009-05-27 11:45:27 +0200 (Mi, 27 Mai 2009) $
	 *
	 */
	
	package org.hswgt.teachingbox.usecases.dicegame;
	
	import java.text.DecimalFormat;
	import java.util.Collections;
	import java.util.Comparator;
	import java.util.HashMap;
	import java.util.LinkedList;
	import java.util.Map.Entry;
	
	import org.apache.log4j.Level;
	import org.apache.log4j.Logger;
	import org.hswgt.teachingbox.core.rl.agent.Agent;
	import org.hswgt.teachingbox.core.rl.env.Action;
	import org.hswgt.teachingbox.core.rl.env.DiceGameEnvironment;
	import org.hswgt.teachingbox.core.rl.env.State;
	import org.hswgt.teachingbox.core.rl.experiment.Experiment;
	import org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver;
	import org.hswgt.teachingbox.core.rl.learner.TabularQLearner;
	import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
	import org.hswgt.teachingbox.core.rl.tabular.HashQFunction;
	import org.hswgt.teachingbox.core.rl.tabular.TabularQFunction;
	import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;
	
	/**
	 * <pre>
	 * Environment: MountainCar
	 * Algorithm: Q-Learning
	 * Approximation: Adaptive RBFNetwork
	 * </pre>
	 */
	public class QLearningTabular
	{
	    public static void main(String[] args) throws Exception
	    {
	        Logger.getRootLogger().setLevel(Level.INFO);
	        
	        // init new Q-Function with default value 0
	        TabularQFunction Q = new HashQFunction(0);
	        
	        // setup environment
	        DiceGameEnvironment env = new DiceGameEnvironment();
	        
	        // setup policy
	        // we use a greedy e-policy with epsilon 0.9 here
	        // in order to make decisions we have to provide the
	        // QFunction and the set of all possible action
	        EpsilonGreedyPolicy pi = new EpsilonGreedyPolicy(Q, DiceGameEnvironment.ACTION_SET, 1);
	        
	        // create agent
	        Agent agent = new Agent(pi);
	        
	        // experiment setups
	        final int MAX_EPISODES = 1000;
	        final int MAX_STEPS    = 5000;
	        final double alpha     = 1.0;
	        final double gamma     = 0.999;
	        final double lambda    = 0.9; // e-traces
	        
	        // setup experiment
	        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);
	        
	        // setup learner
	        TabularQLearner learner = new TabularQLearner(Q);
	        learner.setAlpha(alpha);
	        learner.setGamma(gamma);
	        learner.setLambda(lambda);
	        
	        // attach learner to agent
	        agent.addObserver(learner);
	        
	        // Display Q-Function after each step.
	        experiment.addObserver(new DisplayValuesObserver(Q) );
	        
	        // run experiment
	        experiment.run();
	    }
	}
	
	/**
	 * This is an example how to implement an experiment observer. Imagine
	 * that you want to call the <pre>Grid2x3Environment.Display(Q);</pre> method after
	 * every step of the agent. This can be easily done with the help of an
	 * <pre>ExperimentObserver</pre>. The <pre>update</pre> method will be called
	 * after every step and the <pre>updateNewEpisode</pre> informs that a new episode 
	 * has started
	 */
	class DisplayValuesObserver implements ExperimentObserver
	{
	    private static final long serialVersionUID = -4936384338773368610L;
	    QFunction Q;
	    HashMap<State, Integer> statecounter = new HashMap<State, Integer>();
	    
	    /**
	     * We need the actual QFunction to display and therefore we have
	     * to provide it here
	     * @param Q
	     */
	    public DisplayValuesObserver(QFunction Q){
	        this.Q = Q;
	    }
	    
	    /* (non-Javadoc)
	     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#update(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
	     */
	    public void update(State s, Action a, State sn, Action an, double r, boolean terminalState)
	    {
	        Integer count = statecounter.get(s);
	        if( count == null )
	            statecounter.put(s, 1);
	        else
	            statecounter.put(s, count+1);
	        
	        try
	        {
	
	            System.out.println("s: "+s);
	            System.out.println("a: "+a);
	            System.out.println("r: "+r);
	            System.out.println("sn: "+sn);
/*	            System.out.print("press any key: ");
	            System.in.read();
*/	        }
	        catch (Exception e)
	        {
	            System.err.println("Cannot display Q-Function:");
	            e.printStackTrace();
	        }
	        
	    }
	    /* (non-Javadoc)
	     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateNewEpisode(org.hswgt.teachingbox.env.State)
	     */
	    public void updateNewEpisode(State initialState){        
	    }

	    
	    public void updateExperimentStop()
	    {	final int max_anz_q_werte = 50;
//	    	if(episode == 3){
	        System.out.print("Q(s,THROW): ");
	    	for(int i=0; i<max_anz_q_werte; i++){
	    		double q1 = Q.getValue(new State(new double[]{i}), DiceGameEnvironment.THROW);
	    		System.out.print(new DecimalFormat("00.00").format(q1) + " ");
	    	}
	        System.out.println();
	        System.out.print("Q(s,STOP):  ");
	    	for(int i=0; i<max_anz_q_werte; i++){
	    		double q2  = Q.getValue(new State(new double[]{i}), DiceGameEnvironment.STOP);
	    		System.out.print(new DecimalFormat("00.00").format(q2) + " ");
	    	}
	        System.out.println();
	        
	        // hier erstellich nur aus der HashMap eine List, die ich sortieren kann
	        // details sind nicht so wichtig
	        LinkedList<Entry<State, Integer>> list = new LinkedList<Entry<State, Integer>>(statecounter.entrySet());
	        Collections.sort(list, new Comparator<Entry<State, Integer>>(){
                public int compare(Entry<State, Integer> o1,
                        Entry<State, Integer> o2)
                {
                    return Double.compare(o1.getKey().get(0), o2.getKey().get(0));
                }});
	        
	        // hier wird ueber die ganze (sortierte) liste iteriert.
	        for( Entry<State, Integer> e : list )
	        {
	            System.out.println("Visited " + e.getKey() + "\t" + e.getValue() + "\t times");
	        }
	        
//	    	}
	/*    	for(int i=0; i<100; i++){
	    		double q1 = Q.getValue(new State(new double[]{i}), DiceGameEnvironment.THROW);
	    		double q2 = Q.getValue(new State(new double[]{i}), DiceGameEnvironment.STOP);
	   	}
	*/   	       // ...
	    }
	
	    /* (non-Javadoc)
	     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateExperimentStart()
	     */
	    public void updateExperimentStart()
	    {
	        // ... 
	        
	    }
	
	    /* (non-Javadoc)
	     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateExperimentStop()
	     */
/*	    public void updateExperimentStop()
	    {
	        // ...
	    }
*/
	}
