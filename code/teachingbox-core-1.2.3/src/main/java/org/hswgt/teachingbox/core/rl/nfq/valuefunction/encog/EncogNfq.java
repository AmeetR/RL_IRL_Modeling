package org.hswgt.teachingbox.core.rl.nfq.valuefunction.encog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.EpisodicSaver;
import org.hswgt.teachingbox.core.rl.nfq.features.InputFeatures;
import org.hswgt.teachingbox.core.rl.nfq.learner.QValue;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.Nfq;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.DataTransformer;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.transformer.LogisticTransformer;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.transformer.ScaleTransformer;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.transformer.TanhTransformer;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.transformer.ZTransformer;
import org.hswgt.teachingbox.core.rl.plot.Plotter;

import com.google.common.base.Preconditions;

public abstract class EncogNfq extends Nfq implements Plotter {

    public final static double MIN_WEIGHT = -0.1;
	public final static double MAX_WEIGHT = 0.1;
	
    private final static Logger log4j = Logger.getLogger("EncogNfq");

	/**
	 * 
	 */
	private static final long serialVersionUID = 4516689336164348580L;
	protected int threads = Runtime.getRuntime().availableProcessors();
	

    // Memory of inputs and outputs for neural network training.
	// Important: variables are declared as "transient", in order to skip 
	// exporting data when serializing the object.
    protected transient double inputs[][] = null; 
    protected transient double outputs[][] = null;
    protected transient double inputsTrain[][] = null; 
    protected transient double outputsTrain[][] = null;
    protected transient double inputsTest[][] = null; 
    protected transient double outputsTest[][] = null;
    protected transient List<QValue> qTable = null;
    
	protected BasicNetwork network;	
	protected TrainingContinuation trainContinuation = null;
    
    private DataTransformer inputTransformation; 
    private DataTransformer outputTransformation; 
   
	// early stopping
	protected boolean isEarlyStopping = false;
	protected int earlyStoppingKarrenz = 10; 

	protected double testSetSize = 0.0;
	private long splitRandSeed = 0;

	// batch size
	protected int batchSize = 0;

	public EncogNfq(ActionSet actionSet, InputFeatures features, int layers[]) {
		super(actionSet, features);
	
		this.network = this.createNetwork(layers, MIN_WEIGHT, MAX_WEIGHT);
		
		inputTransformation = new DataTransformer(features.getNumInputFeatures());
		inputTransformation.addTransformer(new ZTransformer());
		
		outputTransformation = new DataTransformer(1);
		//outputTransformation.addTransformer(new ScaleTransformer(InputFeatures.MIN_NEURON_ACT, InputFeatures.MAX_NEURON_ACT));
		//outputTransformation.addTransformer(new ZTransformer());
		//outputTransformation.addTransformer(new TanhTransformer());
		//outputTransformation.addTransformer(new LogisticTransformer());
		//outputTransformation.addTransformer(new ClipTransformer(InputFeatures.MIN_NEURON_ACT, InputFeatures.MAX_NEURON_ACT));
		outputTransformation.addTransformer(new ScaleTransformer(InputFeatures.MIN_NEURON_ACT, InputFeatures.MAX_NEURON_ACT));
		
		
		log4j.debug(network.toString());
	}
	
	public BasicNetwork getEncogNetwork () {
		return network;
	}

	/**
	 * returns a String representation of the weights
	 */
	public String toString() {
		Preconditions.checkNotNull(this.network, "Network not yet configured");		
		
		String out = "";
		
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
		DecimalFormat df = (DecimalFormat)nf;

		
		for (int l=0; l<this.network.getLayerCount()-1; l++) {
			int inputs = this.network.getLayerNeuronCount(l);
			int outputs = this.network.getLayerNeuronCount(l+1);
			
			out += "Connector " + l + " (" + inputs + "x" + outputs + "): [\n";

			float max = Float.NEGATIVE_INFINITY;
			float min = Float.POSITIVE_INFINITY;
			float mean = 0;
			Float variance = new Float(0);
			float m2 = 0;
			int idx = 0;
			
			for (int i=0; i<inputs; i++) {				
				for (int o=0; o<outputs; o++) {

					float weight = (float) this.network.getWeight(l, i, o);
					out += " " + df.format(weight);
					if (this.network.getWeight(l, i, o) > max) {
						max = weight; 
					} 
					if (this.network.getWeight(l, i, o) < min) {
						min = weight;
					}
					
					// update mean/std
					idx += 1;
					float delta = weight - mean; 
					mean += delta / idx;
					m2 += delta * (weight-mean);
				}
				out += "\n";
			}
			
			if (idx <2) {
				variance=Float.NaN;
			} else {
				variance=m2 / (idx-1);
			}
			out += "] weightBounds=[" + df.format(min) + ", " + df.format(max) + "] mean=" + df.format(mean) + ", std=" + df.format(Math.sqrt(variance)) + "\n";
		}
		
		return this.network.toString() + "\n" + out;
	}
	
	@Override
	public void randomizeWeights(long seed) {
		this.randGenerator.setSeed(seed);
		double rand = 0;
		
		// INITIALIZE WEIGHTS
		for (int k=0; k<network.getLayerCount()-1; k++) {
			for (int i=0; i<network.getLayerNeuronCount(k); i++) {
				for (int j=0; j<network.getLayerNeuronCount(k+1); j++) {
					rand = MIN_WEIGHT + (randGenerator.nextDouble() * (MAX_WEIGHT-MIN_WEIGHT));
					network.setWeight(k, i, j, rand);
				}
			}
		}
	}
	
	/**
	 * set the amount of learning threads for multi-core machines (default: 1)
	 * @param threads The amount of threads for parallelizing gradient computation
	 */
	public void setThreadCount (int threads) {
		this.threads = threads;
	}
	
	/**
	 * Returns the amount of threads used for gradient computation
	 * @return the amount of threads used for gradient computation
	 */
	public int getThreadCount () {
		return this.threads;
	}
	
	
	/**
	 * Sets the batch size (0 means batch training with all training data)
	 * @param batchSize The batch size
	 */
	public void setBatchSize (int batchSize) {
		Preconditions.checkArgument(batchSize >= 0, "batchSize must be >= 0, but is %s", batchSize);
		this.batchSize = batchSize;
	}
	
	/**
	 * Returns the batch size (0 means batch training with all training data)
	 * @return the batch size
	 */
	public int getBatchSize() {
		return this.batchSize;
	}
	
	/**
	 * Debugs inputs and outputs of the last MLP training to the given text file
	 * @param filename The filename 
	 */
	public void debugTrainingData(String filename) {
		
		Preconditions.checkNotNull(inputs, "no inputs available"); 
		Preconditions.checkNotNull(outputs, "no outputs available"); 

		File fdata = new File (filename + ".trainData");		
		File fnet = new File (filename + ".network");		
		File fqBatch = new File (filename + ".qBatch");		
		
		try {
			FileWriter fw_data = new FileWriter (fdata, false);
			
			// training data
			int inputColumns = inputs[0].length; 
			int outputColumns = outputs[0].length; 
			
			for (int line=0; line<inputs.length; line++) {
				
				fw_data.write("" + line);
				for (int i=0; i<inputColumns; i++) {
					fw_data.write(" " + inputs[line][i]);
				}
				fw_data.write(" | ");
				for (int o=0; o<outputColumns; o++) {
					fw_data.write(" " + outputs[line][o]);
				}
				fw_data.write("\n");
			}
			
			fw_data.close();
			
			// network			
			FileWriter fw_net = new FileWriter (fnet, false);
			fw_net.write(this.toString());
			fw_net.close();
			
			FileWriter fw_qbatch = new FileWriter (fqBatch, false);
			for (QValue q : qTable) {
				fw_qbatch.write(   "s=" + q.getState().toString() + 
								" | a=" + q.getAction().toString() + 
								" | r=" + q.getReward() + 
								" | sn=" + q.getNextState().toString() + 
								" | Q(s,a)=" + q.getQ() + "\n");
			}
			fw_qbatch.close();

		} catch (IOException e) {
			e.printStackTrace();
		} 		
	}
	
	@Override
	public void plot() {
		this.debugTrainingData("data/q-function-iteration-" + this.iteration + ".txt");
	}
	
	
    /**
     * initializes an Encog network
     * @param layers An array containing how many neurons are on each layer
     * @param minWeight The minimum weight
     * @param maxWeight The maximum weight
     * @return The BasicNetwork object
     */
	public BasicNetwork createNetwork(int[] layers, double minWeight, double maxWeight) {
		
		 // create neural network
        BasicNetwork network = new BasicNetwork();

        String data = "";
        for (int i=0; i< layers.length; i++) {
        	
        	// input layer
        	if (i==0) {
        		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, layers[i]));        		
        	
        	// output layer
        	} else if (i == (layers.length-1)) {
        		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, layers[i]));
        		//network.addLayer(new BasicLayer(new ActivationLinear(), false, layers[i]));
        		
        	// hidden layers
            } else {
        		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, layers[i]));        		
        	}
        	
        	
    		data += (layers[i] + ", ");
        }
        log4j.debug("Generating network with layers: " + data);
        network.getStructure().finalizeStructure();
		//network.reset(0);
		
		
		// INITIALIZE WEIGHTS
		for (int k=0; k<network.getLayerCount()-1; k++) {
			for (int i=0; i<network.getLayerNeuronCount(k); i++) {
				for (int j=0; j<network.getLayerNeuronCount(k+1); j++) {
					double rand = minWeight + (randGenerator.nextDouble() * (maxWeight - minWeight));
					network.setWeight(k, i, j, rand);
				}
			}
		}
		
		//System.exit(-1);
		return network;
	}



	/**
	 * @return the testSetSize
	 */
	public double getTestSetSize() {
		return testSetSize;
	}
	/**
	 * This function sets the test set size for an early stopping strategy (training is stopped, when 
	 * the error of the test set begins to raise.
	 * 
	 * @param testSetSizePercent The size of the test set in percent [0, 100)
	 * @param checkFrequency number of episodes in which the the error is expected to be better than before (karrenz episodes)
 
	 */
	public void setEarlyStopping(int testSetSizePercent, int checkFrequency) {
		Preconditions.checkArgument(testSetSizePercent >= 0 && testSetSizePercent < 100, "The size of the test set must be from the interval [0, 100). ");
		this.testSetSize = testSetSizePercent;
		this.splitRandSeed = randGenerator.nextLong();
		
		if (testSetSizePercent == 0) {
			this.isEarlyStopping = false;
		} else {
			this.isEarlyStopping = true; 			
		}
	}
	
	/**
	 * This function clips the weights of the neural network to specified bounds
	 * @param minWeight The minimum weight 
	 * @param maxWeight The maximum weight
	 */
	protected boolean clipWeights(double minWeight, double maxWeight) {
		boolean clipped = false;
		for (int k=0; k<network.getLayerCount()-1; k++) {
			for (int i=0; i<network.getLayerNeuronCount(k); i++) {
				for (int j=0; j<network.getLayerNeuronCount(k+1); j++) {
					double weight = network.getWeight(k, i, j);
					if (weight > maxWeight) {
						network.setWeight(k,  i,  j,  maxWeight);
						clipped = true;
					}					
					if (weight < minWeight) {
						network.setWeight(k,  i,  j,  minWeight);
						clipped = true;
					}					
				}
			}
		}
		return clipped;
	}
	
	/**
	 * This function prepares the training and test data based on the current qTable. 
	 * If a "early stopping" strategy is configured, the corresponding train and test data arrays
	 * are filled. 
	 */
	protected void prepareTrainingData (List<QValue> qTable) {
		
		log4j.debug("starting transformation of " + qTable.size() + " data rows");
		// allocate memory on first run
		if (inputs == null || qTable.size() != inputs.length) {		
			log4j.debug("creating table with " + qTable.size() + " rows.");
			inputs = new double[qTable.size()][features.getNumInputFeatures()];
			outputs = new double[qTable.size()][1];
		}
		
		// build output table for training
		int qRow=0; 
		for (QValue q : qTable) {
			inputs[qRow] = q.getInputFeatures(); 
			outputs[qRow] = new double[]{q.getQ()};
			qRow++;
		}
				
		//log4j.debug("computing transformation parameters for " + qRow + " data rows");
		inputTransformation.computeParameters(inputs);
		inputTransformation.transformDataArray(inputs);

		outputTransformation.computeParameters(outputs);
		outputTransformation.transformDataArray(outputs);
		
		if (this.isEarlyStopping) {

			// permute data		
			Random splitRandGen = new Random (this.splitRandSeed);
			for (int i=0; i<inputs.length; i++) {
				int randId = splitRandGen.nextInt(inputs.length);			
				double inBuf[] = inputs[i];
				double outBuf[] = outputs[i];			
				inputs[i] = inputs[randId];
				outputs[i] = outputs[randId];
				inputs[randId] = inBuf;
				outputs[randId] = outBuf; 			
			}
			
			// generate training and test sets
			int maxTrainDataRow = (int) (inputs.length * ((100.-this.testSetSize)/100.));
			
			if (inputsTrain == null || inputsTrain.length != maxTrainDataRow) {
				inputsTrain = new double[maxTrainDataRow][inputs[0].length];
				outputsTrain = new double[maxTrainDataRow][outputs[0].length];				
			}
			if (inputsTest == null || inputsTest.length != (inputs.length - maxTrainDataRow)) {
				inputsTest = new double[inputs.length - maxTrainDataRow][inputs[0].length];
				outputsTest = new double[outputs.length - maxTrainDataRow][outputs[0].length];
			}
			
			for (int row=0; row< maxTrainDataRow; row++) {
				inputsTrain[row] = inputs[row];
				outputsTrain[row] = outputs[row];
			}
			for (int row=maxTrainDataRow; row< inputs.length; row++) {
				inputsTest[row-maxTrainDataRow] = inputs[row];
				outputsTest[row-maxTrainDataRow] = outputs[row];
			}
			log4j.debug("Training data from 0.." + (maxTrainDataRow-1) + ", test data from " + maxTrainDataRow + ".." + inputs.length);
		} else {
			inputsTrain = inputs;
			outputsTrain = outputs;
			inputsTest = inputs;
			outputsTest = outputs;
		}
	}
	
	@Override
	public double getValue(State state, Action action) {
		
		double inputFeatures[] = new double[features.getNumInputFeatures()]; //features.getInputFeatures(state, action);
		features.getInputFeatures(state,  action, inputFeatures);
		inputTransformation.transformDataVector(inputFeatures);
		
		double netQ[] = network.compute(new BasicMLData(inputFeatures)).getData();
		
		// backtransformation
		outputTransformation.backTransformDataVector(netQ);

		return netQ[0];
	}
}
