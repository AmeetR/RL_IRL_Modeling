/**
 *
 * $Id: FeatureFunctionObserver.java 696 2010-06-24 10:33:51Z twanschik $
 *
 * @version   $Rev: 696 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-06-24 12:33:51 +0200 (Thu, 24 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.feature;

import java.io.Serializable;

/**
 * Observes a FeatureFunction
 */
public interface FeatureFunctionObserver extends Serializable
{
    /**
     * Will be called whenever the size of the feature vector changes
     * @param newSize The new size of the feature vector
     */
    public void updateFeatureVectorSize(int newSize);

    /**
     * Will be called whenever a new feature is added at index 'index'
     * @param index The index the feature has been added to, index starts with 0
     */
    public void updateFeatureAdded(int index);

    /**
     * Will be called whenever a new feature is removed at index 'index'
     * @param index The index the feature has been removed from, index starts with 0
     */
    public void updateFeatureRemoved(int index);
}
