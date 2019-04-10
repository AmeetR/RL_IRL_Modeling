package org.hswgt.teachingbox.core.rl.nfq.valuefunction.encog;

import java.util.List;

import org.apache.log4j.Logger;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.strategy.end.EndIterationsStrategy;
import org.encog.ml.train.strategy.end.EndTrainingStrategy;
import org.encog.ml.train.strategy.end.SimpleEarlyStoppingStrategy;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.nfq.features.InputFeatures;
import org.hswgt.teachingbox.core.rl.nfq.learner.QValue;

import com.google.common.base.Preconditions;

/**
 * Implements an NFQFunction for the Encog-Library
 * @author tokicm
 *
 */
public class EncogNfqBackprop extends EncogNfq {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4740691732686298668L;
    
	// Logger
    private final static Logger log4j = Logger.getLogger("EncogNfqBackprop");
    	
	// learning rate for gradient
	protected double eta = 0.01;
	// learning rate decay (parameter <= 1)
	protected double etaDecay = 1.0; 
	// weighting of momentum term
	protected double alpha = 0.01;

	public EncogNfqBackprop(ActionSet actionSet, InputFeatures features, int[] layers) {
		super (actionSet, features, layers);
	}
	
	/**
	 * sets the learning rate 
	 * @param eta The learning rate 
	 */
	public void setLearningRate(double eta) {
		this.eta = eta;
	}
	
	/**
	 * Sets the decay factor for the learning rate (1.0 means no decay) 
	 * @param etaDecay The decay rate for the learning rate
	 */
	public void setLearningRateDecay (double etaDecay) {
		Preconditions.checkArgument(etaDecay >0 && etaDecay <= 1.0, "etaDecay must be > 0 and <= 1, but was %s", etaDecay);
		this.etaDecay = etaDecay;
	}
	
	/**
	 * sets the learning rate for the momentum term
	 * @param alpha The learning rate for the momentum term
	 */
	public void setMomentum (double alpha) {
		this.alpha = alpha;
	}
	
	/**
	 * start neural network training
	 * @param epochs The amount of epochs to train
	*/
	@Override
	public void trainBatch(int epochs, List<QValue> qBatch) {
		
		log4j.debug("Preparing training data");
		this.prepareTrainingData(qBatch);
		this.qTable = qBatch;
		
		log4j.debug("Creating BasicMLDataSet");
		MLDataSet trainingSet = new BasicMLDataSet (inputsTrain, outputsTrain);
		
		// train the neural network
		Backpropagation netLearner = new Backpropagation(network, trainingSet);
		netLearner.setLearningRate(eta);
		netLearner.setMomentum(alpha);
		netLearner.setThreadCount(threads);

		EndTrainingStrategy stop = new EndIterationsStrategy(epochs);

		if (this.isEarlyStopping) {
			// create new Encog training and test data set
			MLDataSet testSet = new BasicMLDataSet(inputsTest, outputsTest);
			stop = new SimpleEarlyStoppingStrategy(testSet, earlyStoppingKarrenz);
		}
		netLearner.addStrategy(stop);
		netLearner.setBatchSize(this.batchSize);
		
		// TRAINING
		log4j.debug("Training " + trainingSet.size() + " data patterns. Threads: " + this.threads);
		while (!stop.shouldStop() && netLearner.getIteration() < epochs) {
			netLearner.iteration();
		}

		log4j.info("performed " + netLearner.getIteration() + " Backprop epochs, ERROR: " + netLearner.getError());
		netLearner.finishTraining();
		this.iteration++;
		
		this.eta = this.eta * this.etaDecay;
		log4j.debug("Performing learning rate decay. New eta=" + this.eta);
	}
	
	/**
	 * set the amount of learning threads for multi-core machines (default: 1)
	 * @param threads The amount of epochs for parallelizing gradient computation in batch mode
	 */
	public void setThreadCount (int threads) {
		this.threads = threads; 
		log4j.info("Setting threadCount=" + threads);
	}
}
