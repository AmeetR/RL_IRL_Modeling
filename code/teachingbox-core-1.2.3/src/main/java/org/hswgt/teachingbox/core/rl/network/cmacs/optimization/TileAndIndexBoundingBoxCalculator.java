/**
 *
 * $Id: TileAndIndexBoundingBoxCalculator.java 709 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 709 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network.cmacs.optimization;

import org.hswgt.teachingbox.core.rl.network.cmacs.Tile;
import org.hswgt.teachingbox.core.rl.network.optimization.BoundingBoxCalculator;
import org.hswgt.teachingbox.core.rl.network.optimization.Box;
import org.hswgt.teachingbox.core.rl.network.optimization.NodeAndIndex;

/**
 * BoundingBox calculator which knows how to get a BoundingBox for a Tile from a
 * NodeAndIndex instance.
 */
public class TileAndIndexBoundingBoxCalculator
        implements BoundingBoxCalculator<NodeAndIndex> {

    public Box createBoundingBox(NodeAndIndex tileAndIndex) {
        Tile tile = (Tile) tileAndIndex.getNode();
        return new Box(tile.getPosition(), tile.getDimensions());
    }
}
