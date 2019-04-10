/**
 *
 * $Id: TdErrorExceedsThreshold.java 671 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 671 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.network.adaption;

import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.linalg.SeqBlas;
import java.util.Vector;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.env.UpdateStruct;
import org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver;
import org.hswgt.teachingbox.core.rl.learner.ErrorsObserver;
import org.hswgt.teachingbox.core.rl.network.NetworkNode;

/**
 * This is an adaptive rule which adds network nodes to the network if there is
 * no node near to the current operation point and the currently
 * maximal absolute td-error is bigger than a minimal threshold. You only have
 * to specify a node which will be used as a sample to copy.
 *
 * Do not forget to register this adaption rule as an ExperimentObserver before
 * registering it as a ErrorsObserver!
 *
 *  Example:
    <pre>
    // create a new adaptive net containing Tiles. Add a one dimensional Tile
    // with width = 1 whenever no node is nearby and the td-error is bigger than
    // 0.1
    // AdaptiveRule rule = new TdErrorExceedsThreshold(new Tile(new double[] {0.5},
 *  //     new double[] {1}));
    // rule.setMinTdError(0.1);
    // Network net = new Network(rule);
 *  // ...
 *  // experiment.addObserver(rule);
 *  // learner.addObserver(rule);
    // get the value at 1.0
    DoubleMatrix1D feat = net.getFeatures(new DenseDoubleMatrix1D(new double[] {1.0}));
    </pre>
 */

public class TdErrorExceedsThreshold extends NoNodeNearby implements
        ExperimentObserver, ErrorsObserver {

    /**
     * The current biggest absolute td-error has to be grater than minTdError in
     * order to add a node. Default minTdError is zero such that a node will be
     * added if there is no node nearby ignoring the td-error at all.
     */
    protected double minTdError = 0;

    // numberOfNodesToAdd specifies how many network nodes should be added backwards
    // along the path if no node is nearby
    protected Vector<UpdateStruct> updateParams = new Vector<UpdateStruct>();
    protected int numberOfNodesToAdd = 1;
    
    /**
     * Default Constructor
     *
     * @param nodeToCreate sample node used as a configuration for newly created nodes
     */
    public TdErrorExceedsThreshold(final NetworkNode nodeToCreate)
    {
        super(nodeToCreate);
    }

    /**
     * Default Constructor
     *
     * @param nodeToCreate sample node used as a configuration for newly created nodes
     * @param minTdError minimal td-error to use
     */
    public TdErrorExceedsThreshold(final NetworkNode nodeToCreate,
            double minTdError) {
        super(nodeToCreate);
        this.setMinTdError(minTdError);
    }

    /**
     * Default Constructor
     *
     * @param nodeToCreate sample node used as a configuration for newly created nodes
     * @param distanceCalculator InScopeCalculator to use
     */
    public TdErrorExceedsThreshold(final NetworkNode nodeToCreate,
            final InScopeCalculator distanceCalculator) {
        super(nodeToCreate, distanceCalculator);
    }

    /**
     * Default Constructor
     *
     * @param nodeToCreate sample node used as a configuration for newly created nodes
     * @param distanceCalculator InScopeCalculator to use
     * @param minTdError minimal td-error to use
     */
    public TdErrorExceedsThreshold(final NetworkNode nodeToCreate,
            final InScopeCalculator distanceCalculator, double minTdError) {
        super(nodeToCreate, distanceCalculator);
        this.setMinTdError(minTdError);
    }

    /*
     * Do nothing in changeNet, this adaption rule will add nodes as soon as
     * it has the information of the td-errors, so we have to adapt the net in
     *
     */
    public void changeNet(final DoubleMatrix1D feat) {
    }

    public void updateErrors(DoubleMatrix1D tderrors, IntArrayList tderrorsIndexes,
            State state, Action action, State nextState, Action nextAction,
            double reward, boolean isTerminalState) {
        
        this.updateParams.add(new UpdateStruct(state, action, nextState,
                nextAction, reward, isTerminalState));
        if (this.updateParams.size() > this.getNumberOfNodesToAdd())
            this.updateParams.remove(0);

        // use only tderrors which correspond to the state-action pair currently
        // taken
        DoubleMatrix1D currentTdErrors = tderrors.like(tderrorsIndexes.size());
        if (tderrorsIndexes.size() > 0) {
            for (int i=0; i<tderrorsIndexes.size(); i++)
                currentTdErrors.set(i, tderrors.get(tderrorsIndexes.get(i)));
        }

        // somehow SeqBlas.seqBlas.idamax(lastTdErrors) returns -1 even if
        // lastTdErrors isn't empty (size = 1, value = 0, for example) so we
        // have to code around this bug
        double value = 0;
        int maxIndex = SeqBlas.seqBlas.idamax(currentTdErrors);
        value = (maxIndex == -1) ? 0 : currentTdErrors.get(maxIndex);
        if(Math.abs(value) > minTdError || reward != 0) {
            Vector<UpdateStruct> copy = (Vector<UpdateStruct>) this.updateParams.clone();
            for (int i=copy.size()-1; i>=0; i--) {
                UpdateStruct struct = this.updateParams.get(i);
                super.changeNet(struct.getState());
                this.updateParams.remove(i);
            }
        }
    }

    public double getMinTdError() {
        return minTdError;
    }

    public void setMinTdError(double minTdError) {
        this.minTdError = minTdError;
    }

    public int getNumberOfNodesToAdd() {
        return numberOfNodesToAdd;
    }

    public void setNumberOfNodesToAdd(int numberOfNodesToAdd) {
        if (numberOfNodesToAdd < 1)
            numberOfNodesToAdd = 1;
        this.numberOfNodesToAdd = numberOfNodesToAdd;
    }

    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean isTerminalState) {
    }

    public void updateNewEpisode(State initialState) {
        this.updateParams.clear();
    }

    public void updateExperimentStop() {
    }

    public void updateExperimentStart() {
        this.updateParams.clear();
    }
}
