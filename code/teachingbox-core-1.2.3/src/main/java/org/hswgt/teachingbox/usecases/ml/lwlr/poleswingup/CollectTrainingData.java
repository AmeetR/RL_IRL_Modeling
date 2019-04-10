
package org.hswgt.teachingbox.usecases.ml.lwlr.poleswingup;

import org.hswgt.teachingbox.core.ml.lwlr.LWLR_VectorDatasetWriter;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.PoleSwingupEnvironment;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.viz.pole.PoleSwingUp2dWindow;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;

/**
 * Collect training data for the PoleSwingUp dynamics, using the PoleSwingUp
 * Environment as Simulation. If visualization is wanted, PoleSwingUp2dWindow 
 * can be used as visualization and key input controller (to define actions).
 * 
 * @author Richard Cubek
 *
 */
public class CollectTrainingData
{
	public static final String DATA_FILE_SIN = "data/ml/lwlr/poleswingup/psu_sin_theta.arff";
	public static final String DATA_FILE_COS = "data/ml/lwlr/poleswingup/psu_cos_theta.arff";
	public static final String DATA_FILE_OMEGA = "data/ml/lwlr/poleswingup/psu_theta_d.arff";
	
	public CollectTrainingData() throws Exception
	{
		/* BUILD AN WEKA INSTANCES OBJECT */

		// the vector with the attributes
		FastVector attributeVector = new FastVector();
		
		// THE ATTRIBUTES
		
		// numeric attributes
		Attribute att1 = new Attribute("sin_theta");
		Attribute att2 = new Attribute("cos_theta");
		Attribute att3 = new Attribute("theta_d");
		// nominal attribute
		FastVector nominalVector = new FastVector();
		nominalVector.addElement("NONE");
		nominalVector.addElement("RIGHT");
		nominalVector.addElement("LEFT");
		Attribute att4 = new Attribute("force", nominalVector);
		// numeric attribute to be predicted later
		Attribute att5 = new Attribute("class");
		// add attributes to attributeVector
		attributeVector.addElement(att1);
		attributeVector.addElement(att2);
		attributeVector.addElement(att3);
		attributeVector.addElement(att4);
		attributeVector.addElement(att5);
		// create Instances (raw dataset) object with the attributeVector
		Instances dataset = new Instances("pole_swing_up", attributeVector, 0);
		// define the attribute to be predicted (learned)
		dataset.setClassIndex(dataset.numAttributes() - 1);

		/* CREATE THE VECTOR DATASET WRITER */
		
		// VectorDatasetWriter to write samples of the real TransitionFunction
		LWLR_VectorDatasetWriter writer = new LWLR_VectorDatasetWriter(dataset);
		
		// initialize dataset writer for each variable to be predicted later
		writer.initDatasetWriter(DATA_FILE_SIN);
		writer.initDatasetWriter(DATA_FILE_COS);
		writer.initDatasetWriter(DATA_FILE_OMEGA);

		// we use the same input VectorMapper as later for prediction
		writer.setInputVectorMapper(new QueryVectorMapper());
		// map successor states to the vector format used for prediction
		writer.setOutputVectorMapper(new WriterOutputVectorMapper());

		// collect data
		//collectDataManually(writer);
		//collectDataRandomly(writer);
		collectDataNaturally(writer);
		
		// close writer and window
		writer.close();
		
		/* EVALUATE THE QUALITY OF THE COLLECTED DATA */
		/*
		DataSource source = new DataSource(DATA_PATH + "psu_dataset_sin_theta.arff");
		Instances instances = source.getDataSet();
		instances.setClassIndex(dataset.numAttributes() - 1);
		...
		*/
	}

	// collect the data manually with PoleSwingUp2dWindow visualization
	private void collectDataManually(LWLR_VectorDatasetWriter writer) throws Exception
	{
		// Environment to learn the dynamics model from
		PoleSwingupEnvironment env = new PoleSwingupEnvironment();
		env.init(new State(new double[]{Math.PI/4, 0}));

		// visualization/control
		PoleSwingUp2dWindow window = new PoleSwingUp2dWindow("pole-swing-up", 10);

		State state = env.getState();
		State oldState;
		Action action;
		int control;
		
		for (int i = 0; i < 250; i++)
		{
			// update window (will sleep ~20 ms to make visualization possible)
			window.setAngle(state.get(0));

			// store state, action in t (for t+1)
			oldState = state;
			action = window.getAction();			
			env.doAction(action); // time step
			state = env.getState();
			
			// write data with VectorDatasetWriter (each 5th step)
			if (i % 5 == 0)
			{
				writer.writeVectors(oldState, action, state);
			}
		}
		
		window.close();		
	}
	
	// collect the data randomly without or with visualization (slower)
	private void collectDataRandomly(LWLR_VectorDatasetWriter writer) throws Exception
	{
		boolean visualize = false;
		
		// Environment to learn the dynamics model from
		PoleSwingupEnvironment env = new PoleSwingupEnvironment();
		// visualization
		PoleSwingUp2dWindow window = null;
		// begin at natural initial state
		env.init(new State(new double[]{Math.PI, 0}));
		// visualization
		if (visualize)
			window = new PoleSwingUp2dWindow("pole-swing-up", 10);
		// action set to randomly choose actions from
		ActionSet actionSet = PoleSwingUp2dWindow.ACTION_SET;
		
		// 1 iteration simulates 1 second real time (average)
		for (int i = 0; i < 1500; i++)
		{
			System.out.println("episode " + i);
			double randTheta = (Math.random() - 0.5) * 2 * Math.PI;
			double randThetad = (Math.random() - 0.5) * 2 * 2.5 * Math.PI;
			// define one of three actions randomly
			Action randAction = new Action(0);
			double actionHelp = Math.random();
			if (actionHelp <= 0.333) randAction = actionSet.get(0);
			if (actionHelp > 0.333 && actionHelp < 0.667) randAction = actionSet.get(1);
			if (actionHelp >= 0.667) randAction = actionSet.get(2);
			// set random time between 0 and 2 seconds (physical time step is 0.02 s)
			int randSteps = (int)(Math.random() * 100);
			
			env.init(new State(new double[]{randTheta, randThetad}));
	
			State state = env.getState();
			State oldState;
			
			for (int j = 0; j < randSteps; j++)
			{
				// update window (will sleep ~20 ms to make visualization possible)
				if (visualize)
					window.setAngle(state.get(0));
	
				// store state, action in t (for t+1)
				oldState = state;
				env.doAction(randAction); // time step
				state = env.getState();
				
				// write data with VectorDatasetWriter (each 5th step)
				if (j % 5 == 0)
				{
					writer.writeVectors(oldState, randAction, state);
				}
			}
		}
		
		if (visualize)
			window.close();		
	}

	// collect the data naturally, as one would do with a physical pole swing up
	// environment (can't jump between non-neighbor states), without (fast) or with 
	// visualization (slow)
	private void collectDataNaturally(LWLR_VectorDatasetWriter writer) throws Exception
	{
		// setting true will visualize the process (taking about 30 minutes)
		boolean visualize = false;
		
		// Environment to learn the dynamics model from
		PoleSwingupEnvironment env = new PoleSwingupEnvironment();
		// visualization
		PoleSwingUp2dWindow window = null;
		// begin at natural initial state
		env.init(new State(new double[]{Math.PI, 0}));
		// visualization
		if (visualize)
			window = new PoleSwingUp2dWindow("pole-swing-up", 10);
		// action set to randomly choose actions from
		ActionSet actionSet = PoleSwingUp2dWindow.ACTION_SET;
		
		// 1 iteration simulates 1 second real time (average)
		for (int i = 0; i < 1200; i++)
		{
			System.out.println("episode " + i);
			// define one of three actions randomly
			Action randAction = new Action(0);
			double actionHelp = Math.random();
			if (actionHelp <= 0.333) randAction = actionSet.get(0);
			if (actionHelp > 0.333 && actionHelp <= 0.667) randAction = actionSet.get(1);
			if (actionHelp > 0.667) randAction = actionSet.get(2);
			// physical time step is 0.02 seconds, random time between 0 and 2 seconds
			int randSteps = (int)(Math.random() * 100);
			
			State state = env.getState();
			State oldState;
			
			for (int j = 0; j < randSteps; j++)
			{
				// update window (will sleep ~20 ms to make visualization possible)
				if (visualize)
					window.setAngle(state.get(0));
	
				// store state, action in t (for t+1)
				oldState = state;
				env.doAction(randAction); // time step
				state = env.getState();
				
				// write data with VectorDatasetWriter (each 5th step)
				if (j % 5 == 0)
				{
					writer.writeVectors(oldState, randAction, state);
				}

				if (env.isTerminalState())
				{
					env.init(new State(new double[]{Math.PI, 0}));
					System.out.println("terminal state...aborting");
					break;
				}
			}
		}

		if (visualize)
			window.close();		
	}
	
	/**
	 * @param args The command-line arguments
	 * @throws Exception The Exception
	 */
	public static void main (String[] args) throws Exception
	{
		new CollectTrainingData();
	}
}