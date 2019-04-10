package org.hswgt.teachingbox.core.rl.learner.stepsize;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * @author Richard Cubek
 *
 */
public class ConstantTabularStateAlpha implements StepSizeCalculator
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2205145171763975273L;
	/** value of step size parameter alpha */
	private double alpha = 1.0;

	/**
	 * Constructor.
	 * @param alpha Value of step size parameter alpha.
	 */
	public ConstantTabularStateAlpha(double alpha)
	{
		this.alpha = alpha;
	}
	
	/**
	 * Set Alpha
	 * @param alpha Value of alpha.
	 */
	public void setAlpha(double alpha)
	{
		this.alpha = alpha;
	}
	
	/* (non-Javadoc)
	 * @see org.hswgt.teachingbox.learner.StepSizeAlpha#getValue(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action)
	 */
	public double getAlpha(State state, Action action) 
	{
		return this.alpha;
	}

	public void update(State s, Action a, State sn, Action an, double r,
			boolean isTerminalState) {
		// TODO Auto-generated method stub
		
	}

	public void updateNewEpisode(State initialState) {
		// TODO Auto-generated method stub
		
	}
}
