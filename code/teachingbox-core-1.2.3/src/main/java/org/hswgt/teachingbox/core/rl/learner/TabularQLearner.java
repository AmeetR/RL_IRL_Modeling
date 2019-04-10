package org.hswgt.teachingbox.core.rl.learner;


import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.env.StateActionPair;
import org.hswgt.teachingbox.core.rl.etrace.ETraceType;
import org.hswgt.teachingbox.core.rl.learner.etracedecayrate.ConstantLambda;
import org.hswgt.teachingbox.core.rl.learner.etracedecayrate.LambdaCalculator;
import org.hswgt.teachingbox.core.rl.learner.stepsize.ConstantAlpha;
import org.hswgt.teachingbox.core.rl.learner.stepsize.StepSizeCalculator;
import org.hswgt.teachingbox.core.rl.tabular.TabularQEtrace;
import org.hswgt.teachingbox.core.rl.tabular.TabularQFunction;

/**
 * Ordinary Tabular Q-Learning
 * TODO: Add eligibility traces
 */
public class TabularQLearner implements Learner
{
    private static final long serialVersionUID = 443387333654199521L;
    
    // Logger
    private final static Logger log4j = Logger.getLogger("TabularQLearner");
    
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
    
    /**
     * StepSizeCalculator, calculates the step size alpha.
     */
    protected StepSizeCalculator stepSizeCalculator;
    
    // The learning rate
    // protected double alpha  = DEFAULT_ALPHA;
    
    // The discount-rate
    protected double gamma  = DEFAULT_GAMMA;
    
    /**
    * LambdaCalculator, calculates the paramter lambda to use for the etrace.
    */
    protected LambdaCalculator lambdaCalculator;
//    // The decay-rate
//    protected double lambda = DEFAULT_LAMBDA;
    
    // A list of observers for the tderror
    protected List<ErrorObserver> observers = new LinkedList<ErrorObserver>();
    
    // This is the Q-Function to learn
    protected TabularQFunction Q;
    
    
    // Value for etrace type (replacing or accumulating)
    protected ETraceType etraceType = ETraceType.none;
    
    // local etrace
    protected TabularQEtrace etrace = new TabularQEtrace();
    
    /**
     * Constructor 
     * @param Q The QFunction to use
     */
    public TabularQLearner(TabularQFunction Q)
    {
        this.Q = Q;
        this.stepSizeCalculator = new ConstantAlpha(DEFAULT_ALPHA);
        this.lambdaCalculator = new ConstantLambda(DEFAULT_LAMBDA);
    }
    
    /**
     * Constructor 
     * @param Q The QFunction to use.
     * @param stepSizeCalculator The step-size-parameter (alpha) method to use.
     * @param lambdaCalculator The lambda calculator 
     */
    public TabularQLearner(TabularQFunction Q,
            StepSizeCalculator stepSizeCalculator, LambdaCalculator lambdaCalculator)
    {
        this.Q = Q;
        this.stepSizeCalculator = stepSizeCalculator;
        this.lambdaCalculator = lambdaCalculator;
    }

    /* (non-Javadoc)
     * @see Learner.Learner#updateNewEpisode(org.hswgt.teachingbox.env.State)
     */
    public void updateNewEpisode(State initialState)
    {
        // update lambda and stepSize
        this.lambdaCalculator.updateNewEpisode(initialState);
        this.stepSizeCalculator.updateNewEpisode(initialState);
        etrace.clear();
        
    }

    /**
     * This method will notify all observer of a new tderror
     * @param tderror The actual tderror
     * @param reward The reward
     * @param alpha The StepSizeCalculator object
     * @param gamma The discounting rate
     * @param s The state
     * @param a The action
     * @param isTerminalState Is terminal state yes/no.
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
  
    /**
     * This method will be called to calculate the tderror
     * @param s State at time t
     * @param a Action at time t
     * @param sn State at time t+1
     * @param an Action at time t+1
     * @param r Reward for doing action a in state s
     * @param isTerminalState True if sn is a terminal state
     * @return The tderror
     */
    protected double getTdError(State s, Action a, State sn, Action an, double r, boolean isTerminalState)
    {
        // get Q-Value for actual state and next state
        double q = Q.getValue(s, a);
        double qn = 0;
        
        // the Q-Value of a terminal state is 0
        if ( !isTerminalState ) 
        {
            // Q-Learning uses the best action to perform an update
            // and the best action is the one with best q-value
            qn = Q.getMaxValue(sn);
        }
        
        return (r + gamma*qn - q);
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.Learner#update(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */   
    public void update(State state, Action action, State nextState, Action nextAction,
            double reward, boolean isTerminalState)
    {
        // update lambda and alpha
        this.lambdaCalculator.update(state, action, nextState, nextAction, reward, isTerminalState);
        this.stepSizeCalculator.update(state, action, nextState, nextAction, reward, isTerminalState);
        
        // calculate td-error
        double tderror = getTdError(state, action, nextState, nextAction, reward, isTerminalState);
        double alpha;            
        
        /*
        log4j.debug("Q-Update for state(" + s.get(0) + "," + s.get(1) + ") " + 
        						"action(" + a.get(0) + "," + a.get(1) + ") " + 
        						" reward = " + r);
        
        log4j.debug("Q-Update for state_n(" + sn.get(0) + "," + sn.get(1) + ") " + 
				"action(" + an.get(0) + "," + an.get(1) + ") " + 
				" reward = " + r);
        */
        
        
        // ELIGIBILITY TRACES
        if( etraceType == ETraceType.replacing || etraceType == ETraceType.none) {
            etrace.set(state, action, 1);
        } else if (etraceType == ETraceType.accumulating)  { 
            etrace.increment(state, action, 1);
        }
        
        // is the action chosen the best action? (Watkins Q(\lambda)
        boolean bestAction = (Q.getValue(nextState,nextAction) == Q.getMaxValue(nextState));
        
        // Q(s,a) = Q(s,a) + alpha * [tderror] * etrace(s,a)
        //System.out.println ("\nLEARNING IN STATE state=" + state + ", action=" + action);
        //System.out.println (" => updating e-traces");
        
        for(Entry<StateActionPair, Double> e : etrace.entrySet())
        {
            State sc = e.getKey().getState();            
            Action ac = e.getKey().getAction();
                        
            // get value of alpha from step size parameter method
            alpha = this.stepSizeCalculator.getAlpha(sc, ac);
                       
            // value update
        	//log4j.debug("oldQValue: " + Q.getValue(sc,ac));
            if (etraceType == ETraceType.none) {
            	Q.setValue(sc, ac, (Q.getValue(sc, ac) + alpha * tderror));
            } else { 
	            Q.setValue(sc, ac, (Q.getValue(sc, ac) + alpha * tderror * etrace.get(sc, ac)));
            }
            //log4j.debug("newQValue: " + Q.getValue(sc,ac));
            
            // notify all observers
            this.notify(tderror, reward, this.stepSizeCalculator, gamma, sc, ac, isTerminalState);
        }

        // clear etraces in case "a" was an exploratory action, or if the use of etraces is not desired
        if( bestAction == false || etraceType == ETraceType.none) {
            etrace.clear();

        // decay the etrace 
        } else {
            etrace.decay(lambdaCalculator.getLambda(state, action), gamma);
        }
        

    }
    
    /**
     * Set the StepSizeCalculator. ConstantAlpha(1.0) by default.
     * @param stepSizeCalculator The StepSizeCalculator to be used.
     */
    public void setStepSizeCalculator(StepSizeCalculator stepSizeCalculator)
    {
    	this.stepSizeCalculator = stepSizeCalculator;
    }


    /**
     * Get the StepSizeCalculator.
     * @return Reference to the StepSizeCalculator.
     */
    public StepSizeCalculator getStepSizeCalculator()
    {
    	return this.stepSizeCalculator;
    }
    
    /**
     * Set a constant step size parameter alpha. This method creates and sets a
     * ConstantAlpha object, if ConstantAlpha is not the StepSizeCalculator.
     * @param alpha Value of alpha.
     */
    public void setAlpha(double alpha)
    {
    	this.stepSizeCalculator = new ConstantAlpha(alpha);
    }

    /**
    * Set the LambdaCalculator. ConstantLambda(1.0) by default.
    * @param lambdaCalculator The LambdaCalculator to be used.
    */
    public void setLambdaCalculator(LambdaCalculator lambdaCalculator)
    {
    	this.lambdaCalculator = lambdaCalculator;
    }


    /**
     * Get the LambdaCalculator.
     * @return Reference to the LambdaCalculator.
     */
    public LambdaCalculator getLambdaCalculator()
    {
    	return this.lambdaCalculator;
    }

    /**
     * Set a constant parameter lambda. This method creates and sets a
     * ConstantLambda object, if ConstantLambda is not the LambdaCalculator.
     * @param lambda Value of lambda.
     */
    public void setLambda(double lambda)
    {
    	this.lambdaCalculator = new ConstantLambda(lambda);
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

    /**
     * @return the etraceType
     */
    public ETraceType getEtraceType()
    {
        return etraceType;
    }


    /**
     * @param etraceType the etraceType to set
     */
    public void setEtraceType(ETraceType etraceType)
    {
        this.etraceType = etraceType;
    }
}
