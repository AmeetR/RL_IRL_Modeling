package org.hswgt.teachingbox.core.rl.policy.explorationrate;

import org.hswgt.teachingbox.core.rl.env.State;

/**
 * @author Thomas Wanschik
 * EpsiolonCalculator deacreasing exploration after every n steps / epsisodes to
 * 1/k with k = (int) n/episode or k = (int) n/steps
 */
public class SampleAverageEpsilonDecay extends EpsilonDecayer {

    // default value of the parameter epsilon
    protected double epsilon;
    protected int k = 0;
    
    public SampleAverageEpsilonDecay() {
        this(Mode.EPISODE, 1);
    }

    public SampleAverageEpsilonDecay(Mode mode, int decayInterval) {
        super(mode, decayInterval);
    }

    public double getEpsilon(State state) {
        return this.epsilon;
    }

    public void decayEpsilon() {
        this.k++;
        this.epsilon = 1.0/((double) k);
    }
}
