/**
 *
 * $Id: ErrorObserver.java 660 2010-11-06 10:10:57Z Thomas Wanschik $
 *
 * @version   $Rev: 660 $
 * @author    $Author: Thomas Wanschik $
 * @date      $Date: 2010-11-06 10:10:57 +0100 (Fr, 11 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.learner;

import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * ErrorsObserver will be notified of td-errors. Intended to be used for function
 * approximation and learning rules which use different td-errors for each weight.
 */
public interface ErrorsObserver
{
    /**
     * corresponding to the currently used state-action pair.
     *
     * Remaining parameters are passed to the ErrorsObserver to get informed of
     * the state, action, ... producing the td-errors. Some algorithm may need
     * this!
     * 
     * @param tderrors The temporal difference error
     * @param tderrorIndexes Array indicating the indexes of tderrors
 	 * @param state The state
	 * @param action The action
	 * @param nexState The successor state
	 * @param nextAction The action of the successor state
	 * @param reward The reward
	 * @param isTerminalState Is terminal state yes/no.
	 */
    public void updateErrors(DoubleMatrix1D tderrors, IntArrayList tderrorIndexes,
            State state, Action action, State nexState, Action nextAction,
            double reward, boolean isTerminalState);
}
