package org.hswgt.teachingbox.core.rl.reinforce;

import java.io.Serializable;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.ParameterObserver;
import org.hswgt.teachingbox.core.rl.learner.Learner;
import org.hswgt.teachingbox.core.rl.tabular.HashQFunction;

import cern.jet.random.Normal;
import cern.jet.random.Uniform;

/**
 * This class provides abstact features for REINFORCE adaptation of a Gaussian kernel.  
 * It implements the 
 * @author Michel Tokic
 *
 */
abstract public class BasicReinforceGaussianLearner implements ParameterObserver, Learner, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1954913224270027802L;
	
	// the action for the function approximator
	public static Action PARAMETER_ACTION = new Action(new double[]{0});
	
	protected HashQFunction meanFunction;
	protected HashQFunction sigmaFunction;
	protected HashQFunction baselineFunction;
	protected HashQFunction parameterFunction;
	//protected State currentState;
	
	protected double minMean;
	protected double maxMean;
	protected double minSD;
	protected double maxSD;
	protected double alpha;	

	protected ReinforceLearner rl = null;
	
	public final static String MEAN = "mean";
	public final static String VARIANCE = "variance";
	public final static String POLICY_PARAMETER = "policyParameter";
	public final static String REWARD_BASELINE = "rewardBaseline";
		
	
	// The reinforce adaptation mode
	public enum KERNEL {GAUSSIAN, BERNOULLI}
	private KERNEL kernel = KERNEL.GAUSSIAN;


	/**
	 * The constructor for using a GAUSSIAN kernel
	 * @param rl The @ReinforceLearner object (provides evaluation data)
	 * @param initMean initial mean
	 * @param minMean minimum mean
	 * @param maxMean maximum mean
	 * @param initSD initial standard deviation
	 * @param minSD minimum standard deviation
	 * @param maxSD maximum standard deviation
	 * @param alpha learning rate
	 */
	public BasicReinforceGaussianLearner(ReinforceLearner rl,
			double initMean, double minMean, double maxMean, 
			double initSD, 
			double minSD, double maxSD, 
			double alpha) {
		
		this.setGaussianAdaptation(rl, initMean, minMean, maxMean, initSD, minSD, maxSD, alpha);
	}
	
	/**
	 * sets the Gaussian adaptation
	 * @param rl The @ReinforceLearner object (provides evaluation data)
	 * @param initMean initial mean
	 * @param minMean minimum mean
	 * @param maxMean maximum mean
	 * @param initSD initial standard deviation
	 * @param minSD minimum standard deviation
	 * @param maxSD maximum standard deviation
	 * @param alpha learning rate
	 */
	public void setGaussianAdaptation (ReinforceLearner rl,
			double initMean, double minMean, double maxMean, 
			double initSD, 
			double minSD, double maxSD, 
			double alpha) {
		
		this.rl = rl;
		this.alpha = alpha;
		
		meanFunction = new HashQFunction(initMean);
		sigmaFunction = new HashQFunction (initSD);
		parameterFunction = new HashQFunction (initMean);
		baselineFunction = new HashQFunction (0);
		
		this.rl = rl;
		this.minMean = minMean;
		this.maxMean = maxMean;
		this.minSD= minSD;
		this.maxSD= maxSD;
		this.kernel = KERNEL.GAUSSIAN;
		
	}
	
	
	/**
	 * The constructor for using a BERNOULLI kernel
	 * @param rl The @ReinforceLearner object (provides evaluation data)
	 * @param initMean the initial parameter
	 * @param alpha learning rate
	 */
	public BasicReinforceGaussianLearner(ReinforceLearner rl,
			double initMean, double alpha) {

		this.setBernoulliAdaptation(rl, initMean, alpha);
		
	}
	
	/**
	 * initialize Bernoulli adaptation
	 * @param rl The @ReinforceLearner object (provides evaluation data)
	 * @param initMean The initial mean
	 * @param alpha The step-size parameter
	 */
	public void setBernoulliAdaptation(ReinforceLearner rl,
			double initMean, double alpha) {
		
		this.rl = rl;
		this.alpha = alpha;
		this.minMean = 0.01;
		this.maxMean = 0.99;
		
		meanFunction = new HashQFunction(initMean);
		parameterFunction = new HashQFunction (0);
		baselineFunction = new HashQFunction (0);
		
		this.rl = rl;
		this.kernel = KERNEL.BERNOULLI;
	}
	
	
	/** 
	 * This function recomputes the mean and variance based on rho. 
	 * @param rho The observation (reward) to adapt to in "state
	 * @param state The @State
	 */
	protected void updateREINFORCE(double rho, State state) {
		
		// update mue and sigma using alpha=alpha*sigma*sigma (according to williams, 1992)
		double mean = meanFunction.getValue(state, PARAMETER_ACTION);
		double sigma = sigmaFunction.getValue(state, PARAMETER_ACTION);	
		// baseline is maximum q-value in state s
		double rewardBaseline = baselineFunction.getValue(state, PARAMETER_ACTION);
		double parameter = parameterFunction.getValue(state, PARAMETER_ACTION);

		// REINFORCE ADAPTION OF A GAUSSIAN KERNEL		
		if (this.kernel == KERNEL.GAUSSIAN) {
			double sigmaSquare = Math.pow(sigma,2);
			double paramError = parameter - mean;
			
			
			double deltaMue = alpha * (rho - rewardBaseline) * paramError;
			mean = mean+deltaMue;
			double deltaSigma = alpha * (rho - rewardBaseline) * 
									(Math.pow(paramError, 2) - sigmaSquare) / 
									sigma;	
			sigma = sigma + deltaSigma; 
			
			// Mean correction		
			if (mean < minMean) {
				mean = minMean;
			} else if (Double.isNaN(mean)) {
				mean = Double.MAX_VALUE;		
			}			
			if (mean> maxMean) {
				mean = maxMean;
			}
	
			// SD correction
			if (sigma < minSD) {
				sigma = minSD;
			} else if (Double.isNaN(sigma)) {
				sigma = Double.MAX_VALUE;
			}			
			if (sigma > maxSD) {
				sigma = maxSD;
			}
	
			// draw new parameter according to Normal(mue, variance)
			parameter = Normal.staticNextDouble(mean, sigma); 
			if (parameter < minMean) {
				parameter = minMean;
			} else if (parameter > maxMean) {
				parameter = maxMean;
			}
			
		
		// REINFORCE ADAPTATION OF A BERNOULLI KERNEL 
		} else if (this.kernel == KERNEL.BERNOULLI) {
			System.out.println ("BERNOULLI ADAPTATION");
			double paramError = (mean - parameter) / (mean *(1-mean));
			mean = mean + (alpha * (rho - rewardBaseline) * paramError);
			
			if (mean < minMean) {
				mean = minMean;
			} else if (Double.isNaN(mean)) {
				mean = Double.MAX_VALUE;		
			}			
			if (mean> maxMean) {
				mean = maxMean;
			}
			
			// draw new parameter 
			parameter = Uniform.staticNextDoubleFromTo(0, 1) < mean ? 0 : 1;	
		}
		
		
		if (Double.isNaN(parameter)) {
			System.out.println ("policyParameter isNaN");
			System.exit(-1);
		} else if (Double.isInfinite(parameter)) {
			System.out.println ("policyParameter isInfinite");
			System.exit(-1);
		}
		
		// baseline update
		rewardBaseline = (alpha * rho) + ((1-alpha) * rewardBaseline);
		
		// memorize new values
		sigmaFunction.setValue(state, PARAMETER_ACTION, sigma);
		meanFunction.setValue(state, PARAMETER_ACTION, mean);
		baselineFunction.setValue(state, PARAMETER_ACTION, rewardBaseline);
		parameterFunction.setValue(state, PARAMETER_ACTION, parameter);
	}

	/**
	 * Returns the minimum mean
	 * @return the minMean
	 */
	public double getMinMean() {
		return minMean;
	}

	/**
	 * Sets the minimum mean
	 * @param minMean the minMean to set
	 */
	public void setMinMean(double minMean) {
		this.minMean = minMean;
	}

	/**
	 * Returns the maximum mean
	 * @return the maxMean
	 */
	public double getMaxMean() {
		return maxMean;
	}

	/**
	 * Sets the maximum mean
	 * @param maxMean the maxMean to set
	 */
	public void setMaxMean(double maxMean) {
		this.maxMean = maxMean;
	}
}
