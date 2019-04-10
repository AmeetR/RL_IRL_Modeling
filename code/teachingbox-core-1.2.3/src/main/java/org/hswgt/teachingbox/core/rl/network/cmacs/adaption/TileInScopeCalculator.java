/**
 *
 * $Id: TileInScopeCalculator.java 679 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 679 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network.cmacs.adaption;

import java.io.Serializable;

import cern.colt.matrix.DoubleMatrix1D;
import org.hswgt.teachingbox.core.rl.network.adaption.InScopeCalculator;
import org.hswgt.teachingbox.core.rl.network.cmacs.Tile;

/*
 * Calculates if the operation point falls into the rectangle specified via
 * maxDimensions in order to check if the current opration point is in scope of
 * this rect.
 */
public class TileInScopeCalculator extends InScopeCalculator<Tile>
        implements Serializable {

    private static final long serialVersionUID = -3536598810540720778L;
    protected double[] maxDimensions;

    public TileInScopeCalculator(double[] maxDimensions) {
        this.setMaxDimensions(maxDimensions);
    }

    // set maxDimensions via an example tile
    public TileInScopeCalculator(Tile exampleTile) {
        this(exampleTile.getDimensions().toArray());
    }

    public boolean isInScope(final Tile node, final DoubleMatrix1D feat) {
        return (TileInScopeCalculator.isInScope(node.getPosition(),
                feat.like().assign(this.maxDimensions), feat));
    }

    public static boolean isInScope(final DoubleMatrix1D position,
            final DoubleMatrix1D dimensions, final DoubleMatrix1D feature) {
        if( position.size() != dimensions.size() ||
                position.size() != feature.size()) {
            throw new IllegalArgumentException("feature, position and dimensions" +
                    "must have the same size");
        }

        // is the feature inside the tile (including boundaries)
        for (int i = 0; i < feature.size(); i++) {
            if (!(feature.get(i) >= position.get(i) - dimensions.get(i)/2 &&
                    feature.get(i) <= position.get(i) + dimensions.get(i)/2)) {
                return false;
            }
        }
        return true;
    }

    // getter and setter

    public double[] getMaxDimensions() {
        return maxDimensions.clone();
    }

    public void setMaxDimensions(double[] maxDimensions) {
        this.maxDimensions = maxDimensions.clone();
    }

    // set maxDimensions via an example tile
    public void setMaxDimensions(Tile exampleTile) {
        this.setMaxDimensions(exampleTile.getDimensions().toArray());
    }
}
