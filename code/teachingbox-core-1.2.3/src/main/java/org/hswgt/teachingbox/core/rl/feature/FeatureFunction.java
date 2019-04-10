/**
 * 
 * $Id: FeatureFunction.java 988 2015-06-17 19:48:01Z micheltokic $
 * 
 * @version $Rev: 988 $
 * @author $Author: micheltokic $
 * @date $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 * 
 */

package org.hswgt.teachingbox.core.rl.feature;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import cern.colt.matrix.DoubleMatrix1D;

/**
 * A feature function is responsible to create or modify a feature vector in any
 * way. For example it could be used to discretise a continuous variable or
 * simply enrich the given feature vector
 */
public abstract class FeatureFunction implements Serializable
{
    private static final long serialVersionUID = -937590864203951859L;

    // Logger
    private static final Logger log4j = Logger.getLogger("FeatureFunction");

    // list of observer
    protected List<FeatureFunctionObserver> observers = new LinkedList<FeatureFunctionObserver>();

    /**
     * Default constructor
     */
    public FeatureFunction()
    {
        
    }
    
    /**
     * Copy constructor
     * @param other The feature function
     */
    public FeatureFunction(FeatureFunction other)
    {
        this.observers = new LinkedList<FeatureFunctionObserver>(other.observers);
    }
    
    /**
     * Return the modified feature vector
     * 
     * @param feat The input vector
     * @return the modified vector
     */
    abstract public DoubleMatrix1D getFeatures(final DoubleMatrix1D feat);

    /**
     * Returns the size of the feature vector that is returned by
     * 
     * <pre>
     * getFeatures(final DoubleMatrix1D feat)
     * </pre>
     * 
     * @return The size of the feature vector
     */
    abstract public int getFeatureVectorSize();

    /**
     * Returns a deep copy of the FeatureFunction
     * 
     * @return A deep copy of the FeatureFunction
     */
    abstract public FeatureFunction copy();

    /**
     * This function must return true of the length of feature vector changes
     * over time. It is important for the calling instance to know this. You
     * must also call the
     * 
     * <pre>
     * notityFeatureVectorSizeChanges(newSize)
     * </pre>
     * 
     * method whenever the size is changed
     * 
     * @return true if the size of the feature vector changes
     */
    abstract public boolean hasVariableFeatureVectorSize();

    /**
     * This method will notify all observer, that a the size
     * if the feature vector has changed
     * @param newSize The new size
     */
    protected void notifyFeatureVectorSizeChanged(int newSize)
    {
        for (FeatureFunctionObserver observer : observers)
        {
            observer.updateFeatureVectorSize(newSize);
        }
    }

    /**
     * This method will notify all observer, that a new feature has been added
     * to the feature vector at index 'index'
     * @param newSize The new size
     * @param index The index the feature has been added to
     */
    protected void notifyFeatureAdded(int newSize, int index)
    {
        for (FeatureFunctionObserver observer : observers)
        {
            observer.updateFeatureAdded(index);
        }
        notifyFeatureVectorSizeChanged(newSize);
    }

    /**
     * This method will notify all observer, that a new feature has been removed
     * to the feature vector at index 'index'
     * @param newSize The new size
     * @param index The index the feature has been removed from
     */
    protected void notifyFeatureRemoved(int newSize, int index)
    {
        for (FeatureFunctionObserver observer : observers)
        {
            observer.updateFeatureRemoved(index);
        }
        notifyFeatureVectorSizeChanged(newSize);
    }

    /**
     * Attaches an observer to this 
     * 
     * @param obs The observer to attach
     */
    public void addObserver(final FeatureFunctionObserver obs)
    {
        log4j.info("New Observer added: " + obs.getClass());
        if( !this.observers.contains(obs) )
            this.observers.add(obs);
    }
    
    /**
     * Remove an observer from this 
     * @param obs The observer to detach
     */
    public void removeObserver(final FeatureFunctionObserver obs)
    {
        log4j.info("New Observer removed: " + obs.getClass());
        this.observers.remove(obs);
    }
}
