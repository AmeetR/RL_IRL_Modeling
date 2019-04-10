/**
 *
 * $Id: NumberOfDifferenceNetworkNodesAverager.java 751 2010-06-21 11:38:29Z twanschik $
 *
 * @version   $Rev: 751 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-06-21 13:38:29 +0200 (Mo, 21 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.experiment;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.feature.FeatureFunctionObserver;
import org.hswgt.teachingbox.core.rl.network.Network;

/**
 * Averages the number of network nodes added/deleted per step.
 */

public class NumberOfDifferenceNetworkNodesAverager extends ScalarAverager
    implements FeatureFunctionObserver {

    protected Network net;
    protected int oldNumberOfNodes, newNumberOfNodes = 0;

    public NumberOfDifferenceNetworkNodesAverager(int maxSteps, String configString, Network net) {
        super(maxSteps, configString);
        this.setNet(net);

        // initialize counts
        this.newNumberOfNodes = this.net.getFeatureVectorSize();
        this.oldNumberOfNodes = this.newNumberOfNodes;
    }

    /**
     * The constructor
     * @param maxSteps the maximum steps per episode
     * @param configString the config string for plotting
     */
    public NumberOfDifferenceNetworkNodesAverager(int maxSteps, String configString) {
        super(maxSteps, configString);
    }

    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean terminalState) {
        System.out.println("Nodes added: " + (this.newNumberOfNodes - this.oldNumberOfNodes));
        this.updateAverage(this.newNumberOfNodes - this.oldNumberOfNodes);
        this.oldNumberOfNodes = this.newNumberOfNodes;
    }
    
    public Network getNet() {
        return net;
    }

    public void setNet(Network net) {
        this.net = net;
        this.net.addObserver(this);
    }

    public void updateFeatureVectorSize(int newSize) {
    }

    public void updateFeatureAdded(int index) {
        this.newNumberOfNodes++;
    }

    public void updateFeatureRemoved(int index) {
        this.newNumberOfNodes--;
    }
}