/**
 *
 * $Id: Network.java 669 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 669 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.network;

import org.hswgt.teachingbox.core.rl.network.adaption.DoNothing;
import org.hswgt.teachingbox.core.rl.network.adaption.AdaptionRule;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import org.hswgt.teachingbox.core.rl.feature.FeatureCache;
import org.hswgt.teachingbox.core.rl.feature.FeatureFunction;

//import org.apache.log4j.Logger;

 /* Network holding network nodes. This network can be used for tilecoding as
  * well as for RBFs. It's even possible to combine several different nodes in
  * one and the same network e.g. use tilecoding in combination with RBFs.
  * Additionally you can use an AdaptionRule to specify how to change the
  * network dynamically thus making the network adaptive. In addition to that
  * this rule can even change dynamically during the experiment because you can
  * set the AdaptionRule at runtime.
  */

public class Network extends FeatureFunction implements Iterable<NetworkNode>,
        Serializable {

    private static final long serialVersionUID = 8645021641190593898L;
    /**
     * The network
     */
    protected LinkedList<NetworkNode> net = new LinkedList<NetworkNode>();

    // adaptionRule specifies how to change the network dynamically that is when
    // to add, delete or reshape network nodes, default is to doesn't change the
    // network at all
    protected AdaptionRule adaptionRule = new DoNothing();
    // featureGenerator generates the feature vector to cache/normalize/return
    // in Network.getFeatures, default is to iterate through all nodes
    protected FeatureGenerator featureGenerator = new IterateThroughAllNodes(this);
    // isNormalized is used to determine if the network should use normalized
    // features
    protected boolean isNormalized = false;

    // a cache for already calculated feature vectors
    protected FeatureCache cache = new FeatureCache(10);
    protected boolean cacheEnabled = false;

//    private final static Logger log4j = Logger.getLogger("Network");

    /**
     * Constructor
     */
    public Network() {
    }

    /**
     * Constructor
     * @param adaptationRule The adaptation rule
     */
    public Network(AdaptionRule adaptationRule) {
        this.setAdaptionRule(adaptationRule);
    }

    /**
     * Constructor
     * @param featureGenerator The feature generator
     */
    public Network(FeatureGenerator featureGenerator) {
        this.setFeatureGenerator(featureGenerator);
    }

    /**
     * Constructor
     * @param adaptationRule The adaptation rule
     * @param featureGenerator The feature generator 
     */
    public Network(AdaptionRule adaptationRule, FeatureGenerator featureGenerator)
    {
        this.setAdaptionRule(adaptationRule);
        this.setFeatureGenerator(featureGenerator);
    }

    /**
     * Copy Constructor
     *
     * @param other Any Network to copy
     */
    public Network(final Network other)
    {
        super(other);
        this.net = new LinkedList<NetworkNode>(other.net);
        this.setAdaptionRule(other.getAdaptionRule());
        this.setFeatureGenerator(other.getFeatureGenerator());
        this.setIsNormalized(other.isNormalized);
        this.setCacheEnabled(other.cacheEnabled);
    }

    public void add(final NetworkNode node)
    {
        net.add(node);
        if (node.getNet() == null)
            node.setNet(this);

        this.notifyFeatureAdded(this.getFeatureVectorSize(),
                this.net.size()-1);
    }

    public <U extends NetworkNode, T extends Collection<U>> void add(
            final T nodes)
    {
        for (NetworkNode node: nodes)
            this.add(node);
    }

    public void remove(final NetworkNode node) {
        int index = this.net.indexOf(node);
        net.remove(node);
        this.notifyFeatureRemoved(this.getFeatureVectorSize(), index);
    }

    /**
     * Returns the number of elements in this network.
     *
     * @return the number of elements in this network.
     */
    public int size()
    {
        return net.size();
    }

    public DoubleMatrix1D getFeatures(final DoubleMatrix1D state) {
        // check if we already calculated the features
        DoubleMatrix1D cached = cache.get(state);
        if(cacheEnabled && cached != null && cached.size() == this.size() )
            return cached;

        // do not apply adaption rule if the cache contains s because the rule
        // has been applied already
        adaptionRule.changeNet(state);

        DoubleMatrix1D feat = new SparseDoubleMatrix1D(net.size());
        
        feat.assign(this.featureGenerator.getFeatureVector(state));

        //log4j.debug("Network size: " + net.size());

        if(isNormalized) {
            double sum = feat.zSum();
            
            if (sum != 0) {
                IntArrayList indexList = new IntArrayList();
                DoubleArrayList valueList = new DoubleArrayList();
                feat.getNonZeros(indexList, valueList);

    //            log4j.debug(indexList.size()+ " nonzeros" );

                for(int i=0; i<indexList.size(); i++) {
                    int indexpos = indexList.getQuick(i);
                    double value = valueList.getQuick(i);
                    feat.set(indexpos, value/sum );
                }
            }
        }

        if (this.cacheEnabled)
            cache.put(state, feat);

        return feat;
    }

    public int getFeatureVectorSize() {
        return net.size();
    }

    public FeatureFunction copy() {
        return new Network(this);
    }

    public boolean hasVariableFeatureVectorSize() {
        return true;
    }

    public Iterator<NetworkNode> iterator() {
        return net.iterator();
    }

    // getter and setter

    public boolean isNormalized() {
        return isNormalized;
    }

    public void setIsNormalized(boolean isNormalized) {
        this.isNormalized = isNormalized;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public AdaptionRule getAdaptionRule() {
        return this.adaptionRule;
    }

    public void setAdaptionRule(AdaptionRule adaptionRule) {
        // do not set hasVariableFeatureSize to true cause the adaption rule
        // may only change the shape of the nodes thus not changing the feature
        // size
        this.adaptionRule = adaptionRule;
        if (this.adaptionRule.getNet() == null)
            this.adaptionRule.setNet(this);
    }

    public FeatureGenerator getFeatureGenerator() {
        return this.featureGenerator;
    }

    public void setFeatureGenerator(FeatureGenerator featureGenerator) {
        this.featureGenerator = featureGenerator;
        if (this.featureGenerator.getNet() == null)
            this.featureGenerator.setNet(this);
    }

    public LinkedList<NetworkNode> getNet() {
        // TODO: clone the net? Would it be too much overhead?
        return net;
    }
}
