/**
 *
 * $Id: NetworkNodeObserver.java 704 2010-06-24 10:33:51Z twanschik $
 *
 * @version $Rev: 704 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-24 12:33:51 +0200 (Do, 24 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network;

/*
 * Observer interface called when a network node changes its position or its shape.
 * This can be the case for adaptive methods in a network. FeatureGenerators
 * can make use of this to restructure their internal data if needed.
 */
public interface NetworkNodeObserver {

    public void positionChanged(NetworkNode node);

    public void shapeChanged(NetworkNode node);

}
