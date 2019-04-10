/**
 *
 * $Id: TileCodingFactory.java 671 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 671 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network.cmacs;

import cern.jet.random.Uniform;
import java.util.LinkedList;
import java.util.List;

/**
 * TileCodingFactory can be used to let the designer of the experiment think in
 * the traditional way of Tilings. Behind the scene TileCodingFactory's methods
 * return a list of Tiles which can be added to the network.
 *
 * Example:
 * Network net = new Network();
 * net.add(TilCodingFactory.createTilings(new double[][] {
 *      { 0, 1,  5 }, // fst dim.
 *      { 0, 1, 20 }  // snd. dim.
 *  }));
 */

// TODO: create unittests for these functions

public class TileCodingFactory {
    public static List<Tile> createTiling(double[][] config, boolean shiftRandomly) {
        /** For example, if we have an 2 dim. statespace
        * each in [0,1] and we want to divide the
        * first dim. in 5 parts and the second dim. 20 parts
        * then the config. should look like this:
        *
        * <pre>
        *  double[][] config = new double[][] {
        *      { 0, 1,  5 }, // fst dim.
        *      { 0, 1, 20 }  // snd. dim.
        *  };
        * </pre>
        *
        * @param config The configuration
        */
        List<Tile> tiles = new LinkedList<Tile>();
        double[] tilePosition = new double[config.length];
        double[] tileWidths = new double[config.length];
        double[] offset = new double[config.length];
        double[] minTilePosition = new double[config.length];
        double[] maxTilePosition = new double[config.length];

        for (int dim = 0; dim < config.length; dim++) {
            // calculate the widths for a tile in each dimension and an offset
            // in each dimension used for all tiles
            tileWidths[dim] = (config[dim][1] - config[dim][0])
                    / config[dim][2];
            
            // only shift tilings randomly if disired
            if (shiftRandomly)
                offset[dim] = Uniform.staticNextDoubleFromTo(-tileWidths[dim],
                    tileWidths[dim]);
            else
                offset[dim] = 0;
            
            // initialze minPosition, maxPosition
            minTilePosition[dim] = config[dim][0] + tileWidths[dim]/2 + offset[dim];
            maxTilePosition[dim] = config[dim][1] + offset[dim];
        }
        
        tilePosition = minTilePosition.clone();
        while (true) {
           tiles.add(new Tile(tilePosition, tileWidths));
           if (!incrementTilePosition(tilePosition, minTilePosition,
                   maxTilePosition, tileWidths))
               break;
        }
        return tiles;
    }

    private static boolean incrementTilePosition(double[] position,
            final double[] minPosition, final double[] maxPosition,
            final double[] tileWidths) {
       for (int i=position.length-1; i>=0; i--) {
           position[i] += tileWidths[i];
           if (position[i] < maxPosition[i])
               break;
           position[i] = minPosition[i];
           if (i == 0)
               return false;
       }
       return true;
    }

    public static List<Tile> createTilings(double[][] config, int numberOfTilings) {
        List<Tile> tiles = new LinkedList<Tile>();
        boolean shiftRandomly = true;

        for(int i=0; i<numberOfTilings; i++) {
            // Don't shift the tiling if we only create on one
            if (numberOfTilings == 1)
                shiftRandomly = false;

           tiles.addAll(TileCodingFactory.createTiling(config, shiftRandomly));
        }
        return tiles;
    }
}
