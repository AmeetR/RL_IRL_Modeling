/**
 *
 * $Id: NumberOfActiveNetworkNodesAverager.java 696 2010-06-21 11:38:29Z twanschik $
 *
 * @version   $Rev: 696 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-06-21 13:38:29 +0200 (Mo, 21 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.experiment;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.network.Network;

/**
 * Averages the number of active network nodes.
 * Note it only makes sense for non-normalized networks because for normalized
 * networks all nodes are active per definition i.e. calculating average for
 * active nodes makes no sense.
 */

public class NumberOfActiveNetworkNodesAverager extends ScalarAverager {

    protected Network net;
    protected boolean denormalizeNet = false;

    public NumberOfActiveNetworkNodesAverager(int maxSteps, String configString,
            Network net) {
        super(maxSteps, configString);
        this.setNet(net);
    }

    public NumberOfActiveNetworkNodesAverager(int maxSteps, String configString,
            Network net, boolean denormalizeNet) {
        super(maxSteps, configString);
        this.setNet(net);
        this.setDenormalizeNet(denormalizeNet);
    }

    /**
     * The constructor
     * @param maxSteps the maximum steps per episode
     * @param configString the config string for plotting
     */
    public NumberOfActiveNetworkNodesAverager(int maxSteps, String configString) {
        super(maxSteps, configString);
    }

    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean terminalState) {

        // deactivate normalization if desired
        boolean normalizedNetState = this.net.isNormalized();
        if (this.denormalizeNet)
            this.net.setIsNormalized(false);

        DoubleMatrix1D features = this.net.getFeatures(state);
        IntArrayList indexList = new IntArrayList();
        DoubleArrayList valueList = new DoubleArrayList();
        features.getNonZeros(indexList, valueList);
//        System.out.println(indexList.size());
        this.updateAverage(indexList.size());

        //set normalization back to its original state
        if (this.denormalizeNet)
            this.net.setIsNormalized(normalizedNetState);
    }
    
    public Network getNet() {
        return net;
    }

    public void setNet(Network net) {
        this.net = net;
    }

    public boolean isDenormalizeNet() {
        return denormalizeNet;
    }

    public void setDenormalizeNet(boolean denormalizeNet) {
        this.denormalizeNet = denormalizeNet;
    }
}