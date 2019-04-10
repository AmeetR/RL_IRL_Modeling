/**
 *
 * $Id: TabularTdLearner.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.learner;


import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.learner.stepsize.ConstantAlpha;
import org.hswgt.teachingbox.core.rl.learner.stepsize.StepSizeCalculator;
import org.hswgt.teachingbox.core.rl.tabular.TabularValueFunction;

/**
 * Ordinary Tabular TD-Learning
 * TODO: Add eligibility traces
 */
public class TabularTdLearner implements Learner
{
    private static final long serialVersionUID = 444487333654199521L;

    // Logger
    private final static Logger log4j = Logger.getLogger("TabularTdLearner");
    
    /**
     * The default decay-rate parameter for eligibility traces
     */
    public static final double DEFAULT_LAMBDA = 0.9;
    
    /**
     * The default learning rate
     */
    public static final double DEFAULT_ALPHA = 0.2;
    
    /**
     * The default discount-rate
     */
    public static final double DEFAULT_GAMMA = 0.95;
    
    // The learning rate
    protected double alpha  = DEFAULT_ALPHA;
    
    // The discount-rate
    protected double gamma  = DEFAULT_GAMMA;
    
    // A list of observers for the tderror
    protected List<ErrorObserver> observers = new LinkedList<ErrorObserver>();
    
    // This is the value function to learn
    protected TabularValueFunction V;
    
    /**
     * Constructor 
     * @param V The value function to learn
     */
    public TabularTdLearner(TabularValueFunction V)
    {
        this.V = V;
    }
    

    /* (non-Javadoc)
     * @see Learner.Learner#updateNewEpisode(org.hswgt.teachingbox.env.State)
     */
    public void updateNewEpisode(State initialState)
    {
        // reset etrace
    }

    /**
     * This method will notify all observer of a new tderror
     * @param tderror The actual tderror
     * @param reward The reward
     * @param alpha The step-size parameter
     * @param gamma The discounting rate
     * @param s The state
     * @param a The action
     * @param isTerminalState Is terminal state yes/no
     */
    public void notify(double tderror, double reward, StepSizeCalculator alpha, double gamma, State s, Action a, boolean isTerminalState)
    {
        for(ErrorObserver observer : observers )
            observer.learnerUpdate(tderror, reward, alpha, gamma, s, a, isTerminalState);
    }
    
    /**
     * Attaches an observer to this Learner
     * @param obs The observer to attach
     */
    public void addObserver(ErrorObserver obs)
    {
        log4j.info("New Observer added: "+obs.getClass());
        this.observers.add(obs);   
    }
  
   
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.Learner#update(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public void update(State s, Action a, State sn, Action an, double r, boolean isTerminalState)
    {
        // calcuate td-error
        double tderror = (r + gamma*V.getValue(sn) - V.getValue(s));
               
        // V(s,a) = V(s,a) + alpha * [tderror]
        V.setValue(s, (V.getValue(s) + alpha * tderror));
        
        // notify all observers
        this.notify(tderror, r, new ConstantAlpha(alpha), gamma, s, a, isTerminalState);
    }
    
    /**
     * @return the alpha
     */
    public double getAlpha()
    {
        return alpha;
    }


    /**
     * @param alpha the alpha to set
     */
    public void setAlpha(double alpha)
    {
        this.alpha = alpha;
    }


    /**
     * @return the gamma
     */
    public double getGamma()
    {
        return gamma;
    }


    /**
     * @param gamma the gamma to set
     */
    public void setGamma(double gamma)
    {
        this.gamma = gamma;
    }
}
