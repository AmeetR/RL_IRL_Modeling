/**
 *
 * $Id: $
 *
 * @version   $Rev: $
 * @author    $Author: $
 * @date      $Date: $
 *
 */
package org.hswgt.teachingbox.core.rl.learner.stepsize;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.tabular.HashQFunction;

/**
 * @author Michel Tokic, Richard Cubek
 *
 */
public class SampleAverageAlpha implements StepSizeCalculator 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8568114359198189049L;
	/**
	 * The k-Array for the sample average method.
	 */
	private HashQFunction kArray = new HashQFunction(0);

    /* (non-Javadoc)
	 * @see org.hswgt.teachingbox.learner.stepsize.StepSizeAlpha#getValue(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action)
	 */
	public double getAlpha(State state, Action action) 
	{	
		double value = this.kArray.getValue(state, action);
		//this.kArray.setValue(state, action, value + 1);
		//return 1 / (value+1);
		
		
		//System.out.println ("ALPHA: alpha(s, " + action.get(0) + ")=" + 1/(value) + 
		//				    " - kArray[" + action.get(0) + "]=" + value);		
		//return 1/(this.kArray.getValue(state, action) + 1);
		return 1 / (value);		
	}

	/**
	 * Get the kArray.
	 * @return Reference to the kArray.
	 */
	public HashQFunction getkArray()
	{
		return this.kArray;
	}

	/**
	 * update the kArray
	 */
	public void update(State s, Action a, State sn, Action an, double r,
			boolean isTerminalState) {
		
		double value = this.kArray.getValue(s, a);		
		this.kArray.setValue(s, a, value + 1);
		//System.out.println("ALPHA: updating k(s,a) => k(" + s.get(0) + "," + a.get(0) + ")=" + this.kArray.getValue(s, a));		
	}

	
	/**
	 * reinitialize the kArray
	 */
	public void updateNewEpisode(State initialState) {
		// TODO Auto-generated method stub
		this.kArray = new HashQFunction(0);
		//System.out.println("ALPHA: clearning hash-table");
	}
}
