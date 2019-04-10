
package org.hswgt.teachingbox.usecases.grid2x3;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.datastructures.RewardFunction;
import org.hswgt.teachingbox.core.rl.datastructures.TransitionFunction;
import org.hswgt.teachingbox.core.rl.datastructures.TransitionProbability;
import org.hswgt.teachingbox.core.rl.dp.ValueIterationLearner;
import org.hswgt.teachingbox.core.rl.dp.TabularValueIteration;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.Grid2x3Environment;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.tabular.HashValueFunction;
import org.hswgt.teachingbox.core.rl.tabular.TabularValueFunction;

import cern.colt.matrix.linalg.SeqBlas;

/**
 * @author Richard Cubek
 *
 */
public class Grid2x3TabularValueIteration 
{
	public static void main(String[] args)
	{
		Logger.getRootLogger().setLevel(Level.INFO);
		Logger.getLogger(ValueIterationLearner.class.getSimpleName()).setLevel(Level.DEBUG);
	
		// init new V-Function with default value 0
		TabularValueFunction V = new HashValueFunction(0);
		
		// setup environment
		Grid2x3Environment env = new Grid2x3Environment();
		
        // setup transition function and reward function
        TransitionFunction tf = new Grid2x3TransitionFunction();
        RewardFunction rf = new Grid2x3RewardFunction();
        
        // setup learner
        TabularValueIteration learner = new TabularValueIteration(V, Grid2x3Environment.STATE_SET, Grid2x3Environment.ACTION_SET, tf, rf);
        learner.setTheta(0.1);
        learner.setMaxSweeps(100);
        
        int iterations = learner.run();
        
        // print result
        
        System.out.println("State[row/column]\tstate value");
        for (int i = 0; i < Grid2x3Environment.STATE_SET.size(); i++)
        {
        	System.out.println(Grid2x3Environment.STATE_SET.get(i)+"\t\t"+V.getValue(Grid2x3Environment.STATE_SET.get(i)));
        }
        
        if (iterations > -1)
        {
        	System.out.println("\nconvergence reached after " + iterations + " sweeps");
        }
        else
        {
        	System.out.println("\nconvergence not reached");
        }
	}
}

class Grid2x3TransitionFunction_ implements TransitionFunction
{
    private static final long serialVersionUID = 2721625934013237870L;

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.datastructures.TransitionFunction#getTransitionProbabilities(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action)
     */
    public List<TransitionProbability> getTransitionProbabilities(State s,
            Action a)
    {
        State sn = s.copy();
        
        // s = s + a;
        SeqBlas.seqBlas.daxpy(1, a, sn);
        
        // check range [0 2][0 1]
        sn.set(0, Math.min(sn.get(0), 2));
        sn.set(0, Math.max(sn.get(0), 0));
        sn.set(1, Math.min(sn.get(1), 1));
        sn.set(1, Math.max(sn.get(1), 0));
        
        List<TransitionProbability> tp = new LinkedList<TransitionProbability>();
        tp.add(new TransitionProbability(s,a,sn,1));
        return tp;
    }
}

class Grid2x3RewardFunction_ implements RewardFunction
{
    private static final long serialVersionUID = 2452455971918030887L;

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.datastructures.RewardFunction#getReward(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State)
     */
    public double getReward(State s, Action a, State sn)
    {
        // calc reward
        double r = 0;
        if( s.get(1) == 0 )
        {
            if( a.equals( Grid2x3Environment.RIGHT ) )
            {
                r = +1;
            }
            if( a.equals( Grid2x3Environment.LEFT ) )
            {
                r = -1;
            }
        }
        return r;
    }    
}