/**
 *
 * $Id: Box.java 709 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 709 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network.optimization;

import cern.colt.matrix.DoubleMatrix1D;


/*
 * A Box consists of an n-dimensional position and widhts in each dimension.
 * Box should be used for bounding box calculations in feature generators.
 */

public class Box {
    protected DoubleMatrix1D position;
    protected DoubleMatrix1D dimensions;
    protected int dimension;
    
    public Box(DoubleMatrix1D position, DoubleMatrix1D dimensions) {
        if( position.size() != dimensions.size() ) {
	        throw new IllegalArgumentException("position and dimension must" +
                        "have the same size");
	}
        
        this.setPosition(position);
        this.setDimensions(dimensions);
        this.dimension = this.position.size();
    }

    private void setPosition(DoubleMatrix1D position) {
        this.position = position.copy();
    }

    public DoubleMatrix1D getPosition() {
        return position.copy();
    }

    private void setDimensions(DoubleMatrix1D dimensions) {
        this.dimensions = dimensions.copy();
    }

    public DoubleMatrix1D getDimensions() {
        return dimensions.copy();
    }

    public int getDimension() {
        return this.dimension;
    }
}
