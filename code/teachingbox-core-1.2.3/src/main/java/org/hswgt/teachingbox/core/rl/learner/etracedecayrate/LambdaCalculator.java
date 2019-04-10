/**
 *
 * $Id: LambdaCalculator.java 66$ 2010-11-06 10:10:57Z Thomas Wanschik $
 *
 * @version   $Rev: 664 $
 * @author    $Author: Thomas Wanschik $
 * @date      $Date: 2010-11-06 10:10:57 +0100 (Fr, 11 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.learner.etracedecayrate;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.learner.Learner;

public interface LambdaCalculator extends Learner
{
    /**
     * Get the lambda parameter for eligibility traces.
     * @param state The actual state (for the case, lambda depends on it).
     * @param action The actual action (for the case, lambda depends on it).
     * @return Value of lambda.
     */
    public double getLambda(State state, Action action);
}
