/**
 *
 * $Id: GridHash.java 709 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 709 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network.optimization;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.linalg.SeqBlas;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/*
 * GridHash stores lists of type T in a hash. The hash key is calculated via
 * a generated bounding box for an instance of type T. The bounding box is
 * used to check intersection with the grid specified by config to put the
 * instance of type T into the corresponding lists.
 *
 * Example:
 * config = double {
 *      {0, 1, 7} // first dimension: min env coordinate, max env coordinate, divide into n parts
 *      {0, 1, 5} // second dimension: min env coordinate, max env coordinate, divide into n parts
 * }
 * Let's assume a Tile at x=1/7 * 2, y=1/5 * 3
 * We have the following situation:
 * ----------------------------------------------------------
 * |        |       |       |       |       |       |       |
 * |        |       |       |       |       |       |       |
 * |        |       |       |       |       |       |       |
 * ----------------------------------------------------------
 * |        |       |       |       |       |       |       |
 * |        |       |       |       |       |       |       |
 * |        |    ---|---    |       |       |       |       |
 * ----------------------------------------------------------
 * |        |    |  |   |   |       |       |       |       |
 * |        |    ---|---    |       |       |       |       |
 * |        |       |       |       |       |       |       |
 * ----------------------------------------------------------
 * |        |       |       |       |       |       |       |
 * |        |       |       |       |       |       |       |
 * |        |       |       |       |       |       |       |
 * ----------------------------------------------------------
 * |        |       |       |       |       |       |       |
 * |        |       |       |       |       |       |       |
 * |        |       |       |       |       |       |       |
 * ----------------------------------------------------------
 *
 * => Tile will be stored in the hasmap with keys (1,2), (2,2), (1,3), (2,3)
 *
 * Now we can have multiple such Tiles overlapping so that each key in the map
 * holds a list of Tiles. When iterating over the net to generate the
 * featurevector, we just have to calculate the key for the agent's position
 * that is the grid cell's index and iterate through the list of Tiles contained
 * by that grid cell.
 *
 * GridHash allows to store an arbitrary type T. This way we can store not only
 * NetworkNodes in the GridHash but NetworkNodes and the corresponding position
 * in the feature vector of the net for example.
 * GridHash must only be told how to calculate bounding boxes for the type T.
 * This can be done by deriving a calculator from BoundingBoxCalculator.
 * See the example TileAndIndexBoundingBoxCalculator for more info.
 */

public class GridHash<T> {
    public enum RoundingMode {DOWN, UP};
    protected HashMap<Integer, List<T>> map = new HashMap<Integer, List<T>>();
    protected double[][] config;
    protected BoundingBoxCalculator boundingBoxCalculator;
    // minima is the position of the left most point of the world to overlab
    // with the grid, minima is used to translate bounding box positions to the
    // origin of the grid
    private DoubleMatrix1D minima;
    private DoubleMatrix1D maxima;
    private DenseDoubleMatrix1D widths;

    public GridHash(double[][] config,
            BoundingBoxCalculator boundingBoxCalculator) {
        this.config = config.clone();
        this.minima = new DenseDoubleMatrix1D(this.config.length);
        this.maxima = new DenseDoubleMatrix1D(this.config.length);
        this.widths = new DenseDoubleMatrix1D(this.config.length);

        // calculate minimum corner
        for (int dim=0; dim<config.length; dim++) {
            this.minima.set(dim, this.config[dim][0]);
            this.maxima.set(dim, this.config[dim][1] - this.minima.get(dim));
            this.widths.set(dim, (this.config[dim][1] - this.config[dim][0])/
                    this.config[dim][2]);
        }
 
        this.setBoundingBoxCalculator(boundingBoxCalculator);
    }

    public void add(T node) {
        Box boundingBox = this.boundingBoxCalculator.createBoundingBox(node);
        DoubleMatrix1D boxMinima = boundingBox.getPosition();
        DoubleMatrix1D boxMaxima = boundingBox.getPosition();

        // calculate minimum and maximum positions with respect to the grid's
        // origin
        SeqBlas.seqBlas.daxpy(-0.5, boundingBox.getDimensions(), boxMinima);
        SeqBlas.seqBlas.daxpy(-1, this.minima, boxMinima);
        SeqBlas.seqBlas.daxpy(0.5, boundingBox.getDimensions(), boxMaxima);
        SeqBlas.seqBlas.daxpy(-1, this.minima, boxMaxima);

        // check for boundaries in order to calculate indexes right
        for(int dim=0; dim<this.config.length;dim++) {
            if (boxMinima.get(dim) < 0)
                boxMinima.set(dim, 0);
            if (boxMaxima.get(dim) < 0)
                boxMaxima.set(dim, 0);

            if (boxMaxima.get(dim) > this.maxima.get(dim))
                boxMaxima.set(dim, this.maxima.get(dim));
            if (boxMinima.get(dim) > this.maxima.get(dim))
                boxMinima.set(dim, this.maxima.get(dim));
        }
        
        for (int[] index : createIndexesForInterval(
                this.createIndex(boxMinima, RoundingMode.DOWN),
                this.createIndex(boxMaxima, RoundingMode.UP))) {
            if (this.map.containsKey(Arrays.hashCode(index))) {
                this.map.get(Arrays.hashCode(index)).add(node);
            }
            else {
                List<T> list = new LinkedList<T>();
                list.add(node);
                this.map.put(Arrays.hashCode(index), list);
            }
        }
    }

    public void remove(T node) {
        // TODO: implement this
    }

    public List<T> getNodes(DoubleMatrix1D position) {
        DoubleMatrix1D pos = position.copy();
        SeqBlas.seqBlas.daxpy(-1, this.minima, pos);

        int[] index = this.createIndex(pos);
        if (this.map.containsKey(Arrays.hashCode(index)))
            return this.map.get(Arrays.hashCode(index));
        else
            return new LinkedList<T>();
    }


    // low level helper functions

    /*
     * Creates a grid cell's n-dimensional index for a given position, position
     * has to contain positive values for each dimension
     */
    protected int[] createIndex(final DoubleMatrix1D position) {
        int[] index = new int[position.size()];
        
        for (int dim = 0; dim < config.length; dim++) {
            index[dim] = (int) (position.get(dim) / this.widths.get(dim));
            // this can happen if the agent is located exactly at the maximal
            // allowed position in that particular dimension
            if (index[dim] >= this.config[dim][2])
                index[dim] = index[dim] - 1;
        }
        return index.clone();
    }

    protected int[] createIndex(final DoubleMatrix1D position, RoundingMode jump) {
        int[] index = new int[position.size()];
        double tmp = 0;
        
        for (int dim = 0; dim < config.length; dim++) {
            index[dim] = (int) (position.get(dim) / this.widths.get(dim));
            // this can happen if the agent is located exactly at the maximal
            // allowed position in that particular dimension
            tmp = position.get(dim)/this.widths.get(dim);
            if ((tmp - (int) tmp) == 0) {
                if (jump == RoundingMode.DOWN && index[dim] > 0)
                    index[dim] = index[dim] - 1;
                else if(jump == RoundingMode.UP && index[dim] < this.config[dim][2]-1)
                    index[dim] = index[dim] + 1;
            }

            if (index[dim] >= this.config[dim][2])
                index[dim] = index[dim] - 1;
        }
        return index.clone();
    }

    /*
     * Creates a range of n-dimensional indexes for a given position range.
     */
    protected static List<int[]> createIndexesForInterval(final int[] minIndex,
            final int[] maxIndex) {
        List<int[]> indexes = new LinkedList<int[]>();
        int[] index = minIndex.clone();

        while (true) {
           indexes.add(index.clone());
           if (!incrementIndex(index, minIndex, maxIndex))
               break;
        }
//        indexes.add(maxIndex);
        return indexes;
    }

    protected static boolean incrementIndex(int[] index,
            final int[] minIndex, final int[] maxIndex) {
       for (int i=index.length-1; i>=0; i--) {
           index[i] += 1;
           if (index[i] <= maxIndex[i])
               break;
           index[i] = minIndex[i];
           if (i == 0)
               return false;
       }
       return true;
    }

    // setter and getter
    
    public BoundingBoxCalculator getBoundingBoxCalculator() {
        return boundingBoxCalculator;
    }

    public void setBoundingBoxCalculator(BoundingBoxCalculator boundingBoxCalculator) {
        this.boundingBoxCalculator = boundingBoxCalculator;
    }
}