/**
 * 
 * $Id: EpsilonGreedyPolicy.java 1054 2016-10-05 20:29:44Z micheltokic $
 * 
 * @version $Rev: 1054 $
 * @author $Author: micheltokic $
 * @date $Date: 2016-10-05 22:29:44 +0200 (Wed, 05 Oct 2016) $
 * 
 */

package org.hswgt.teachingbox.core.rl.policy;

import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;

import org.hswgt.teachingbox.core.rl.policy.explorationrate.ConstantEpsilon;
import org.hswgt.teachingbox.core.rl.policy.explorationrate.EpsilonCalculator;


/**
 * Returns selectedAction random action with probability p(epsilonCalculator) and
 * with p(1-epsilonCalculator) the best action.
 *
 * Default epsilon is 0.1
 */
public class EpsilonGreedyPolicy extends GreedyPolicy {
    private static final long serialVersionUID = -7925509483406872885L;
    protected EpsilonCalculator epsilonCalculator;
    protected Action selectedAction;

    /**
     * @param Q The QFunction for state values
     * @param actionSet The current ActionSet
     * @param epsilon probability of taking selectedAction random action
     */
    public EpsilonGreedyPolicy(QFunction Q, ActionSet actionSet, double epsilon) {
        super(Q, actionSet);
        this.setEpsilon(epsilon);
    }

    /**
     * @param Q The QFunction for state values
     * @param actionSet The current ActionSet
     * @param epsilonCalculator EpsilonCalculator to use
     */
    public EpsilonGreedyPolicy(QFunction Q, ActionSet actionSet,
            EpsilonCalculator epsilonCalculator) {
        super(Q, actionSet);
        this.setEpsilonCalculator(epsilonCalculator);
    }

    /**
     * @param Q The QFunction for state values
     * @param actionSet The current ActionSet
     */
    public EpsilonGreedyPolicy(QFunction Q, ActionSet actionSet) {
        super(Q, actionSet);
        this.setEpsilon(0.1);
    }

    /**
     * Returns an action for every given state
     * With probability of epsilon selectedAction random action and with (1-epsilon)
     * the best action
     * @param state The state in which an action should be chosen
     * @return The action to take in state s
     */
    public Action getAction(final State state) {
    	//System.out.println ("returning action with epsilon=" + getEpsilonCalculator().getEpsilon(state));
        return getAction(state, epsilonCalculator.getEpsilon(state));
    }

    /**
     * Returns an action for every given state
     * With probability of epsilon selectedAction random action and with (1-epsilon)
     * the best action
     * @param state The state in which an action should be chosen
     * @param epsilon probability of taking selectedAction random action
     * @return The action to take in state s
     */
    public Action getAction(final State state, double epsilon) {
        if( randGenerator.nextDouble() < epsilon ) {
            final ActionSet validActions = actionSet.getValidActions(state);
            selectedAction = validActions.get( randGenerator.nextInt(validActions.size()) );
            return selectedAction.copy();
        }

        return getBestAction(state);
    }

    public double getProbability(State state, Action action) {
        ActionSet validActions = this.actionSet.getValidActions(state);
        if (!validActions.contains(action))
            return 0.0;

        double epsilon = this.epsilonCalculator.getEpsilon(state);
        ActionSet bestActions = this.getBestActions(state);
        if (bestActions.contains(action))
            return (1.0 - epsilon) /( (double) bestActions.size() ) +
                    epsilon / ((double) validActions.size());

        return epsilon / ( (double) validActions.size() );
    }

    /**
    * Set the EpsilonCalculator.
    * @param epsilonCalculator The EpsilonCalculator to be used.
    */
    public void setEpsilonCalculator(EpsilonCalculator epsilonCalculator) {
    	this.epsilonCalculator = epsilonCalculator;
    }

    /**
     * Get the EpsilonCalculator.
     * @return Reference to the EpsilonCalculator.
     */
    public EpsilonCalculator getEpsilonCalculator() {
    	return this.epsilonCalculator;
    }

    /**
     * @param epsilon the epsilon to set
     */
    public void setEpsilon(double epsilon) {
    	//System.out.println ("setting epsilon=" + epsilon);
        this.epsilonCalculator = new ConstantEpsilon(epsilon);
    }
}
