
package org.hswgt.teachingbox.core.rl.tabular;

import java.util.HashMap;
import java.util.LinkedHashMap;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * Represents the value function with the help of a hash-map.
 * There is no need to tell the function in advanced which states
 * will be used. If a certain state is unknown to the function,
 * a default value will be returned
 */
public class HashValueFunction implements TabularValueFunction
{
    private static final long serialVersionUID = 4920868114014330086L;
    
    /**
     * HashMap to store a state value
     */
    protected HashMap<State, Double> map = new LinkedHashMap<State, Double>();

    /**
     * The initial value for new states
     */
    protected double initialValue;
    
    /**
     * Constructor
     * @param initialValue The initial value for new states
     */
    public HashValueFunction(double initialValue)
    {
        this.initialValue = initialValue;
    }
    
    /**
     * Sets a value of a given State
     * @param s The state
     * @param value The value to set
     */
    public void setValue(State s, double value)
    {
        map.put(s, value);
    }
    
    /**
     * Return the value of a given state
     * @param s The state
     * @return The value of the state
     */
    public double getValue(State s)
    {
        Double value = map.get(s);
        if (value == null)
        {
            //map.put(s, initialValue);
            return initialValue;
        }
        return value;
    }
    
    /**
     * returns the size of the hashmap
     * @return The size
     */
    public int getTableSize() {
    	return map.size();
    }
}
