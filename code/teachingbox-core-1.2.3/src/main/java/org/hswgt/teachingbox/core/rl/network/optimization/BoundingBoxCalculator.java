/**
 *
 * $Id: BoundingBoxCalculator.java 709 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 709 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network.optimization;

/*
 * Interface used to calculate bounding boxes for Objects of type T.
 * T can be NetworkNode for example so that we can calculate a BoundingBox
 * for them.
 */

public interface BoundingBoxCalculator<T> {

    public Box createBoundingBox(T node);

}
