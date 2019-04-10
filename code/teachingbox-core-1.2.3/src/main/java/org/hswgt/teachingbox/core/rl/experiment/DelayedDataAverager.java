package org.hswgt.teachingbox.core.rl.experiment;

import java.util.Vector;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * Delegates to an arbitrary averager starting delay steps after an episode has
 * started. Should be used to average parameters correctly when used with
 * RewardDelayedEnvironments. Should not be used with EpsidociCumulativ averagers.
 * @author twanschik
 */
public class DelayedDataAverager extends DataAverager {
    protected DataAverager dataAverager;
   
    protected int delay;
    protected int currentStep = 0;

    public DelayedDataAverager(DataAverager dataAverager, int delay, int maxSteps,
            String title) {
        super(maxSteps, title);
        this.dataAverager = dataAverager;
        this.delay = delay;
    }
    
    public void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean terminalState) {
        this.currentStep++;
        if (this.currentStep > this.delay) {
            this.dataAverager.update(state, action, nextState, nextAction, reward,
                    terminalState);
        }
    }

    public void updateNewEpisode(State initialState) {
        this.dataAverager.updateNewEpisode(initialState);
        this.currentStep = 0;
    }

    public void updateExperimentStop() {
        this.dataAverager.updateExperimentStop();
    }

    public void updateExperimentStart() {
        this.dataAverager.updateExperimentStart();
    }

    // getter and setter

    public Vector getDataArray() {
        return this.dataAverager.getDataArray();
    }

    public Vector getVarianceDataArray() {
        return this.dataAverager.getVarianceDataArray();
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void clearDataArrray() {
    }

    public void clearVarianceDataArrray() {
    }
    
    public DataAverager getDataAverager() {
		return dataAverager;
	}

	public void setDataAverager(DataAverager dataAverager) {
		this.dataAverager = dataAverager;
	}
}
