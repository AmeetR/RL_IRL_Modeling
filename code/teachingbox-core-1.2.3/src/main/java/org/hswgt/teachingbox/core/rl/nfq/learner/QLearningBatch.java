package org.hswgt.teachingbox.core.rl.nfq.learner;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.env.StateActionPair;
import org.hswgt.teachingbox.core.rl.env.TransitionSampleSARS;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQEnvironment;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQMountainCarEnv;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQEnvironment.STATE_CLASS;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQMountainCarEnv.MC_DYNAMICS;
import org.hswgt.teachingbox.core.rl.tabular.HashQFunction;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;


/**
 * This class organizes a QFunction-Batch. It also has some meta functions.
 * @author tokicm
 *
 */
public class QLearningBatch {

	protected List<QValue> qTable = new LinkedList<QValue>(); 
	
	// Logger
    private final static Logger log4j = Logger.getLogger("QLearningBatch");

    
    QFunction Q; 
    ActionSet as;
    NFQEnvironment nfqEnv = null;
    double k; 
    
    /**
     * The constructor
     * @param Q The Q-Function object
     * @param nfqEnv If Environment is an NFQEnvironment (containing a set S^-) use this constructor, 
     * in order to check for state bound violation
     */
	public QLearningBatch(QFunction Q, NFQEnvironment nfqEnv) {
		
		this.Q = Q; 
		this.as = nfqEnv.getActionSet();
		this.nfqEnv = nfqEnv;
	}
	
	/**
	 * performs a Q-iteration on the batch
	 *
	 * @param alpha The step-size parameter
	 * @param gamma The discounting rate
	 */
	public void qLearning(double alpha, double gamma) {
		
		log4j.debug("performing Q-Learning on " + this.qTable.size() + " transition samples. alpha=" + alpha + ", gamma=" + gamma);

		// perform q-learning rule for each transition sample in the current episode
		for( QValue qvalue : qTable) {
			
			State state = qvalue.getState();
			Action action = qvalue.getAction();
			State nextState = qvalue.getNextState();
			double reward = qvalue.getReward();
			double tdError = getTdError(state, action, nextState, reward, gamma);
			
            // value update
        	double newQ;  
            
            // check for S_PLUSPLUS
            if (nfqEnv.getStateClass(nextState) == STATE_CLASS.S_PLUSPLUS) {
            	
            	newQ = nfqEnv.getTerminalQValue(nextState);
            } else {
            	newQ = Q.getValue(state, action) + (alpha*tdError);
            }
            qvalue.setQ(newQ);
		}
	}
	
	
	protected double getTdError(State state, Action action, State nextState, double reward, double gamma)
    {
        
		double tdError = Double.NEGATIVE_INFINITY;
        
        /** State class check */
        STATE_CLASS saClass = nfqEnv.getStateClass(nextState);
        //System.out.println(saClass.name());
        
        // STATE_CLASS.NORMAL
        if (saClass == STATE_CLASS.NORMAL) {

        	double q = Q.getValue(state, action);
            double qn = Q.getMaxValue(nextState);
        	
            tdError = reward + (gamma*qn) - q; 

        //STATE_CLASS.S_PLUS
        } else if (saClass == STATE_CLASS.S_PLUS) {
        	tdError = 0 + gamma*nfqEnv.getTerminalQValue(nextState);
        	
        // STATE_CLASS.S_MINUS
        } else if (saClass == STATE_CLASS.S_MINUS) {
        	tdError = reward + gamma*nfqEnv.getTerminalQValue(nextState);
        }         
        return tdError;
    }

	/**
	 * returns the batch
	 * @return the batch
	 */
	public List<QValue> getQTable() {
		return this.qTable;
	}
	
	/**
	 * Adds a transition sample to the batch.  
	 * @param sa the StateActionPair
	 * @return The created QValue object
	 */
	public QValue putTransitionSample (TransitionSampleSARS sa) {
		QValue q = new QValue (sa);
		this.qTable.add(q);
		return q;
	}	

	
	/**
	 * clears the Q-table  
	 */
	public void clear() {
		
		log4j.debug("CLEARING QTable....");
		this.qTable.clear();		
	}
	
	/** 
	 * returns the current size of the q table
	 * @return The size
	 */
	public int size() {
		return qTable.size(); 
	}
	
	/**
	 * Debugs all Q-values to the console
	 */
	public void printBatch() {
		
		// print transition table
		System.out.println("transition table");
		for( QValue tSample : qTable) {
			
			System.out.println ("s=" + tSample.getState() + ", " + 
								"a=" + tSample.getAction() + ", " + 
								"sn=" + tSample.getNextState() + ", " +
								"r=" + tSample.getReward() + ", " + 
								"Q(s,a)=" + tSample.getQ());
		}		
	}
	
	
	/**
	 * main function for basic tests
	 * @param args The command-line arguments
	 */
	public static void main(String[] args) {
		
		Logger.getRootLogger().setLevel(Level.DEBUG);

		
		// create QBatch
		QLearningBatch qbatch = new QLearningBatch(new HashQFunction(), new NFQMountainCarEnv(MC_DYNAMICS.RIEDMILLER, true));
		
		// add two entries to the set
		qbatch.putTransitionSample(new TransitionSampleSARS (new State(new double[]{0.5,2}), 
											new Action(new double[]{1}), 
											new State(new double[]{0.5,2}), 1));
		qbatch.putTransitionSample(new TransitionSampleSARS (	new State(new double[]{0.8,2}), 
											new Action(new double[]{1}), 
											new State(new double[]{0.8,2}), 1));
		
		qbatch.qLearning(0.1, 0.9);	
		
		qbatch.printBatch();
	}
}
