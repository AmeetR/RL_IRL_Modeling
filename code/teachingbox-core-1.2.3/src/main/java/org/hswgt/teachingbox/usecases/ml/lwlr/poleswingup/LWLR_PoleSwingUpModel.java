
package org.hswgt.teachingbox.usecases.ml.lwlr.poleswingup;

import java.util.List;

import org.hswgt.teachingbox.core.ml.lwlr.LWLR_TransitionFunction;
import org.hswgt.teachingbox.core.rl.datastructures.TransitionProbability;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.Environment;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.viz.pole.PoleSwingUp2dWindow;

import cern.jet.random.Uniform;

/**
 * This example shows, how the LWLR_TransitionFunction (which again is a 
 * LWLR_VectorPrediction) can be used to build a Teachingbox compatible
 * Environment, using Locally Weighted Linear Regression for simulation. 
 * 
 * @author Richard Cubek
 * 
 */
public class LWLR_PoleSwingUpModel implements Environment
{
	private static final long serialVersionUID = -2236854640701443258L;
	
	protected State state;
	protected double punish = 0;
	protected boolean terminal = false;
	// the LWLR_TransitionFunction represents the learned model
	protected LWLR_TransitionFunction tf = new LWLR_TransitionFunction();

	/**
	 * Constructor.
	 * @throws Exception The Exception
	 */
	public LWLR_PoleSwingUpModel() throws Exception
	{
		/* add the data sets (Instances), where to learn the model from (data set files
		 * have to be added in the same order as class variables have been added to the
		 * VectorDatasetWriter) 
		 */		
		tf.addDatasetFile(CollectTrainingData.DATA_FILE_SIN);
		tf.addDatasetFile(CollectTrainingData.DATA_FILE_COS);
		tf.addDatasetFile(CollectTrainingData.DATA_FILE_OMEGA);
		
		// set incoming/outgoing vector mapper
		tf.setInputVectorMapper(new QueryVectorMapper());
		tf.setOutputVectorMapper(new PredictionOutputVectorMapper());
		
		//tf.setKNearestNeighbours(12);
		//tf.setWeightingKernel(LocallyWeightedLinearRegression.TRICUBE);
	}
	
	/* (non-Javadoc)
	 * @see org.hswgt.teachingbox.env.Environment#doAction(org.hswgt.teachingbox.env.Action)
	 */
	public double doAction(Action a) 
	{
		List<TransitionProbability> list = tf.getTransitionProbabilities(state, a);
		// there is only one entry in the list (next state with probability 1)
		state = list.get(0).getNextState();
		
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.hswgt.teachingbox.env.Environment#getState()
	 */
	public State getState()
	{
		return state;
	}

	/* (non-Javadoc)
	 * @see org.hswgt.teachingbox.env.Environment#init(org.hswgt.teachingbox.env.State)
	 */
	public void init(State s) 
	{
		state = s;
		// thetaTotal = 0;
		terminal = false;
		punish = 0;
	}

	/* (non-Javadoc)
	 * @see org.hswgt.teachingbox.env.Environment#initRandom()
	 */
	public void initRandom() 
	{
		init(new State(new double[]{Uniform.staticNextDoubleFromTo(-Math.PI, Math.PI), 0}));
	}

	/* (non-Javadoc)
	 * @see org.hswgt.teachingbox.env.Environment#isTerminalState()
	 */
	public boolean isTerminalState() 
	{
		if (terminal == true && punish <= 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Test the model simply by playing with it (subjective test).
	 * @param args The command-line arguments
	 * @throws Exception The Exception
	 */
	public static void main (String[] args) throws Exception
	{
		// learned model of Environment
		LWLR_PoleSwingUpModel env = new LWLR_PoleSwingUpModel();
		env.init(new State(new double[]{Math.PI/2, 0}));
		// visualization/control
		PoleSwingUp2dWindow window = new PoleSwingUp2dWindow("pole-swing-up", 10);

		// run view iterations with the learned model environment
		
		State state = env.getState();
		Action action;
		
		System.out.println("use arrow keys to apply forces");
		
		// end the loop by closing the window
		while (true)
		{
			// update window
			window.setAngle(state.get(0));

			// store state, action in t (for t+1)
			action = window.getAction();			
			env.doAction(action); // time step
			state = env.getState();
		}
	}
}
