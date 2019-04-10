/**
 * 
 * $Id: GreedyPolicy.java 1054 2016-10-05 20:29:44Z micheltokic $
 * 
 * @version $Rev: 1054 $
 * @author $Author: micheltokic $
 * @date $Date: 2016-10-05 22:29:44 +0200 (Wed, 05 Oct 2016) $
 * 
 */

package org.hswgt.teachingbox.core.rl.policy;

import java.util.Random;

import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;


/**
 * A GreedyPolicy always returns the best action
 */
public class GreedyPolicy implements Policy {
    private static final long serialVersionUID = 2775611047393532162L;
    protected final QFunction Q;
    protected final ActionSet actionSet;
    protected Random randGenerator = new Random();


    public GreedyPolicy(QFunction Q, ActionSet actionSet) {
        this.Q = Q;
        this.actionSet = actionSet;
    }
    
    public void setSeed(long seed) {
    	randGenerator.setSeed(seed);
    }

    /**
     * Returns an action for every given state
     * @param state The state in which an action should be chosen
     * @return The action to take in state s
     */
    public Action getAction(final State state) {
        return getBestAction(state);
    }

    /**
     * Returns the best action for every given state
     * @param state The state in which an action should be chosen
     * @return The action to take in state s
     */
    public Action getBestAction(State state) {
        ActionSet bestActions = this.getBestActions(state);

        // sample on of the best actions randomly
        int randIndex = randGenerator.nextInt(bestActions.size());
        return bestActions.get(randIndex);
    }

    public double getProbability(State state, Action action) {
        ActionSet bestActions = this.getBestActions(state);
        if (!bestActions.contains(action))
            return 0.0;
        return 1.0 / ( (double) bestActions.size() );
    }

    // helper functions

    protected ActionSet getBestActions(State state) {
        final ActionSet validActions = actionSet.getValidActions(state);
        double max = Double.NEGATIVE_INFINITY;
        ActionSet bestActions = new ActionSet();
        double vnew = 0;
        
        for (int i = 0; i < validActions.size(); i++) {
            vnew = Q.getValue(state, validActions.get(i));

            // skip if value is less than best
            if( max > vnew ) {
                continue;
            }

            // clear the best-ActionSet and add the
            // new best action to it
            else if( max < vnew ) {
                max = vnew;
                bestActions.clear();
                bestActions.add(validActions.get(i));
                continue;
            }
            // if we have another best action, add
            // it to the best-ActionSet
            else { // max == vnew
                bestActions.add(validActions.get(i));
            }
        }
        return bestActions;
    }
}
