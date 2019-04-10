package org.hswgt.teachingbox.core.rl.datastructures;

import java.util.Collection;
import java.util.Vector;

import org.hswgt.teachingbox.core.rl.env.State;
/**
 * An StateSet holds all possible states in an environment
 */
public class StateSet extends Vector<State> implements java.io.Serializable {

    private static final long serialVersionUID = -8585889922654198048L;

    /**
     * 
     */
    public StateSet() {
        super();
    }

    /**
     * @param otherStateSet A State set
     */
    public StateSet(Collection<? extends State> otherStateSet) {
        super(otherStateSet);
    }	
}
