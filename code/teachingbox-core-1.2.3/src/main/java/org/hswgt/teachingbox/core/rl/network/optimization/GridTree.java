package org.hswgt.teachingbox.core.rl.network.optimization;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.hswgt.teachingbox.core.rl.network.NetworkNode;

// TODO: In each function check if dimensions do match!
// Can be optimzed by using a linear list instead of a HashMap

/** For example, if we have an 2 dim. statespace
* each with width 1 and we want to divide the
* first dim. in 5 parts and the second dim. 20 parts
* then the config. should look like this:
*
* <pre>
*  double[][] config = new double[][] {
*      { 1,  5 }, // fst dim.
*      { 1, 20 }  // snd. dim.
*  };
* </pre>
*/

public class GridTree {
    protected HashMap<Integer[], GridTree> subGrid = new HashMap<Integer[],
            GridTree>();
    protected List<NetworkNode> nodes = new LinkedList<NetworkNode>();
    protected double[][] config;
    protected int maxNetworkNodes;

    // TODO: call this this constructor from within a constructor which gets
    // a configuration which looks like the TileCodingFactory config
    public GridTree(int maxNetworkNodes, double[][] config) {
        this.maxNetworkNodes = maxNetworkNodes;
        this.config = config.clone();
    }

    protected void createSubGrid(int maxNetworkNodes, double[][] config) {
        Integer[] gridCellIndex = new Integer[config.length];
        int[] minGridCellIndex = new int[config.length];
        int[] maxGridCellIndex = new int[config.length];
        double[][] subGridConfig = config.clone();
        
        // initialize minGirdCellIndex and maxGirdCellIndex and create subTreeConfig
        for (int i=0; i<config.length; i++) {
            minGridCellIndex[i] = 0;
            maxGridCellIndex[i] = (int) (config[i][1] - 1);
            subGridConfig[i][0] = this.config[i][0]/this.config[i][1];
        }

        while (true) {
            this.subGrid.put(gridCellIndex, new GridTree(this.maxNetworkNodes,
                   subGridConfig));
           if (!incrementIndex(gridCellIndex, maxGridCellIndex,
                   minGridCellIndex))
               break;
        }
    }

    protected static boolean incrementIndex(Integer[] index,
            final int[] maxIndex, final int[] minIndex) {
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

    public void add(NetworkNode networkNode) {
        this.add(networkNode, networkNode.getPosition().toArray());
    }

    protected void add(NetworkNode networkNode, double[] position) {
        Integer[] nodeIndex = this.createNodeIndex(position);

        // check if we already have subGrid
        if (this.subGrid == null)
            this.createSubGrid(this.maxNetworkNodes, this.config);

        GridTree subGrid = this.subGrid.get(nodeIndex);
        if (subGrid.nodes.size() < subGrid.maxNetworkNodes)
            subGrid.nodes.add(networkNode);
        else {
            double[][] subGridConfig = config.clone();

            // calculate configuration for new sub-grid
            for (int i=0; i<config.length; i++)
                subGridConfig[i][0] = this.config[i][0]/this.config[i][1];

            // remove all network nodes from this grid cell and put them into
            // the new sub-grid
            subGrid.createSubGrid(this.maxNetworkNodes, this.config);

            // calculate position relative to new subgrid
            // TODO: this will not work with negative positions, position of agent has
            // to be relative to the grid
            for (int i=0; i<config.length; i++)
                position[i] -= nodeIndex[i]*this.config[i][0];

            for (NetworkNode node : this.nodes)
                subGrid.add(node, position);
            subGrid.add(networkNode, position);
            this.nodes.clear();
        }
    }
    // TODO: this will not work with negative positions, position of agent has
    // to be relative to the grid
    protected Integer[] createNodeIndex(double[] position) {
        Integer[] index = new Integer[position.length];
        
        // calculate the widths for a grid cell in each dimension
        for (int dim = 0; dim < config.length; dim++)
            index[dim] = (int) (position[dim] / this.config[dim][0]);

        return index.clone();
    }

    public List<NetworkNode> getNodes(double[] position) {
        Integer[] nodeIndex = this.createNodeIndex(position);
        GridTree subGrid = this.subGrid.get(nodeIndex);

        if (subGrid.nodes.size() == 0)
            subGrid.getNodes(position);

        return subGrid.nodes;
    }
}
