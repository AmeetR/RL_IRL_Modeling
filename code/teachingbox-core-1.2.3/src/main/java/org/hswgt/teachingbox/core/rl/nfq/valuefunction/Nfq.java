package org.hswgt.teachingbox.core.rl.nfq.valuefunction;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.nfq.features.InputFeatures;
import org.hswgt.teachingbox.core.rl.nfq.learner.QValue;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;

public abstract class Nfq implements QFunction, Serializable {

	private static final long serialVersionUID = -4774988736651612147L;
	protected ActionSet actionSet;
	protected InputFeatures features;
	protected long seed = 0;
	protected long nextSeed = 0;
	protected Random randGenerator = new Random();

	// memorize iteration count
	protected int iteration = 0; 
	
	public Nfq(ActionSet actionSet, InputFeatures features)
	{
		this.actionSet = actionSet;
		this.features = features;
	}
	
	@Override
	public synchronized double getMaxValue(State state)
	{
		double maxValue = Double.NEGATIVE_INFINITY;
		
		// buffer for avoiding rerunning the state through the network
		double valueToCheck = Double.NEGATIVE_INFINITY;
		
		ActionSet validActions = this.actionSet.getValidActions(state);
		
		for (int i=0;i<validActions.size();i++)
		{
			valueToCheck = this.getValue(state, actionSet.get(i));
			if ( valueToCheck > maxValue ) 
			{
				maxValue = valueToCheck;
			}
		}
		
		return maxValue;
	}
	
	/**
	 * randomizes the weights with the next seed from the PRNG
	 */
	public void randomizeWeights() {
		this.randomizeWeights(randGenerator.nextLong());
	}
	
	/**
	 * randomizes the weights based on a given PRNG seed 
	 * @param seed The seed 
	 */
	abstract public void randomizeWeights(long seed);
	
	/**
	 * Trains the batch to the neural Q-function.
	 * @param epochs The number of maximum training epochs
	 * @param qBatch a java.util.List of QValue objects
	 */
	abstract public void trainBatch(int epochs, List<QValue> qBatch);
	
	/**
	 * returns the object for transforming an state/action pair to the neural network input features
	 * @return The InputFeature object
	 */
	public InputFeatures getInputFeatures() {
		return this.features;
	}
}
