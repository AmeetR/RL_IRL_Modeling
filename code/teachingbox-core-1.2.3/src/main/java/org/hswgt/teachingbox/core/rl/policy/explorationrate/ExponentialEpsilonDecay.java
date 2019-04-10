package org.hswgt.teachingbox.core.rl.policy.explorationrate;

import org.hswgt.teachingbox.core.rl.env.State;

/**
 *
 * @author twanschik
 * Decays epsilon by a given factor after each n steps/episodes resulting in an
 * exponentionaly decaying exploration rate.
 */

public class ExponentialEpsilonDecay extends EpsilonDecayer {
    protected double decayRate;

    // threshold can be used to set the qFuntion value to 0 when it reaches the
    // threshold.
    protected double threshold;
    protected double epsilon;

    public ExponentialEpsilonDecay() {
        this(0.9995, 0.01, 1.0);
    }

    public ExponentialEpsilonDecay(double startEpsilon) {
        this(0.9995, 0.0, startEpsilon);
    }

    public ExponentialEpsilonDecay(double decayRate, double threshold,
            double startEpsilon) {
        this(decayRate, threshold, startEpsilon, EpsilonDecayer.Mode.EPISODE, 1);
    }

    public ExponentialEpsilonDecay(double decayRate, double threshold,
            double startEpsilon, EpsilonDecayer.Mode mode, int decayInterval) {
        super(mode, decayInterval);
        this.threshold = threshold;
        this.decayRate = decayRate;
        this.setEpsilon(startEpsilon);
    }

    public void decayEpsilon() {
        this.epsilon *= this.decayRate;
        // reset values lower than the given threshold to 0.0
        if (Math.abs(this.epsilon) < threshold)
            this.epsilon = 0.0;
    }

    // getter and setter
    
    public double getEpsilon(State state) {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }
    
    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getDecayRate() {
        return decayRate;
    }

    public void setDecayRate(double decayRate) {
        this.decayRate = decayRate;
    }
}
