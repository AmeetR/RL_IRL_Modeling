/**
 *
 * $Id: FeatureCache.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.feature;

import java.util.LinkedHashMap;
import java.util.Map;

import cern.colt.matrix.DoubleMatrix1D;

/**
 * A cache for vectors with a maximal size
 * Oldest entries will be removed first
 */
public class FeatureCache extends LinkedHashMap<DoubleMatrix1D, DoubleMatrix1D>
{
    private static final long serialVersionUID = 7160499426780153553L;

    // maximal number of entries
    private int maxEntries;
    
    /**
     * Constructor
     * @param maxEntries Maximal size of cache
     */
    public FeatureCache(int maxEntries)
    {
        this.maxEntries = maxEntries;
    }
    
    
    /**
     * Copy Constructor
     * @param other The feature cache
     */
    public FeatureCache(FeatureCache other)
    {
        super(other);
    }


    protected boolean removeEldestEntry(Map.Entry<DoubleMatrix1D, DoubleMatrix1D> eldest) {
       return size() > maxEntries;
    }

}
