/**
 * 
 * $Id: NoNodeNearby.java 669 2010-06-14 14:53:38Z twanschik $
 * 
 * @version $Rev: 669 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 * 
 */

package org.hswgt.teachingbox.core.rl.network.adaption;

import java.io.Serializable;

import org.hswgt.teachingbox.core.rl.network.*;
import cern.colt.matrix.DoubleMatrix1D;

/**
 * This is an adaptive rule which adds network nodes to the network if there is
 * no node near to the current operation point. You only have to specify a node
 * which will be used as a sample to copy.
 * 
 *  Example:
    <pre>
    // create a new adaptive net containing Tiles. Add a one dimensional Tile
    // with width = 1 whenever no node is nearby.
    // AdaptiveRule rule = new NoNodeNearby(new Tile(new double[] {0.5}, new double[] {1}));
    // Network net = new Network(rule);
    // get the value at 1.0
    DoubleMatrix1D feat = net.getFeatures(new DenseDoubleMatrix1D(new double[] {1.0}));
    </pre>
 */
public class NoNodeNearby extends AdaptionRule implements Serializable
{

    private static final long serialVersionUID = -4553026366109644315L;
    /**
     * The node to create a copy of. This instance is beeing used as a
     * configuration for new nodes.
     * Example1:
     * nodeToCopy = new Tile(new double[] {0}, new double[] {1});
     * This will result in adding new one-dimensional Tiles to the network with
     * width 1.
     * Example2:
     * nodeToCopy = new RadialBasisFunction(new double[] {0, 0}, new double[] {1, 2});
     * This will result in adding new two-dimensional RadialBasisFunctions to
     * the network with sigma = {1, 2}.
     */
    protected NetworkNode nodeToCopy;
    /*
     * inScopeCalculator should be used to define what it means for a node to be
     * in the scope of the current opration points.
     * For example, for RadialBasisFunction we use the value of the RBF to decide
     * if the RBF is nearby the current operation point (agent's position).
     *
     * Default is to use the squared euclidean distance (squared in order to save
     * computation time)
     */
    protected InScopeCalculator inScopeCalculator = new EuclideanDistanceSquared();

    /**
     * Default Constructor
     * 
     * @param nodeToCopy sample node used as a configuration for newly created nodes
     */
    public NoNodeNearby(final NetworkNode nodeToCopy)
    {
        this.nodeToCopy = nodeToCopy.copy();
    }

    /**
     * Default Constructor
     *
     * @param nodeToCopy sample node used as a configuration for newly created nodes
     * @param inScopeCalculator InScopeCalculator to use
     */
    public NoNodeNearby(final NetworkNode nodeToCopy,
            final InScopeCalculator inScopeCalculator)
    {
        this.nodeToCopy = nodeToCopy.copy();
        this.inScopeCalculator = inScopeCalculator;
    }
    
    /**
     * Returns true if a node is in scope of the feature
     * @param feat The feature to test
     * @return true if a node is in scope of the feature
     */
    protected boolean isNodeInScope(final DoubleMatrix1D feat)
    {
        // TODO: add optimization using a GridHash here if possible
        // (for a normalized RBF net, it's not possible)
        for( NetworkNode node : net ) {
            if( this.inScopeCalculator.isInScope(node, feat))
                return true;
        }
        return false;
    }

    /*
     * Default is to add a node if no one is close enough because minTdError=0.
     */
    public void changeNet(final DoubleMatrix1D feat) {
        if(!isNodeInScope(feat)) {
            // create new node with the same parameters as nodeToCopy and set
            // new position
            NetworkNode newNode = this.nodeToCopy.copy();
            newNode.setPosition(feat);
            this.net.add(newNode);
        }
    }

    // getter and setter

    public NetworkNode getNodeToCreate() {
        return nodeToCopy.copy();
    }

    public void setNodeToCreate(NetworkNode nodeToCreate) {
        this.nodeToCopy = nodeToCreate.copy();
    }

    public InScopeCalculator getInScopeCalculator() {
        return inScopeCalculator;
    }

    public void setInScopeCalculator(InScopeCalculator inScopeCalculator) {
        this.inScopeCalculator = inScopeCalculator;
    }
}
