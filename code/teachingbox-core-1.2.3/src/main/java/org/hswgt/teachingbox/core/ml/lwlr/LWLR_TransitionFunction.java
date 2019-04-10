
package org.hswgt.teachingbox.core.ml.lwlr;

import java.util.LinkedList;
import java.util.List;

import org.hswgt.teachingbox.core.rl.datastructures.TransitionFunction;
import org.hswgt.teachingbox.core.rl.datastructures.TransitionProbability;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/**
 * A Locally Weighted Linear Regression Learned (approximated) TransitionFunction
 * for an Environment.
 *
 * @author Richard Cubek
 * 
 */
public class LWLR_TransitionFunction extends LWLR_VectorPrediction implements TransitionFunction
{
	private static final long serialVersionUID = -5286084587427700463L;

	/**
	 * Predicts the successor state s' in state s taking Action a. Since Locally Weighted
	 * Regression does not predict probabilities but single values, the returned List will
	 * always be of length 1, hence, that transition probability will have the probability 1.
	 * @param s The actual state.
	 * @param a The action to take.
	 * @return A List with a single entry: The TransitionProbability to the successor state.
	 */
	public List<TransitionProbability> getTransitionProbabilities(State s, Action a)
	{
		if (lwlrList.size() == 0)
		{
			try 
			{
				throw new Exception("No Instances for classifiers specified yet.");
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	
		// build a query vector from state and action to apply vector prediction
		
		DenseDoubleMatrix1D queryVector = new DenseDoubleMatrix1D(s.size() + a.size());

		// state vector variables
		for (int i = 0; i < s.size(); i++)
		{
			queryVector.set(i, s.get(i));
		}
		// action vector variables
		for (int i = 0; i < a.size(); i++)
		{
			queryVector.set(s.size() + i, a.get(i));
		}
		
		// predict the output vector
		
		DenseDoubleMatrix1D outputVector = null;
		
		try 
		{
			outputVector = predict(queryVector);
		} 
		catch (Exception e) 
		{ 
			e.printStackTrace(); 
		}
		
		TransitionProbability tp = new TransitionProbability(s, a, new State(outputVector.toArray()), 1.0);
		List<TransitionProbability> list = new LinkedList<TransitionProbability>();
		list.add(tp);
		
		return list;
	}	
}
