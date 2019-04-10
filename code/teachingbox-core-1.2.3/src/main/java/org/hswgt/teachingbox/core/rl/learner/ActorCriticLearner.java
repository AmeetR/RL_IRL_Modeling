/**
 *
 * $Id: ActorCriticLearner.java 787 2011-01-11 09:49:14Z micheltokic $
 *
 * @version   $Rev: 787 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2011-01-11 10:49:14 +0100 (Tue, 11 Jan 2011) $
 *
 */

package org.hswgt.teachingbox.core.rl.learner;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.etrace.ETraceType;
import org.hswgt.teachingbox.core.rl.policy.DifferentiablePolicy;
import org.hswgt.teachingbox.core.rl.tools.VectorUtils;
import org.hswgt.teachingbox.core.rl.valuefunctions.DifferentiableVFunction;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.linalg.SeqBlas;
import org.hswgt.teachingbox.core.rl.learner.etracedecayrate.ConstantLambda;
import org.hswgt.teachingbox.core.rl.learner.etracedecayrate.LambdaCalculator;

/**
 * Actor-Critic Learning
 *  BETA STATUS
 * TODO: ActorCritic should be updated to support all features of GradientDecentTdLearner
 * i.e. using StepSizeCalculator and tdError vectors, ...
 */
public class ActorCriticLearner implements Learner
{
    private static final long serialVersionUID = -151555176748902513L;
    
    // Logger
    private final static Logger log4j = Logger.getLogger("ActorCriticLearner");
    
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
    
    // The eligibility trace vector
    protected DoubleMatrix1D eCritic = new DenseDoubleMatrix1D(0);

    // The eligibility trace vector
    protected DoubleMatrix1D eActor = new DenseDoubleMatrix1D(0);

    /**
     * LambdaCalculator, calculates the paramter lambda to use for the etrace.
     */
    protected LambdaCalculator lambdaCalculator;
//    // The decay-rate parameter for eligibility traces
//    protected double lambda = DEFAULT_LAMBDA;
    
    // The learning rate
    protected double alpha  = DEFAULT_ALPHA;
    
    // The discount-rate
    protected double gamma  = DEFAULT_GAMMA;
    
    protected DifferentiableVFunction critic;
    protected DifferentiablePolicy actor;
    
    /**
     * Constructor takes an actor and a critic
     * @param actor The actor
     * @param critic The critic
     */
    public ActorCriticLearner(DifferentiablePolicy actor, DifferentiableVFunction critic)
    {
        this.critic = critic;
        this.actor = actor;
        this.lambdaCalculator = new ConstantLambda(DEFAULT_LAMBDA);
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.Learner#update(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public void update(State state, Action action, State nextState, Action nextAction,
            double reward, boolean isTerminalState)
    {
        this.lambdaCalculator.update(nextState, nextAction, nextState, nextAction, reward, isTerminalState);
        // calcuate td-error
        double tderror = getTdError(state, action, nextState, nextAction, reward, isTerminalState);
        
        { // CRITIC
  
            DoubleMatrix1D grad = critic.getGradient(state);
            
            // check sizes XXX: BUG HERE!
            if( eCritic.size() != grad.size() )
                eCritic = VectorUtils.resize(eCritic, grad.size());
            
            SeqBlas.seqBlas.dscal(lambdaCalculator.getLambda(state, action), eCritic);
            SeqBlas.seqBlas.daxpy(1, grad, eCritic);
                     
            // theta = theta + ALPHA*delta*e;
            critic.updateWeightsScaled(alpha*tderror, eCritic);
        }

        
        { // ACTOR
            DoubleMatrix1D grad = actor.getGradient(state, action);
            // check sizes XXX: BUG HERE!
            if( eActor.size() != grad.size() )
                eActor = VectorUtils.resize(eActor, grad.size());
            
            SeqBlas.seqBlas.dscal(lambdaCalculator.getLambda(state, action), eActor);
            SeqBlas.seqBlas.daxpy(1, grad, eActor);
                     
            // theta = theta + ALPHA*delta*e;
            actor.updateWeightsScaled(alpha*tderror, eActor);
        }
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.learner.Learner#updateNewEpisode(org.hswgt.teachingbox.env.State)
     */
    public void updateNewEpisode(State initialState)
    {
        this.lambdaCalculator.updateNewEpisode(initialState);
        eCritic.assign(0);
        eActor.assign(0);

    }

    protected double getTdError(State s, Action a, State sn, Action an, double r,
            boolean isTerminalState)
    {
        // get value for actual state and next state
        double v = critic.getValue(s);
        double vn = 0;
        
        // the value of a terminal state is 0
        if ( !isTerminalState ) 
        {
            vn = critic.getValue(sn);
        }
        
        // calcuate td-error
        return  (r + gamma*vn - v);
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
