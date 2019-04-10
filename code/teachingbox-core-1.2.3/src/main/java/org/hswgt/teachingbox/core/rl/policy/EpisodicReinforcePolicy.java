package org.hswgt.teachingbox.core.rl.policy;

import java.io.Serializable;

import org.hswgt.teachingbox.core.rl.reinforce.EpisodicReinforceLearner;
import org.hswgt.teachingbox.core.rl.reinforce.ReinforceLearner;

/**
 * This class adapts a policy parameter (eg. sigma of VDBE-Softmax) based
 * on the return per episode (REINFORCE, Williams, 1992).
 * 
 * This paper is an implementation of: 
 * M. Tokic and G. Palm. Adaptive exploration using stochastic neurons. In 
 * A. Villa, W. Duch, P. Erdi, F. Masulli, and G. Palm, editors, Artificial 
 * Neural Networks and Machine Learning - ICANN 2012, volume 7553 of 
 * Lecture Notes in Computer Science, pages 42-49. Springer Berlin / 
 * Heidelberg, 2012.
 *  
 * @author tokicm
 *
 */
public class EpisodicReinforcePolicy implements ReinforceLearner, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5077722337763576018L;
	protected Policy pi;

	/** 
	 * Constructor
	 * @param pi An arbitrary policy
	 */
	public EpisodicReinforcePolicy(Policy pi) {
		this.pi = pi;
	}
	
	@Override
	public void setParameter (double policyParameter) {
		// set new global policy parameter
		if (this.pi.getClass().getName().contains("EpsilonGreedyPolicy") || this.pi.getClass().getName().contains("MaxBoltzmannExplorationPolicy")) {			
			((EpsilonGreedyPolicy)pi).setEpsilon(policyParameter);
			//System.out.println ("setting epsilon to " + policyParameter);
			
		} else if (this.pi.getClass().getName().contains("SoftmaxActionSelection")) {
			((SoftmaxActionSelection)pi).setTemperature(Math.exp(policyParameter));
			//System.out.println ("setting tau to " + Math.exp(policyParameter));
			
		} else if (this.pi.getClass().getName().toLowerCase().contains("vdbe")) {
			((TabularVDBEPolicy)pi).setSigma(Math.exp(policyParameter));
			//System.out.println ("setting sigma to " + Math.exp(policyParameter));
		}
	}

	@Override
	public double getScaledMetaParameter(String parameter, double value) {

		//System.out.println ("returning parameter " + parameter + " in episode" + episodeCounter + ", step=" + stepCounter);
		if (parameter.equals(EpisodicReinforceLearner.MEAN)) {
			
			if (this.pi.getClass().getName().contains("EpsilonGreedyPolicy") ||  this.pi.getClass().getName().contains("MaxBoltzmannExplorationPolicy")) {
				return value;
				
			// return denormalized parameter for vdbe* and softmax
			} else if (this.pi.getClass().getName().contains("SoftmaxActionSelection") || 
					   this.pi.getClass().getName().toLowerCase().contains("vdbe")) {
				return Math.exp(value);				
			}
			
		} else if (parameter.equals (EpisodicReinforceLearner.VARIANCE)) {

			//return this.variance;
			
			if (this.pi.getClass().getName().contains("EpsilonGreedyPolicy") ||  this.pi.getClass().getName().contains("MaxBoltzmannExplorationPolicy")) {

				return value;
				
				
			} else {
				return Math.exp(value);
			}
			
		} else if (parameter.equals (EpisodicReinforceLearner.POLICY_PARAMETER)) {
			
			if (this.pi.getClass().getName().contains("EpsilonGreedyPolicy") ||  this.pi.getClass().getName().contains("MaxBoltzmannExplorationPolicy")) {
				return value;
				
			} else if (this.pi.getClass().getName().contains("SoftmaxActionSelection") || 
					   this.pi.getClass().getName().toLowerCase().contains("vdbe")) {
				return Math.exp(value);
			}
			
		} else if (parameter.equals (EpisodicReinforceLearner.REWARD_BASELINE)) {
			return value;
		}

		return 0;
	}

	/**
	 * @return the pi
	 */
	public Policy getPi() {
		return pi;
	}

	/**
	 * @param pi the pi to set
	 */
	public void setPi(Policy pi) {
		this.pi = pi;
	}
}
