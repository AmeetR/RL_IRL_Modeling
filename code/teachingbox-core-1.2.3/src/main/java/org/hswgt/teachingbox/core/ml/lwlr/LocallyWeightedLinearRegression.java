
package org.hswgt.teachingbox.core.ml.lwlr;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;

import weka.classifiers.functions.LinearRegression;
import weka.classifiers.lazy.LWL;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.KDTree;

/**
 * Memory-based approximation by Locally Weighted Linear Regression (lazy learning). 
 * Uses the WEKA (Univ. of Waikato) (http://www.cs.waikato.ac.nz/ml/weka/) class LWL 
 * (Locally Weighted Learning) with Linear Regression as local approximation, a linear
 * weighting function (by default) and a KDTree for nearest neighbor search. For
 * more information about Locally Weighted Learning, please read
 * <i>C. Atkeson, A. Moore, S. Schaal (1996). Locally Weighted Learning. AI Review...</i>
 *
 * @author Richard Cubek
 * 
 */
public class LocallyWeightedLinearRegression
{
	/** WEKA LWL object */
	protected LWL lwl = new LWL();
	
	/* WEIGHTING KERNEL METHODS FOR LOCALLY WEIGHTED REGRESSION */
	
	/* corresponding constants in LWL are not accessible, already annotated, will be changed.
	 * So we provide them here again. GAUSSIAN and INVERSE weighting from LWL does not work 
	 * locally, only global over the whole data set, also annotated, will maybe be changed in 
	 * near future. So, we only provide the remaining method constants, tests over the global 
	 * data set showed, that there are not too big differences in the results in comparison to 
	 * GAUSSIAN weighting. In tests using local weighting, LINEAR weighting performed very well, 
	 * that's why it will be used by default. */
	
	/** Linear weighting function. */
	public static final int LINEAR = 0;
	/** Epanechnikov weighting function. */
	public static final int EPANECHNIKOV = 1;
	/** Tricube weighting function. */
	public static final int TRICUBE = 2;  
	/** Constant weighting function. */
	public static final int CONSTANT = 5;
	
	/* The Instances (data/training set) */
	protected Instances dataset;

	/**
	 * Constructor.
	 * @param dataset WEKA Instances object.
	 * @throws Exception The Exception
	 */
	public LocallyWeightedLinearRegression(Instances dataset) throws Exception
	{
		// set the method for local regression
		lwl.setClassifier(new LinearRegression());
		// set number of nearest neighbours to be used for local prediction
		lwl.setKNN(10); // 10 by default
		// set weighting kernel method (see comments on constants)
		lwl.setWeightingKernel(LINEAR);
		// set KDTree as nearest neighbour search method
		lwl.setNearestNeighbourSearchAlgorithm(new KDTree());
		// build the classifier
		lwl.buildClassifier(dataset);
		// store instance reference
		this.dataset = dataset;
	}
	
	/**
	 * Set the number of neighbors being considered for local regression, 10 by default.
	 * @param knn The number of neighbors
	 */
	public void setKNearestNeighbours(int knn)
	{
		lwl.setKNN(knn);
	}
	
	/**
	 * Set the weighting kernel being used to determine the weights of the k nearest
	 * neighbours. Possible choices are LINEAR, EPANECHNIKOV, TRICUBE, CONSTANT. The use
	 * of GAUSSIAN or INVERSE (LWL in WEKA) does not work locally (already annotated, will
	 * maybe be changed in near future). Default value: LINEAR (performed very good in tests).
	 * @param kernel The kernel method to use.
	 */
	public void setWeightingKernel(int kernel)
	{
		lwl.setWeightingKernel(kernel);
	}
	
	/**
	 * Compare an arbitrary instances header to this instances header. 
	 * @param dataset The dataset header to compare.
	 * @return Whether the datasets have the same header.
	 */
	public boolean equalDataSetHeader(Instances dataset)
	{
		return this.dataset.equalHeaders(dataset);
	}

	/**
	 * Make a prediction for a query vector (numeric classification).
	 * @param queryVector The input vector to predict the output value from.
	 * @return The predicted value.
	 * @throws Exception The Exception
	 */
	public double predict(DenseDoubleMatrix1D queryVector) throws Exception
	{
		// build instance from queryVector (length: attributes + prediction class)
		Instance instance = new Instance(queryVector.size() + 1);
		
		for (int i = 0; i < queryVector.size(); i++)
		{
			instance.setValue(i, queryVector.get(i));
		}
				
		return predict(instance);
	}

	/**
	 * Make a prediction for a query vector (numeric classification).
	 * @param queryVector The input vector to predict the output value from.
	 * @return The predicted value.
	 * @throws Exception The Exception
	 */
	public double predict(double[] queryVector) throws Exception
	{
		return predict(new DenseDoubleMatrix1D(queryVector));
	}
	
	/**
	 * Make a prediction for a query vector in form of a WEKA instance.
	 * @param instance The WEKA instance to predict the output value from.
	 * @return The predicted value.
	 * @throws Exception The Exception
	 */
	public double predict(Instance instance) throws Exception
	{
		/* refer instance to dataset, does not check if the instance is compatible 
		 * with the dataset (from javadocs), that's why we check it a step later
		 */
		instance.setDataset(dataset);

		if (!dataset.checkInstance(instance))
		{
			throw new Exception("Instance to predict is not compatible with the dataset!");
		}

		// predict the instance
		return lwl.classifyInstance(instance);
	}
}