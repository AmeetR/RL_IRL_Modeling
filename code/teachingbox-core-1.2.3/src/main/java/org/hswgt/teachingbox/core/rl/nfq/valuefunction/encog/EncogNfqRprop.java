package org.hswgt.teachingbox.core.rl.nfq.valuefunction.encog;

import java.util.List;

import org.apache.log4j.Logger;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.strategy.StopTrainingStrategy;
import org.encog.ml.train.strategy.end.EndIterationsStrategy;
import org.encog.ml.train.strategy.end.EndTrainingStrategy;
import org.encog.ml.train.strategy.end.SimpleEarlyStoppingStrategy;
import org.encog.neural.networks.training.propagation.resilient.RPROPType;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.nfq.features.InputFeatures;
import org.hswgt.teachingbox.core.rl.nfq.learner.QValue;

import com.google.common.base.Preconditions;

/**
 * Implements an NFQFunction using the Encog library
 * @author tokicm
 *
 */
public class EncogNfqRprop extends EncogNfq {

	private static final long serialVersionUID = -5774988736651612142L;
	
	// Logger
    private final static Logger log4j = Logger.getLogger("EncogNfqRprop");

	public EncogNfqRprop(ActionSet actionSet, InputFeatures features, int[] layers)
	{
		super (actionSet, features, layers);
	}
	

	/**
	 * start neural network training
	 * @param epochs The amount of epochs to train
	 * @param qBatch A List of QValue objects
	 */
	public void trainBatch(int epochs, List<QValue> qBatch) {
		
		this.prepareTrainingData(qBatch);
		this.qTable = qBatch;

		MLDataSet trainingSet = new BasicMLDataSet (inputsTrain, outputsTrain);
		ResilientPropagation netLearner = new ResilientPropagation(network, trainingSet);
		
		netLearner.setRPROPType(RPROPType.iRPROPp);
		netLearner.setThreadCount(threads);	
		netLearner.setBatchSize(this.batchSize);
		
		EndTrainingStrategy stop = new EndIterationsStrategy(epochs);

		// overwrite EndTrainingStrategy in case of early-stopping
		if (this.isEarlyStopping) {
			// create new Encog training and test data set
			MLDataSet testSet = new BasicMLDataSet(inputsTest, outputsTest);
			stop = new SimpleEarlyStoppingStrategy(testSet, 10);
		} 
		netLearner.addStrategy(stop);

		// TRAINING
		log4j.debug("Training " + trainingSet.size() + " data patterns.");
		while (!stop.shouldStop() && netLearner.getIteration() < epochs) {			
			netLearner.iteration();			
		}
		
		log4j.info("performed " + netLearner.getIteration() + " RPROP epochs, ERROR: " + netLearner.getError() + ", strategy=" + stop.getClass().getSimpleName());
		netLearner.finishTraining();	
		if (Double.isNaN(netLearner.getError())) {
			this.debugTrainingData("data/debug.txt");
			System.out.println("Network: " + this.toString());
			throw new RuntimeException ("Network error is NaN. Debugging data to file 'data/debug.txt'");
			
		}
		this.iteration++;
	}
}
