package org.hswgt.teachingbox.core.rl.learner;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.etrace.ETraceType;
import org.hswgt.teachingbox.core.rl.tools.VectorUtils;
import org.hswgt.teachingbox.core.rl.valuefunctions.DifferentiableFunction;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.linalg.SeqBlas;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.datastructures.WeightsVector;
import org.hswgt.teachingbox.core.rl.feature.FeatureFunction;
import org.hswgt.teachingbox.core.rl.learner.etracedecayrate.ConstantLambda;
import org.hswgt.teachingbox.core.rl.learner.etracedecayrate.LambdaCalculator;
import org.hswgt.teachingbox.core.rl.learner.stepsize.ConstantAlpha;
import org.hswgt.teachingbox.core.rl.learner.stepsize.StepSizeCalculator;

/**
 * Gradient Decent Temporal Difference Learning
 */
public abstract class GradientDescentTdLearner implements Learner {
    private static final long serialVersionUID = 444487333654199521L;

    // Logger
    private final static Logger log4j = Logger.getLogger("GradientDescentTdLearner");
    
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
     * The default eligibility trace type
     */
    public static final ETraceType DEFAULT_ETRACE_TYPE = ETraceType.accumulating;
    
    // The eligibility trace vector, use tderrors as a member of
    // GradientDescentTdLearner to allow auto-resizing of tderrors
    protected WeightsVector eTrace, tderrors;
    
    /**
     * LambdaCalculator, calculates the paramter lambda to use for the etrace.
     */
    protected LambdaCalculator lambdaCalculator;
    
    // The learning rate, one possible step size calculator could use the net
    // to update averages for active nodes for a given state action pair instead
    // of the pure state-action pair itself
    protected StepSizeCalculator stepSizeCalculator;
    
    // The discount-rate
    protected double gamma  = DEFAULT_GAMMA;
    
    // The decay-rate parameter for eligibility traces
    protected ETraceType eTraceType = DEFAULT_ETRACE_TYPE;
    
    // A list of observers for the tderror
    protected List<ErrorsObserver> observers = new LinkedList<ErrorsObserver>();
    
    // This is the valuefunction to learn. Can be Q or V
    protected DifferentiableFunction valueFunction;
    protected FeatureFunction feat;

    /**
     * Constructor 
     * @param valueFunction The value function to learn
     * @param featureFunction The feature function
     * @param actionSet The action set
     */
    public GradientDescentTdLearner(DifferentiableFunction valueFunction,
            FeatureFunction featureFunction, ActionSet actionSet) {
        this.feat = featureFunction;
        this.valueFunction = valueFunction;
        this.eTrace = new WeightsVector(featureFunction, actionSet);
        this.tderrors = new WeightsVector(featureFunction, actionSet);
        this.lambdaCalculator = new ConstantLambda(DEFAULT_LAMBDA);
        this.stepSizeCalculator = new ConstantAlpha(DEFAULT_ALPHA);
    }

    /* (non-Javadoc)
     * @see Learner.Learner#updateNewEpisode(org.hswgt.teachingbox.env.State)
     */
    public void updateNewEpisode(State initialState) {
        // update lambda and stepsize
        this.stepSizeCalculator.updateNewEpisode(initialState);
        this.lambdaCalculator.updateNewEpisode(initialState);

        // reset E-Trace
        eTrace.setWeights(eTrace.getWeights().assign(0));
    }

    /**
     * This method will notify all observer of a new tderror.
     * 
     * @param tderrors The td errors
     * @param tderrorsIndexes The indexes
     * @param state The state
     * @param action The action 
     * @param nextState The successor state
     * @param nextAction The action of the successor state
     * @param reward The reward
     * @param isTerminalState Is terminal state yes/no.
     */
    public void notify(DoubleMatrix1D tderrors, IntArrayList tderrorsIndexes,
            State state, Action action, State nextState, Action nextAction,
            double reward, boolean isTerminalState) {

        for(ErrorsObserver observer : observers )
            observer.updateErrors(tderrors, tderrorsIndexes, state, action,
                nextState, nextAction, reward, isTerminalState);
    }
    
    /**
     * Attaches an observer to this Learner
     * @param obs The observer to attach
     */
    public void addObserver(ErrorsObserver obs) {
        log4j.info("New Observer added: "+obs.getClass());
        this.observers.add(obs);   
    }
    
    /**
     * This method will be called to calculate the tderror
     * @param state State at time t
     * @param action Action at time t
     * @param nextState State at time t+1
     * @param nextAction Action at time t+1
     * @param reward Reward for doing action a in state s
     * @param isTerminalState True if sn is a terminal state
     * @return The tderror
     */
    abstract public DoubleMatrix1D getTdErrors(State state, Action action,
            State nextState, Action nextAction, double reward, boolean isTerminalState);
    
    /**
     * This method will be called to calculate the gradient in the current state
     * @param s State at time t
     * @param a Action at time t
     * @param sn State at time t+1
     * @param an Action at time t+1
     * @param r Reward for doing action a in state s
     * @param isTerminalState True if sn is a terminal state
     * @return The gradient
     */
    abstract public DoubleMatrix1D getGradient(State s, Action a, State sn,
            Action an, double r, boolean isTerminalState);
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.Learner#updateErrors(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean isTerminalState) {
        DoubleMatrix1D tderrorsCopy, grad;
        IntArrayList indexList;
        boolean refresh = false;

        // update lambda and stepsize
        this.stepSizeCalculator.update(state, action, nextState, nextAction, reward, isTerminalState);
        this.lambdaCalculator.update(state, action, nextState, nextAction, reward, isTerminalState);

        // calcuate td-error
        do {
            // wrap all this into a loop because this.notify can change the net
            // resulting in new td-errors
            refresh = false;
            tderrors.setWeights(getTdErrors(state, action, nextState, nextAction,
                    reward, isTerminalState));
            tderrorsCopy = tderrors.getWeights();
            // eTrace = eTrace*LAMBDA + Q.getGradient(s,a);
            grad = getGradient(state, action, nextState, nextAction, reward,
                    isTerminalState);

            // notify all observers, use the gradient to notify observers about
            // indexes corresponding to the currently taken action
            indexList = new IntArrayList();
            grad.getNonZeros(indexList, new DoubleArrayList());
            
            this.notify(tderrors.getWeights(), indexList, state, action, nextState,
                    nextAction, reward, isTerminalState);

            if (!tderrors.getWeights().equals(tderrorsCopy))
                refresh = true;

        } while(refresh == true);

        // update weigths of the e-trace for active features only
        DoubleMatrix1D eWeights = eTrace.getWeights();
        // TODO: Add replacing traces that first clear all action values for the
        // active state
        if( eTraceType == ETraceType.replacing || eTraceType == ETraceType.none) {
            for(int i=0; i<indexList.size(); i++)
                eWeights.set(indexList.getQuick(i), 1);
        }
        else {
            SeqBlas.seqBlas.daxpy(1, grad, eWeights);
        }
        eTrace.setWeights(eWeights);
     
        // theta_i = theta_i + ALPHA*delta_i*e_i;
        valueFunction.updateWeightsScaled(stepSizeCalculator.getAlpha(state, action),
                VectorUtils.multiply(eTrace.getWeights(), tderrors.getWeights()));
        
        // don't use dscal on etrace.getWeights directly because getWeights 
        // returns a copy!
        SeqBlas.seqBlas.dscal(gamma*lambdaCalculator.getLambda(state, action),
                eWeights);
        eTrace.setWeights(eWeights);
        // TODO: set etrace to zero if less than a minimum value
    }

    
    // setter and getter
    

    /**
    * Set the LambdaCalculator.
    * @param lambdaCalculator The LambdaCalculator to be used.
    */
    public void setLambdaCalculator(LambdaCalculator lambdaCalculator) {
    	this.lambdaCalculator = lambdaCalculator;
    }

    /**
     * Get the LambdaCalculator.
     * @return Reference to the LambdaCalculator.
     */
    public LambdaCalculator getLambdaCalculator() {
    	return this.lambdaCalculator;
    }

    /**
     * Set a constant parameter lambda.
     * @param lambda Value of lambda.
     */
    public void setLambda(double lambda) {
    	this.lambdaCalculator = new ConstantLambda(lambda);
    }

    /**
    * Set the StepSizeCalculator.
    * @param stepSizeCalculator The StepSizeCalculator to be used.
    */
    public void setStepSizeCalculator(StepSizeCalculator stepSizeCalculator) {
    	this.stepSizeCalculator = stepSizeCalculator;
    }

    /**
     * Get the StepSizeCalculator.
     * @return Reference to the StepSizeCalculator.
     */
    public StepSizeCalculator getStepSizeCalculator() {
    	return this.stepSizeCalculator;
    }

    /**
     * Set a constant parameter alpha.
     * @param alpha Value of alpha.
     */
    public void setAlpha(double alpha) {
        this.stepSizeCalculator = new ConstantAlpha(alpha);
    }

    /**
     * @return the gamma
     */
    public double getGamma() {
        return gamma;
    }


    /**
     * @param gamma the gamma to set
     */
    public void setGamma(double gamma) {
        this.gamma = gamma;
    }


    /**
     * @return the eTraceType
     */
    public ETraceType getETraceType() {
        return eTraceType;
    }

    /**
     * @param traceType the eTraceType to set
     */
    public void setETraceType(ETraceType traceType) {
        eTraceType = traceType;
    }
}
