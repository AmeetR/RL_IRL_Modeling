package org.hswgt.teachingbox.core.rl.experiment;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;



/**
 * A class for averaging the optimal action selection at each 
 * time step (over episodes) w.r.t. the a given Q-function. This
 * Q-function may be optimal, in order to compare wheather the agent
 * selected the "real" optimal action. 
 * @author tokicm
 *
 */
public class OptimalActionAverager extends ScalarAverager {

    private QFunction Q = null;

    private static final long serialVersionUID = 5142231296709151487L;

    /**
     * The constructor
     * @param maxSteps the maximum steps per episode
     * @param configString the config string for plotting
     * @param Q the Q-function
     */
    public OptimalActionAverager(int maxSteps, String configString, QFunction Q) {
        super(maxSteps, configString);
        this.Q = Q;
    }

    /**
     * The constructor
     * @param maxSteps the maximum steps per episode
     * @param configString the config string for plotting
     */
    public OptimalActionAverager(int maxSteps, String configString) {
        super(maxSteps, configString);
    }

    /**
     * Sets the given (optimal) Q-Function
     * @param q the q to set
     */
    public void setQ(QFunction q) {
        Q = q;
    }


    /*
     * (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.DataAverager#update(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public void update(State s, Action a, State sn, Action an, double r,
            boolean terminalState) {

        double optimalAction = 0;

        // determine if the optimal action has been selected
        if (Q.getMaxValue(sn) == Q.getValue(sn, an)) {
            optimalAction = 1;
        }

        this.updateAverage(optimalAction);
    }
}
