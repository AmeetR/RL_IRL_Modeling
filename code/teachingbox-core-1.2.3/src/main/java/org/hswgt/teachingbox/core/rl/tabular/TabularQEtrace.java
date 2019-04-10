/**
 *
 * $Id: TabularQEtrace.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.tabular;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.env.StateActionPair;

/**
 * An E-Trace for (s,a) pairs. 
 * Simple implementation with a HashMap. Values smaller than MIN_VALUE will
 * be automatically removed. 
 */
public class TabularQEtrace
{       
    protected LinkedHashMap<StateActionPair, Double> map = new LinkedHashMap<StateActionPair, Double>(16, (float) 0.75, false);
    
    // Logger
    private static final Logger log4j = Logger.getLogger("TabularQEtrace");
    
    /**
     * Entries less than this values will be removed automatically
     */
    public final double MIN_VALUE = 0.00001;
    
    /**
     * Sets the trace value for (s,a)
     * @param s The state-key
     * @param a The action-key
     * @param value The value to set (s,a)
     */
    public void set(State s, Action a, double value)
    {
        map.put(new StateActionPair(s,a), value);
    }
    
    /**
     * Increments the value of (s,a) by a constant
     * @param s The state-key
     * @param a The action-key
     * @param value The value to increment (s,a)
     */
    public void increment(State s, Action a, double value)
    {
        StateActionPair key = new StateActionPair(s,a);
        map.put(key, get(s,a) + value);
    }
    
    /**
     * Returns the trace value for (s,a)
     * @param s The state-key
     * @param a The action-key
     * @return the value for (s,a)
     */
    public double get(State s, Action a)
    {
        Double value = map.get(new StateActionPair(s,a));
        return  value != null ? value : 0;
    }
    
    /**
     * Multiplies each value in the trace by the factor lambda * gamma.
     * Entries smaller than MIN_VALUE will be removed from the trace.
     * @param lambda The e-trace discounting parameter 
     * @param gamma The discounting parameter from the environment
     */
    public void decay(double lambda, double gamma)
    {
    	
    	LinkedHashMap<StateActionPair, Double> delMap = new LinkedHashMap<StateActionPair, Double>(); 
    	
    	// decay all etraces and determine objects to be deleted due to value < MIN_VALUE
        for( Entry<StateActionPair, Double> e : map.entrySet() )
        {
            double value = lambda * gamma * e.getValue();
            
            if( value < MIN_VALUE ) {
                //map.remove(e.getKey());            	
                //System.out.println("  ETrace-Delete: state=" + e.getKey().getState() + ", action=" + e.getKey().getAction() + ", e=" + value);
            	delMap.put(e.getKey(), e.getValue());
            } else {
                e.setValue( value );
                //System.out.println("  ETrace-Update: state=" + e.getKey().getState() + ", action=" + e.getKey().getAction() + ", e=" + value);
            }
        }
         
        // finally delete objects having value < MIN_VALUE
        for( Entry<StateActionPair, Double> e : delMap.entrySet() )
        {
            map.remove(e.getKey());            	
            log4j.debug("Deleting ETRACE due to value < MIN_VALUE");
        }
    }
        
    /**
     * Returns the size of the trace.
     * @return The trace size
     */
    public int size()
    {
        return map.size();
    }
    
    /**
     * @return a Collection view of the values contained in this map. 
     */
    public Set<Entry<StateActionPair, Double>> entrySet() 
    {
        return map.entrySet();
    }

    /**
     * Removes all of the mappings from this map
     */
    public void clear()
    {
        map.clear();
    }
}