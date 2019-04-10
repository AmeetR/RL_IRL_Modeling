/**
 *
 * $Id: ConstantLambda.java 664 2010-11-06 10:10:57Z Thomas Wanschik $
 *
 * @version   $Rev: 664 $
 * @author    $Author: Thomas Wanschik $
 * @date      $Date: 2010-11-06 10:10:57 +0100 (Fr, 11 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.learner.etracedecayrate;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

public class ConstantLambda implements LambdaCalculator {
    /** value of the lambda parameter */
    private double lambda = 1.0;

    /**
     * Constructor.
     * @param lambda Value of the lambda parameter.
     */
    public ConstantLambda(double lambda) {
        this.lambda = lambda;
    }

    /**
     * Set Alpha
     * @param lambda of lambda.
     */
    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public double getLambda(State state, Action action) {
        return this.lambda;
    }

    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean isTerminalState) {
        // TODO Auto-generated method stub
    }

    public void updateNewEpisode(State initialState) {
        // TODO Auto-generated method stub
    }
}
