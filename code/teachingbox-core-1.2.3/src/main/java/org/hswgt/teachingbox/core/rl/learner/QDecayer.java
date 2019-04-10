/**
 *
 * $Id: QDecayer.java 676 2010-06-11 08:45:17Z twanschik $
 *
 * @version $Rev: 676 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-11 10:45:17 +0200 (Fr, 11 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.learner;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;

/**
 * Abstract class for a decay mechanism which can be used to simulate an agents
 * forgetfulness. Can produce better learning results because suboptimal behaviors
 * will be forgotten.
 * Actually this isn't really a learner in a sense of learning V/Q values but
 * the Learner interface fits exactly the interface needed to implement a Decayer.
 */
public abstract class QDecayer<T extends QFunction> implements Learner {
    public static enum Mode {
        // decays Q-values after every n steps
        STEP,       
        // decays Q-values after every n episodes
        EPISODE
    }
    
    protected Mode mode;
    protected int step = 0;
    protected int episode = 0;
    protected int decayInterval;
    // the Q-Function to decay
    protected T qFunction;

    public QDecayer() {
        
    }
    
    public QDecayer(T qFunction, Mode mode, int decayInterval) {
        this.qFunction = qFunction;
        this.mode = mode;
        this.decayInterval = decayInterval;
    }

    public void update(State state, Action currentAction, State nextState,
            Action nextAction, double reward, boolean isTerminalState) {
        step = step + 1;
        if (mode == Mode.STEP && (step % decayInterval) == 0)
            this.decay();
    }

    public void updateNewEpisode(State initialState) {
        episode = episode + 1;
        if (mode == Mode.EPISODE && (episode % decayInterval ) == 0)
            this.decay();
    }

    // implement decay in order to let the q-values decay
    public abstract void decay();

    // setter and getter
    
    public int getDecayInterval() {
        return decayInterval;
    }

    public void setDecayInterval(int decayInterval) {
        this.decayInterval = decayInterval;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public T getqFunction() {
        return qFunction;
    }

    public void setqFunction(T qFunction) {
        this.qFunction = qFunction;
    }
}
