/**
 *
 * $Id: TabularFeatureFunction.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.tabular;

import org.hswgt.teachingbox.core.rl.datastructures.StateSet;
import org.hswgt.teachingbox.core.rl.feature.FeatureFunction;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;

public class TabularFeatureFunction extends FeatureFunction
{
    private static final long serialVersionUID = 613830151665918022L;

    // all possible state
    protected StateSet sSet;
    
    /**
     * Default Constructor
     * @param sSet Build the table on this StateSet
     */
    public TabularFeatureFunction(final StateSet sSet)
    {
        this.sSet = new StateSet(sSet);
    }
    
    /**
     * Copy Constructor
     * @param other The object to copy
     */
    public TabularFeatureFunction(final TabularFeatureFunction other)
    {
        this(other.sSet);
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureModifier#copy()
     */
    public FeatureFunction copy()
    {
        return new TabularFeatureFunction(this);
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureModifier#getFeatureVectorSize()
     */
    public int getFeatureVectorSize()
    {
        return sSet.size();
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureModifier#getFeatures(cern.colt.matrix.DoubleMatrix1D)
     */
    public DoubleMatrix1D getFeatures(DoubleMatrix1D feat)
    {
        DoubleMatrix1D featureVector = new SparseDoubleMatrix1D(getFeatureVectorSize());
        featureVector.set(sSet.indexOf(feat), 1);
        return featureVector;
    }

    /**
     * Returns the index of the active feature
     * @param feat The feature matrix
     * @return The index of the feature
     */
    public int getFeatureIndex(DoubleMatrix1D feat)
    {
        return sSet.indexOf(feat);
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.feature.FeatureFunction#hasVariableFeatureVectorSize()
     */
    @Override
    public boolean hasVariableFeatureVectorSize()
    {
        return false;
    }
}
