/**
 *
 * $Id: NetworkNode.java 669 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 669 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */

package org.hswgt.teachingbox.core.rl.network;

import java.io.Serializable;

import org.hswgt.teachingbox.core.rl.tools.Copyable;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import java.util.LinkedList;
import java.util.List;

// Abstract class representing a node in a network. Currently all nodes hold a
// position so put it into the NetworkNode itself.

public abstract class NetworkNode implements Copyable<NetworkNode>, Serializable {
    private static final long serialVersionUID = -1375335252449897775L;
    
    protected DoubleMatrix1D position;
    // dimension indicates how much dimensions this node covers
    protected int dimension = 0;

    protected List<NetworkNodeObserver> observers =
            new LinkedList<NetworkNodeObserver>();
    protected Network net = null;

    // abstract method to implement
    public abstract double getValue(DoubleMatrix1D state);

    public double getValue(double[] state) {
        return getValue(new DenseDoubleMatrix1D(state));
    }

    public DoubleMatrix1D getPosition() {
        return position.copy();
    }

    public void setPosition(DoubleMatrix1D position) {
        boolean notify = true;
        if (this.position != null && this.position.equals(position))
            notify = false;

        this.position = position.copy();

        if (notify)
            this.notifyShapeChanged();
    }

    public void setPosition(double[] position) {
        this.setPosition(new DenseDoubleMatrix1D(position));
    }

    public void notifyPositionChanged() {
        for (NetworkNodeObserver observer: observers)
            observer.positionChanged(this);
    }

    // classes derived from NetworkNode should call notifyShapeChanged
    // if the change the shape. For RBFs this should be called if sigma changes!
    public void notifyShapeChanged() {
        for (NetworkNodeObserver observer: observers)
            observer.shapeChanged(this);
    }

    /**
     * Attaches an observer to this
     *
     * @param obs The observer to attach
     */
    public void addObserver(final NetworkNodeObserver obs)
    {
        if( !this.observers.contains(obs) )
            this.observers.add(obs);
    }

    /**
     * Remove an observer from this
     * @param obs The observer to detach
     */
    public void removeObserver(final NetworkNodeObserver obs)
    {
        this.observers.remove(obs);
    }

    public Network getNet() {
        return net;
}

    public void setNet(Network net) {
        this.net = net;
    }

    public int getDimension() {
        return dimension;
    }
}
