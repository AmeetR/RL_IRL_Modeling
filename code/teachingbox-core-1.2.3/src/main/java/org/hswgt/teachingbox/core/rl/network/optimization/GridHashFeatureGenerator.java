/**
 *
 * $Id: GridHashFeatureGenerator.java 709 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 709 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network.optimization;

import org.hswgt.teachingbox.core.rl.network.*;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import org.hswgt.teachingbox.core.rl.feature.FeatureFunctionObserver;

/*
 * GridHashFeatureGenerator listens to the feature function and adds NetworkNodes
 * with corresponding feature vector positions to a GridHash instance.
 */

public class GridHashFeatureGenerator extends FeatureGenerator
        implements NetworkNodeObserver, FeatureFunctionObserver {

    protected GridHash<NodeAndIndex> gridHash;

    public GridHashFeatureGenerator(double[][] config,
            BoundingBoxCalculator boundingBoxCalculator) {
        this.gridHash = new GridHash<NodeAndIndex>(config, boundingBoxCalculator);
    }

    public GridHashFeatureGenerator(GridHash gridHash) {
        this.gridHash = gridHash;
    }

    public DoubleMatrix1D getFeatureVector(DoubleMatrix1D state) {
        DoubleMatrix1D featureVector = new DenseDoubleMatrix1D(this.net.size());
        for (NodeAndIndex node : this.gridHash.getNodes(state))
            featureVector.set(node.index, node.node.getValue(state));
        return featureVector;
    }

    public void positionChanged(NetworkNode node) {
    // TODO: implement this
//        NodeAndIndex nodeAndIndex = new NodeAndIndex(node, this.net.net.indexOf(node));
//        this.gridHash.remove(nodeAndIndex);
//        this.gridHash.add(nodeAndIndex);
    }

    public void shapeChanged(NetworkNode node) {
        // TODO: implement this
    }

    public void updateFeatureVectorSize(int newSize) {
        // updateFeatureAdded and updateFeatureRemoved handle this
    }

    public void updateFeatureAdded(int index) {
        NodeAndIndex nodeAndIndex = new NodeAndIndex(this.net.getNet().get(index),
                index);
        this.gridHash.add(nodeAndIndex);
    }

    public void updateFeatureRemoved(int index) {
        NodeAndIndex nodeAndIndex = new NodeAndIndex(this.net.getNet().get(index),
                index);
        this.gridHash.remove(nodeAndIndex);
    }

    public void setNet(Network net) {
        super.setNet(net);
        net.addObserver(this);
    }
}
