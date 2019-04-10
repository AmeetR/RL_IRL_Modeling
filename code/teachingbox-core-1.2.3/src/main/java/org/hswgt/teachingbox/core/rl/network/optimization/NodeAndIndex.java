/**
 *
 * $Id: NodeAndIndex.java 709 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 709 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.network.optimization;

import org.hswgt.teachingbox.core.rl.network.NetworkNode;

/**
 * Structure storing a network node and the corresponding index of the net's
 * global list. This way we do not have to call indexOf on the net's list
 * resulting in better performance.
 */
 public class NodeAndIndex {
    protected NetworkNode node;
    protected int index;

    public NodeAndIndex(NetworkNode node, int index) {
        this.node = node;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public NetworkNode getNode() {
        return node;
    }

    public void setNode(NetworkNode node) {
        this.node = node;
    }
}