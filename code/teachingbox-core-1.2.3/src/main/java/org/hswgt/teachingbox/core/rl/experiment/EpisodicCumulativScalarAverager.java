package org.hswgt.teachingbox.core.rl.experiment;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

/**
 * This class averages episodic cumulative (double) data over multiple experiments. 
 * @author tokicm
 */
public class EpisodicCumulativScalarAverager extends ScalarAverager {
    private static final long serialVersionUID = -7888995921297521081L;

    protected double episodicData = 0.0;
    protected int experimentId = 0;
    protected int episodeId = 0;
    protected int maxEpisodes = 0;

    /**
     * The constructor.
     * @param maxEpisodes The maximum number of episodes 
     * @param configString The config string used as plot title
     */
    public EpisodicCumulativScalarAverager(int maxEpisodes, String configString) {
        // use maxEpisode - 1 because super will use the passed in parameter + 1
        // TODO: this should be fixed
        super(maxEpisodes - 1, configString);

        this.maxEpisodes = maxEpisodes;
    }

    /*
     * (non-Javadoc)
     * @see org.hswgt.teachingbox.core.rl.experiment.DataAverager#updateExperimentStart()
     */
    public void updateExperimentStart() {
        this.episodicData = 0.0;
        this.episodeId = 0;

//        System.out.println ("incrementing experiment id");
        this.experimentId++;
    }

    /*
     * (non-Javadoc)
     * @see org.hswgt.teachingbox.core.rl.experiment.DataAverager#update(org.hswgt.teachingbox.core.rl.env.State, org.hswgt.teachingbox.core.rl.env.Action, org.hswgt.teachingbox.core.rl.env.State, org.hswgt.teachingbox.core.rl.env.Action, double, boolean)
     */
    public void update(State s, Action a, State sn, Action an, double r,
        boolean terminalState) {

        // average the reward by default
        this.episodicData += r;

//        System.out.println ("cumulating reward " + (this.episodicData-r) + " + " + r + " = " + this.episodicData);
    }

    /*
     * (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateNewEpisode(org.hswgt.teachingbox.env.State)
     */
    public void updateNewEpisode(State initialState) {

//        System.out.println ("NEW episode ( " + this.episodeId + "/" + this.maxEpisodes + " ) started");

        // average data
        if (episodeId > 0) {
//            System.out.println ("updating data Arrays");
//            System.out.println ("episodeId=" + episodeId + ", experimentId=" + experimentId + ", episodicData=" + episodicData + ", dataArray[eId-1]=" + this.dataArray[episodeId-1]);
            double oldMean = this.dataArray.get(episodeId-1);
            this.dataArray.set(episodeId-1, this.dataArray.get(episodeId-1)
                    + (1.0/this.experimentId)
                    * (this.episodicData - this.dataArray.get(episodeId-1)));
        
            // calculate the variance, see Donald Knuth's Art of Computer Programming,
            // Vol 2, page 232, 3rd edition. Algorithm by by B. P. Welford

            // added small modification to the algorithm: we divide by the
            // number of samples in order to always store the variance, thus we
            // have to multiply it with the number of sample one step before so
            // we can update the variance correctly :)
            this.varianceDataArray.set(episodeId-1, this.varianceDataArray.get(episodeId-1)
                    *(this.experimentId-1));

            this.varianceDataArray.set(episodeId-1, this.varianceDataArray.get(episodeId-1)
                    + (this.episodicData - oldMean)
                    *(this.episodicData - this.dataArray.get(this.episodeId-1)));

            this.varianceDataArray.set(episodeId-1, this.varianceDataArray.get(episodeId-1)
                    / this.experimentId);
            
//            System.out.println ("dataArray[eId-1]=" + this.dataArray[episodeId-1]);
//            System.out.println ("varianceDataArray[eId-1]=" + Math.sqrt(this.varianceDataArray[episodeId-1]));
        }

        // increment episode id
        this.episodeId++;

        this.episodicData = 0.0;
    }

    /*
     * (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateExperimentStop()
     */
    public void updateExperimentStop() {
        // average last episode
        this.updateNewEpisode(new State(new double[]{0}));
    }

    // getter and setter

    /**
     * @return the maxEpisodes
     */
    public int getMaxEpisodes() {
        return maxEpisodes;
    }

    /**
     * @return the maxEpisodes
     */
    public int getMaxSteps() {
        return maxEpisodes;
    }

    public int getNumberOfSamples() {
        return this.experimentId;
    }

    public double getEpisodicData() {
        return this.episodicData;
    }

    public void setEpisodicData(double episodicData) {
        this.episodicData = episodicData;
    }
}
