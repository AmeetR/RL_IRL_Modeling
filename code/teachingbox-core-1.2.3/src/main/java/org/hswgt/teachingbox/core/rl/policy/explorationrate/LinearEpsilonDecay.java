package org.hswgt.teachingbox.core.rl.policy.explorationrate;

import org.hswgt.teachingbox.core.rl.env.State;


/**
 * @author Thomas Wanschik
 * EpsiolonCalculator deacreasing exploration after every n steps / epsisodes in
 * a linear way i.e. epsilon -= decreaseRate;
 */
public class LinearEpsilonDecay extends EpsilonDecayer {

    // default value of the parameter epsilon
    protected double epsilon = 1.0;
    protected double decreaseRate = 0.05;

    public LinearEpsilonDecay() {
        this(EpsilonDecayer.Mode.EPISODE, 1, 1.0, 0.05);
    }

    public LinearEpsilonDecay(Mode mode, int decayInterval, double startEpsilon, double decreaseRate) {
        super(mode, decayInterval);
        this.decreaseRate = decreaseRate;
        this.epsilon = startEpsilon;
    }

    public double getEpsilon(State state) {
        return this.epsilon;
    }

    public void decayEpsilon() {
        this.epsilon -= this.decreaseRate;
        if (this.epsilon <= 0.0)
            this.epsilon = 0.0;
    }

    // getter and setter

    public double getDecreaseRate() {
        return decreaseRate;
    }

    public void setDecreaseRate(double decreaseRate) {
        this.decreaseRate = decreaseRate;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }
}
