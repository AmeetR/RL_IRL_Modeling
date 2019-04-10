/**
 *
 * $Id: Tile.java 671 2010-06-11 08:45:17Z twanschik $
 *
 * @version $Rev: 671 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-11 10:45:17 +0200 (Fr, 11 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.network.cmacs;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import org.hswgt.teachingbox.core.rl.network.NetworkNode;

/**
 * * A Tile with position and dimensions (width in each dimension).
 *
 * <pre>
   1 +---------+++++++++++++++++++++----------+
     +                                        +
     |                                        |
     |                                        |
 0.8 +                                        +
     |                                        |
     |                                        |
     |                                        |
 0.6 +                                        +
     |                                        |
     |                                        |
     |                                        |
 0.4 +                                        +
     |                                        |
     |                                        |
     |                                        |
 0.2 +                                        +
     |                                        |
     |                                        |
     +                                        +
   0 +++++++++++---------+---------++++++++++++
    -2        -1         0         1          2         3         4         5
    </pre>
 * Example:
    <pre>
    // create new Tile with position=0 and width=1.
    Tile tile = new Tile(0,2);
    // get the value at 0.5
    double v = tile.getValue(0.5);
    </pre>
 */

public class Tile extends NetworkNode {

    protected DoubleMatrix1D dimensions;

    public Tile() {
    }

    /**
     * Constructor
     * Creates a new Tile with center position and widths dimensions
     * @param position Center
     * @param dimensions Widths
     */
    public Tile(DoubleMatrix1D position, DoubleMatrix1D dimensions) {
        if( position.size() != dimensions.size() ) {
	        throw new IllegalArgumentException("position and dimension must" +
                        "have the same size");
	}

        this.setPosition(position);
        this.setDimensions(dimensions);
        this.dimension = this.dimensions.size();
    }

    /**
     * Constructor
     * Creates a new Tile with center position and widths dimensions
     * @param position Center
     * @param dimensions Widths
     */
    public Tile(double[] position, double[] dimensions) {
        this(new DenseDoubleMatrix1D(position), new DenseDoubleMatrix1D(dimensions));
    }

    /**
     * Copy Constructor
     * @param other The Tile
     */
    public Tile(Tile other) {
        this(other.position, other.dimensions);
    }

    /**
     * returns 1 if state is inside the tile otherwise 0
     * @param state The input
     */
    public double getValue(DoubleMatrix1D state) {
        return Tile.getValue(state, position, dimensions);
    }

    /**
     * returns 1 if state is inside the tile otherwise 0
     * @param feature The input
     * @param position The location of the center
     * @param dimensions The widths
     * @return the value
     */
    public static double getValue(DoubleMatrix1D feature, DoubleMatrix1D position,
            DoubleMatrix1D dimensions) {
        if( position.size() != dimensions.size() ||
                position.size() != feature.size()) {
            throw new IllegalArgumentException("feature, position and dimensions" +
                    "must have the same size");
        }

        // is the feature inside the tile (including boundaries)
        for (int i = 0; i < feature.size(); i++) {
            if (!(feature.get(i) >= position.get(i) - dimensions.get(i)/2 &&
                    feature.get(i) <= position.get(i) + dimensions.get(i)/2)) {
                return 0;
            }
        }
        return 1;
    }

    /**
     * Creates a deep copy of the Tile
     * @return The copy
     */
    public Tile copy() {
        return new Tile(position, dimensions);
    }

    // getter and setter
    
    public DoubleMatrix1D getDimensions() {
        return dimensions.copy();
    }

    public void setDimensions(DoubleMatrix1D dimensions) {
        boolean notify = true;
        if (this.dimensions != null && this.dimensions.equals(dimensions))
            notify = false;
        
        this.dimensions = dimensions.copy();

        if (notify)
            this.notifyShapeChanged();
    }

    public void setDimensions(double[] dimensions) {
        this.setDimensions(new DenseDoubleMatrix1D(dimensions));
    }
}
