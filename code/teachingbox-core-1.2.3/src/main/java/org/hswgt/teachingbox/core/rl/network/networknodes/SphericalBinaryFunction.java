/**
 *
 * $Id: SphericalBinaryFunction.java 675 2010-06-11 08:45:17Z twanschik $
 *
 * @version $Rev: 675 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-11 10:45:17 +0200 (Fr, 11 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network.networknodes;

import java.io.Serializable;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.linalg.SeqBlas;
import org.hswgt.teachingbox.core.rl.network.NetworkNode;

/**
 * Represents a spherical binary function which returns 1 if x falls into the
 * sphere and 0 otherwise. It is similar to a Tile, only a different shape is
 * used.
 */

public class SphericalBinaryFunction extends NetworkNode implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 6669374847856748297L;
    protected double radius;

    /**
     * Constructor
     * Creates a new Tile with center position and widths dimensions
     * @param position Center
     * @param radius radius of the sphere
     */
    public SphericalBinaryFunction(DoubleMatrix1D position, double radius) {
        this.setPosition(position);
        this.setRadius(radius);
    }

    /**
     * Constructor
     * Creates a new Tile with center position and widths dimensions
     * @param position Center
     * @param radius radius of the sphere
     */
    public SphericalBinaryFunction(double[] position, double radius) {
        this(new DenseDoubleMatrix1D(position), radius);
    }

    /**
     * Copy Constructor
     * 
     * @param other The spherical binary function
     */
    public SphericalBinaryFunction(final SphericalBinaryFunction other) {
        this(other.position, other.radius);
    }

    /**
     * returns 1 if state is inside the spher otherwise 0
     * @param feat The input
     */
    public double getValue(final DoubleMatrix1D feat) {
        return SphericalBinaryFunction.getValue(feat, this.position,
                this.radius);
    }

    /**
     * returns 1 if state is inside the sphere otherwise 0
     * @param feat The input
     * @param position The location of the center
     * @param radius The radius
     * @return the value
     */
    public static double getValue(final DoubleMatrix1D feat,
            DoubleMatrix1D position, double radius)
    {
        if( (position.size() != feat.size())) {
            throw new IllegalArgumentException("state and position must have the" +
                    "same size");
        }

        DoubleMatrix1D difference = position.copy();
        SeqBlas.seqBlas.daxpy(-1, feat, difference);
        return (difference.zDotProduct(difference) <= radius*radius) ? 1.0 : 0.0;
    }

    /**
     * Creates a deep copy of the Tile
     * @return The copy
     */
    public SphericalBinaryFunction copy() {
        return new SphericalBinaryFunction(this.position, this.radius);
    }

    // getter and setter

    public void setRadius(double radius) {
        this.radius = radius;
        this.notifyShapeChanged();
    }

    public double getRadius() {
        return this.radius;
    }
}
