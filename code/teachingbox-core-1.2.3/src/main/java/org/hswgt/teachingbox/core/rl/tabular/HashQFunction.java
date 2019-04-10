/**
 *
 * $Id: HashQFunction.java 1054 2016-10-05 20:29:44Z micheltokic $
 *
 * @version   $Rev: 1054 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2016-10-05 22:29:44 +0200 (Wed, 05 Oct 2016) $
 *
 */

package org.hswgt.teachingbox.core.rl.tabular;

import java.util.HashMap;
import java.util.LinkedHashMap;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.ParameterObserver;

/**
 * Represents the Q-function with the help of a hash-map.
 * There is no need to tell the function in advanced which states/actions
 * will be used. If a certain state/action is unknown to the function,
 * a default value will be returned
 */
public class HashQFunction implements TabularQFunction, ParameterObserver
{
    private static final long serialVersionUID = -1195522308801164208L;
    
    public static final String TABLE_SIZE = "SIZE";

    /**
     * HashMap to store a state value
     */
    protected HashMap<Action, HashValueFunction> map = new LinkedHashMap<Action, HashValueFunction>();

    /**
     * The initial value for new states
     */
    protected double initialValue;
    
    /**
     * The ActionSet of the corresponding Environment.
     */
    protected ActionSet actionSet;
    
    /**
     * Constructor. It is recommended to create the HashQFunction with the corresponding ActionSet
     * (<pre>HashQFunction(double initialValue, ActionSet actionSet)</pre>).
     * @param initialValue The initial value for new states
     */
    public HashQFunction(double initialValue)
    {
        this.initialValue = initialValue;
    }
    
    /**
     * Constructor
     * @param initialValue The initial value for new states
     * @param actionSet The ActionSet of the corresponding Environment.
     */
    public HashQFunction(double initialValue, ActionSet actionSet)
    {
        this.initialValue = initialValue;
        this.actionSet = actionSet;
        
        // initialize HashValueFunction for each action
        for (int i = 0; i < actionSet.size(); i++)
        {
        	map.put(actionSet.get(i), new HashValueFunction(initialValue));
        }
    }

    /**
     * Construtor
     */
    public HashQFunction()
    {
    }

    /**
     * Return the value of a given action a in state s
     * 
     * @param s The state
     * @param a The action
     * @return The value of a in s
     */
    public double getValue(State s, Action a)
    {
        HashValueFunction V = map.get(a);
        
        // check if value does not already exists
        if( V == null){
        	
        	// if action set exists, just return initial value
        	if (actionSet != null) {
        		return initialValue;
        		
        	// otherwise create value
        	} else {
        		V = new HashValueFunction(initialValue);
                map.put(a, V);
        	}
        } 
        return V.getValue(s);
    }
    
    /**
     * Set the value for a State/Action
     * @param s The state
     * @param a The Action
     * @param value The value to set
     */
    public void setValue(State s, Action a, double value)
    {
        HashValueFunction V = map.get(a);
        if( V == null ){
            V = new HashValueFunction(initialValue);
            map.put(a, V);
            
        }
        V.setValue(s, value);
    }
    
    /**
     * Get the maximal Q-Value in State s
     * 
     * @param s The State to evaluate
     * @return the maximal Q-Value
     */
    public double getMaxValue(final State s)
    {
        double max = Double.NEGATIVE_INFINITY;
        
        // get the max value dependent on permitted actions for this state
        if (this.actionSet != null)
        {
        	// getValidActions will return the whole ActionSet again, if no Filter is set 
        	ActionSet validActions = this.actionSet.getValidActions(s);
        	
        	
        	for (int i = 0; i < validActions.size(); i++)
        	{
        		max = Math.max(max, map.get(validActions.get(i)).getValue(s));
        	}
        }
        else
        {
        	// if no ActionSet is set, we simply iterate through all actions (action->HashValueFunction)
	        for( HashValueFunction V : map.values() )
	        {
	            max = Math.max(max, V.getValue(s) );
	        }
        }
        
        return max;
    }

	@Override
	public double getParameter(State s, Action a, String parameter) {
		
		if (parameter == TABLE_SIZE) {
			int size=0;
			for( HashValueFunction V : map.values() ) {	
	            size += V.getTableSize();
	        }
			return size;
			
		} else {
			return -1;
		}
	}
}
