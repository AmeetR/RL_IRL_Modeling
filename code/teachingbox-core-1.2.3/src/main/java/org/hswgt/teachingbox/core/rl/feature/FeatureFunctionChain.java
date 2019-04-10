/**
 *
 * $Id: FeatureFunctionChain.java 708 2010-06-25 11:54:45Z twanschik $
 *
 * @version   $Rev: 708 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-06-25 13:54:45 +0200 (Fri, 25 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.feature;

import java.util.LinkedList;

import cern.colt.matrix.DoubleMatrix1D;

/**
 * BETA
 * Connect a number of features together
 */
public class FeatureFunctionChain extends FeatureFunction implements
        FeatureFunctionObserver
{
    private static final long serialVersionUID = 6256686510555993678L;
    protected LinkedList<FeatureFunction> chain = new LinkedList<FeatureFunction>();
    
    /**
     * Constructor
     */
    public FeatureFunctionChain()
    {
    }

    /**
     * Copy constructor
     * @param other The Chain to copy
     */
    public FeatureFunctionChain(FeatureFunctionChain other)
    {
        super(other);
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureFunction#copy()
     */
    @Override
    public FeatureFunctionChain copy()
    {
        return new FeatureFunctionChain(this);
    }
    
    /**
     * Add a new FeatureFunction to the chain
     * @param ff The FeatureFunction to add
     */
    public void addFeatureFunction(FeatureFunction ff)
    {
        // first detach this as an observer from the last feature function
        if( chain.size() > 0 )
            chain.getLast().removeObserver(this);
        
        // add the new feature function to chain
        chain.add(ff);
        
        // add this as an observer to the new feature function
        ff.addObserver(this);
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureFunction#getFeatureVectorSize()
     */
    @Override
    public int getFeatureVectorSize()
    {
        return chain.getLast().getFeatureVectorSize();
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureFunction#getFeatures(cern.colt.matrix.DoubleMatrix1D)
     */
    @Override
    public DoubleMatrix1D getFeatures(DoubleMatrix1D feat)
    {
        DoubleMatrix1D result = feat.copy();
        for( FeatureFunction ff : chain ){
            result = ff.getFeatures(result);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureFunction#hasVariableFeatureVectorSize()
     */
    @Override
    public boolean hasVariableFeatureVectorSize()
    {
        for( FeatureFunction ff : chain ){
            if( ff.hasVariableFeatureVectorSize() )
                return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureFunctionObserver#updateFeatureVectorSize(int)
     */
    public void updateFeatureVectorSize(int newSize)
    {
        this.notifyFeatureVectorSizeChanged(newSize);
    }

    // TODO: implement this

    public void updateFeatureAdded(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateFeatureRemoved(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
