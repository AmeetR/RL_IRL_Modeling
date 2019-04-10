/**
 *
 * $Id: TileBoundingBoxCalculator.java 709 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 709 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network.cmacs.optimization;

import org.hswgt.teachingbox.core.rl.network.optimization.*;
import org.hswgt.teachingbox.core.rl.network.cmacs.Tile;

/*
 * TileBoundingBoxCalculator returns a bounding box for a Tile. In fact this is
 * almost the Tile itself :)
 */

public class TileBoundingBoxCalculator implements BoundingBoxCalculator<Tile> {

    public Box createBoundingBox(Tile node) {
        Tile tile = (Tile) node;
        return new Box(tile.getPosition(), tile.getDimensions());
    }
}
