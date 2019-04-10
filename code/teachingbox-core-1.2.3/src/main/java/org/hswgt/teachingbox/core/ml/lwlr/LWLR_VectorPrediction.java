
package org.hswgt.teachingbox.core.ml.lwlr;

import java.util.LinkedList;
import java.util.List;

import org.hswgt.teachingbox.core.rl.datastructures.VectorMapper;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * WEKA does not make vector predictions, as we typically need it for n-dimensional
 * approximations of transition functions for environment models  - to predict a vector 
 * of n elements, it is necessary, to build n classifiers for n sets of data sets (see
 * search results for "vector prediction" in the WEKA mailing-list archive). The data
 * sets (Instances) have all to be of the same ARFF header. This class uses a List of
 * LocallyWeightedLinearRegression objects for predictions. 
 * 
 * @author Richard Cubek
 *
 */
public class LWLR_VectorPrediction
{
	/**
	 * List of LocallyWeightedLinearRegression objects. 
	 */
	protected List<LocallyWeightedLinearRegression> lwlrList = new LinkedList<LocallyWeightedLinearRegression>();	
	/**
	 * VectorMapper for incoming and outgoing vector mappings. 
	 */
	protected VectorMapper queryVectorMapper = null;
	protected VectorMapper outputVectorMapper = null;
	/** Length of query vectors (set with first added data set) */
	protected int queryVectorLength = 0;
	/** weighting kernel */
	protected int weightingKernel = LocallyWeightedLinearRegression.LINEAR;
	/** k nearest neighbors */
	protected int knn = 10;
	

	/**
	 * Set a VectorMapper to map incoming vectors to vectors containing variables relevant
	 * for Locally Weighted Regression (regarding this special problem to solve). 
	 * @param vectorMapper The VectorMapper to be used.
	 */
	public void setInputVectorMapper(VectorMapper vectorMapper)
	{
		this.queryVectorMapper = vectorMapper;
	}
	
	/**
	 * Set a VectorMapper to map predicted vectors to outgoing vectors, i.e. to represent the
	 * same model as the original incoming vectors. 
	 * @param vectorMapper The VectorMapper to be used.
	 */
	public void setOutputVectorMapper(VectorMapper vectorMapper)
	{
		this.outputVectorMapper = vectorMapper;
	}
	
	/**
	 * Add an ARFF or CSV data file name, from where to read the data for one dimension
	 * of prediction. This method will then automatically build the Instances and a
	 * corresponding LocallyWeightedLinearRegression Classifier. The added data have
	 * all to be of the same ARFF header format. In case of using CSV files, the first
	 * file has to be composed of the variable names (i.e.: "x, f_x").
	 * @param file Path to ARFF or CSV data file.
	 * @throws Exception  The Exception
	 */
	public void addDatasetFile(String file) throws Exception
	{
		/* CREATE INSTANCES FROM THIS FILE AND ADD A CORRESPONDING LWLR CLASSIFIER */
		
		// standard procedure to create Instances (data set) from file
		DataSource dataSource = new DataSource(file);
		Instances dataset = dataSource.getDataSet();
		dataset.setClassIndex(dataset.numAttributes() - 1);
		
		// add LWLR classifier to list (Instances have all to be of same header)
		if (lwlrList.size() > 0)
		{
			if (lwlrList.get(0).equalDataSetHeader(dataset))
			{
				LocallyWeightedLinearRegression lwlr = new LocallyWeightedLinearRegression(dataset);
				lwlr.setWeightingKernel(weightingKernel);
				lwlr.setKNearestNeighbours(knn);
				lwlrList.add(lwlr);			
			}
			else
			{
				throw new Exception("Adding Instances of different headers.");
			}
		}
		else
		{
			// first LWLR classifier
			lwlrList.add(new LocallyWeightedLinearRegression(dataset));
			queryVectorLength = dataset.numAttributes() - 1; // last one is the class attribute
		}		
	}

	/**
	 * Make a prediction for a query vector (numeric classification of vector variables).
	 * @param queryVector The input vector to predict the output vector from.
	 * @return The predicted vector.
	 * @throws Exception The Exception
	 */
	public DenseDoubleMatrix1D predict(DenseDoubleMatrix1D queryVector) throws Exception
	{
		// map query vector if necessary
		if (queryVectorMapper != null)
		{
			queryVector = queryVectorMapper.getMappedVector(queryVector);
		}

		if (queryVector.size() != queryVectorLength)
		{
			throw new Exception("Query vector has different length than prediction is provided for.");
		}
		
		// each of the LWLR classifiers predicts a single vector variable  

		DenseDoubleMatrix1D outputVector = new DenseDoubleMatrix1D(lwlrList.size());
		
		for (int i = 0; i < lwlrList.size(); i++)
		{
			outputVector.set(i, lwlrList.get(i).predict(queryVector));
		}
		
		// map output vector if necessary
		if (outputVectorMapper != null)
		{
			outputVector = outputVectorMapper.getMappedVector(outputVector);
		}

		return outputVector;
	}
	
	/**
	 * Set the number of neighbors being considered for local regression, 10 by default.
	 * @param knn The number of neighbors
	 */
	public void setKNearestNeighbours(int knn)
	{
		// has to be set for all already in the list...
		for (int i = 0; i < lwlrList.size(); i++)
		{
			lwlrList.get(i).setKNearestNeighbours(knn);
		}
		// ...and for those added later!
		this.knn = knn;
	}
	
	/**
	 * Set the weighting kernel being used to determine the weights of the k nearest
	 * neighbours. Possible choices are LINEAR, EPANECHNIKOV, TRICUBE, CONSTANT. The use
	 * of GAUSSIAN or INVERSE (LWL in WEKA) does not work locally (already annotated, will
	 * be changed in near future). Default value: LINEAR (performed very good in tests).
	 * @param kernel The kernel method to use.
	 */
	public void setWeightingKernel(int kernel)
	{
		// has to be set for all already in the list...
		for (int i = 0; i < lwlrList.size(); i++)
		{		
			lwlrList.get(i).setWeightingKernel(kernel);
		}
		// ...and for those added later!
		this.weightingKernel = kernel;
	}	
}
