package org.hswgt.teachingbox.core.rl.policy;



import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.reinforce.EpisodicReinforceLearner;
import org.hswgt.teachingbox.core.rl.reinforce.LocalReinforceLearner;
import org.hswgt.teachingbox.core.rl.reinforce.ReinforceLearner;

/**
 * This class adapts a policy parameter (eg. sigma of VDBE-Softmax) based
 * on the return per episode (REINFORCE, Williams, 1992).
 *  
 * @author tokicm
 *
 */
public class LocalReinforcePolicy implements Policy, ReinforceLearner {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8588259757002490456L;
	private Policy pi;
	LocalReinforceLearner lrl;

	/** 
	 * Constructor
	 * @param pi An arbitrary policy
	 */
	public LocalReinforcePolicy(Policy pi) {			
		this.pi = pi;
	}
	
	public void setReinforceLearner (LocalReinforceLearner lrl) {
		this.lrl = lrl;
	}


	@Override
	public double getScaledMetaParameter(String parameter, double value) {

		
		// return values only once per episode because of cumulative averaging
		if (parameter.equals(EpisodicReinforceLearner.MEAN)) {
			
			if (this.pi.getClass().getName().contains("EpsilonGreedyPolicy") ||  this.pi.getClass().getName().contains("MaxBoltzmannExplorationPolicy")) {
				
				return value;
				
			// return denormalized parameter for vdbe* and softmax
			} else if (this.pi.getClass().getName().contains("SoftmaxActionSelection") || 
					   this.pi.getClass().getName().toLowerCase().contains("vdbe")) {
				
				return Math.exp(value);
				
			}
			
		} else if (parameter.equals (EpisodicReinforceLearner.VARIANCE)) {

			if (this.pi.getClass().getName().contains("EpsilonGreedyPolicy") ||  this.pi.getClass().getName().contains("MaxBoltzmannExplorationPolicy")) {
				
				return value;
				
			} else {
			
				return Math.exp(value);
			}
			
		} else if (parameter.equals (EpisodicReinforceLearner.POLICY_PARAMETER)) {
			
			// return epsilon
			if (this.pi.getClass().getName().contains("EpsilonGreedyPolicy") ||  this.pi.getClass().getName().contains("MaxBoltzmannExplorationPolicy")) {

				return value;
				
			// return denormalized parameter for softmax and vdbe*
			} else if (this.pi.getClass().getName().contains("SoftmaxActionSelection") || 
					   this.pi.getClass().getName().toLowerCase().contains("vdbe")) {
				return Math.exp(value);
			}
			
		} else if (parameter.equals (EpisodicReinforceLearner.REWARD_BASELINE)) {
			return value;
		}

		return 0;
	}


	@Override
	public Action getAction(State s) {		

		// set the policy parameter
		this.setParameter (lrl.getParameter(s,  lrl.PARAMETER_ACTION, lrl.POLICY_PARAMETER));
		
		// return policy action
		return pi.getAction(s);		
	}

	@Override
	public Action getBestAction(State state) {
		return pi.getBestAction(state);
	}

	@Override
	public void setParameter(double parameter) {

		// set new policy parameter
		if (this.pi.getClass().getName().contains("EpsilonGreedyPolicy") || this.pi.getClass().getName().contains("MaxBoltzmannExplorationPolicy")) {
			((EpsilonGreedyPolicy)pi).setEpsilon(parameter);
			
		} else if (this.pi.getClass().getName().contains("SoftmaxActionSelection")) {
			((SoftmaxActionSelection)pi).setTemperature(Math.exp(parameter));

		} else if (this.pi.getClass().getName().toLowerCase().contains("vdbe")) {
			((TabularVDBEPolicy)pi).setSigma(Math.exp(parameter));
		}
	}
	

	@Override
	public double getProbability(State state, Action action) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Policy getPi() {
		return this.pi;
	}
}
