/**
 *
 * $Id: RandomPolicy.java 1058 2016-10-12 21:16:37Z micheltokic $
 *
 * @version   $Rev: 1058 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2016-10-12 23:16:37 +0200 (Wed, 12 Oct 2016) $
 *
 */

package org.hswgt.teachingbox.core.rl.policy;

import java.util.Random;

import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

import cern.jet.random.Uniform;

/**
 * RandomPolicy
 * Chooses a (valid) action by random. Does not need a Q- or ValueFunction
 */
public class RandomPolicy implements Policy {
    private static final long serialVersionUID = -6522267520569814819L;
    protected ActionSet actionSet;
    
    Random randGenerator = new Random(); 
    
    /**
     * Constructor
     * @param actionSet The actionsSet for the experiment
     */
    public RandomPolicy(ActionSet actionSet) {
        this.actionSet = actionSet;
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.policy.Policy#getAction(org.hswgt.teachingbox.env.State)
     */
    public Action getAction(State state) {
        ActionSet validActions = actionSet.getValidActions(state);        
        return validActions.get(randGenerator.nextInt(validActions.size()));
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.policy.Policy#getBestAction(org.hswgt.teachingbox.env.State)
     */
    public Action getBestAction(State state) {
        return getAction(state);
    }

    public double getProbability(State state, Action action) {
        ActionSet validActions = actionSet.getValidActions(state);
        if (!validActions.contains(action))
            return 0.0;
        return 1.0 / ( (double) validActions.size() );
    }
    
    public void setSeed(long seed) {
    	this.randGenerator.setSeed(seed);
    }
}
