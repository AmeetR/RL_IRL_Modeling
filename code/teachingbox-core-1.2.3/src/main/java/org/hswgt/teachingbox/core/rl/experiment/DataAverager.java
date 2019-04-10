package org.hswgt.teachingbox.core.rl.experiment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.plot.Plotter;


/**
 * A class for averaging experiment data over episodes. 
 */
// TODO: should we variance calculation into a seperate observer (would be slower)?
public abstract class DataAverager<T, S> implements ExperimentObserver,
        java.io.Serializable, Plotter {
    private static final long serialVersionUID = -8186273065573597988L;

    // the current time step
    protected int t;

    // the maximum steps per episode
    protected int maxSteps;

    // the episode number
    protected int episode=0;
    
    // cumulative data
    protected double dataAccumulator=0.0;

    // the data array
    protected Vector<T> dataArray;
    protected Vector<S> varianceDataArray;
    protected String configString;
    
    protected String statFile;

    // Logger
    //Logger log4j = Logger.getLogger("RewardAverager");

    /**
     * The constructor
     * @param maxSteps the maximum steps per episode
     * @param configString the config string for plotting
     */
    public DataAverager(int maxSteps, String configString) {

        this.maxSteps = maxSteps;
        this.configString = configString;
        //log4j.info("logging rewards for '" + this.configString + "' with maxSteps=" + this.maxSteps);

        this.t = 0;
        this.episode = 1;
        this.initDataVectors(maxSteps);
    }

    /*
     * (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#update(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    // use this method to update averages
    public abstract void update(State state, Action action, State nextState,
            Action nextAction, double reward, boolean terminalState);

    
    /*
     * (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateExperimentStart()
     */
    public void updateExperimentStart() {
        // update episode and t only if last episode
        if (t > 0) {
            this.episode++;
            this.t = 0;
            this.dataAccumulator = 0.0;
            //log4j.debug("  starting new episode (no. " + episode + ")");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateExperimentStop()
     */
    public void updateExperimentStop() {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateNewEpisode(org.hswgt.teachingbox.env.State)
     */
    public void updateNewEpisode(State initialState) {

        // update episode and t only if last episode
        if (t > 0) {
            this.episode++;
            this.t = 0;
            this.dataAccumulator = 0.0;
            //log4j.debug("  starting new episode (no. " + episode + ")");
        }
    }

    // helper functions
    public void initDataVectors(int capacity) {
        this.dataArray = new Vector<T>(capacity+1);
        this.varianceDataArray = new Vector<S>(capacity + 1);
        this.dataArray.setSize(this.dataArray.capacity());
        // call set size to make initialization of the array possible
        this.varianceDataArray.setSize(this.varianceDataArray.capacity());
        this.clearDataArrray();
        this.clearVarianceDataArrray();
    }
    
    // getter and setter
    /**
     * @return the maxSteps
     */
    public int getMaxSteps() {
        return maxSteps;
    }

    /**
     * @return the episode
     */
    public int getEpisode() {
        return episode;
    }

    public int getNumberOfSamples() {
        return this.episode;
    }

    /**
     * @return the rewardArray
     */
    public Vector<T> getDataArray() {
        return dataArray;
    }

    /**
     * @return the maxEpisodes
     */
    public Vector<S> getVarianceDataArray() {
        return this.varianceDataArray;
    }

    // initialize arrays via the following methods
    public abstract void clearDataArrray();
    public abstract void clearVarianceDataArrray();

    /**
     * @return the configString
     */
    public String getConfigString() {
        return configString;
    }

    /**
     * sets the config string
     * @param configString The config string
     */
    public void setConfigString(String configString) {
        this.configString = configString;
    }
    
    public void setFilename (String filename) {
    	this.statFile = filename;
    }
    
    public void plot() {
    	 if (this.statFile != null) {
         	try {
 				File f = new File (this.statFile);
 				FileWriter fw = new FileWriter (f);

 				// write header
 				fw.write("row;mean;variance;samples\n");
 				
 				for (int row=0; row<dataArray.size(); row++) {
 					fw.write(row+";" + dataArray.get(row) + ";" + varianceDataArray.get(row) + ";" + this.getNumberOfSamples() + "\n");
 				}
 				fw.close(); 				
 			} catch (IOException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}         	
    	 }
    }
}
