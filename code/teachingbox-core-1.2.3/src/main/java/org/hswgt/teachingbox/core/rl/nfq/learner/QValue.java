package org.hswgt.teachingbox.core.rl.nfq.learner;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.env.TransitionSampleSARS;

import com.google.common.base.Preconditions;

public class QValue extends TransitionSampleSARS {

	private static final long serialVersionUID = 7394510514187031244L;
	private double q = 0;
	private double inputFeatures[] = null;
    private final static Logger log4j = Logger.getLogger("QValue");


	/**
	 * @return the inputFeatures
	 */
	public double[] getInputFeatures() {
		//Preconditions.checkNotNull(inputFeatures, "inputFeatures is null: " + this.toString());
		return inputFeatures.clone();
	}

	/**
	 * @param inputFeatures the inputFeatures to set
	 */
	public void setInputFeatures(double[] inputFeatures) {
		this.inputFeatures = inputFeatures.clone();
		//log4j.info("InputFeatures=" + ArrayUtils.toString(inputFeatures));
	}

	public QValue(TransitionSampleSARS sars) {
		super(sars.getState(), sars.getAction(), sars.getNextState(), sars.getReward());
	}

	public QValue(TransitionSampleSARS sars, double qvalue) {
		super(sars.getState(), sars.getAction(), sars.getNextState(), sars.getReward());
		this.q = qvalue;
	}

	
	public QValue(State state, Action action, State nextState, double r) {
		super(state, action, nextState, r);
	}
	
	public QValue(State state, Action action, State nextState, double r, double qvalue) {
		super(state, action, nextState, r);
		this.q = qvalue;
	}
	
	public double getQ() {
		return this.q;
	}
	
	public void setQ(double newQ) {
		this.q = newQ;
	}
	
	public String toString() {
		return 	"s=" + this.state.toString() + ", a=" + this.action.toString() + 
				"sn=" + this.nextState.toString() + ", r=" + this.reward + ", Q(s,a)=" + this.q + ", inputFeatures=" + this.inputFeatures;
	}
}
