/**
 * 
 * $Id: EpisodicSaver.java 1066 2016-11-08 17:29:34Z micheltokic $
 * 
 * @version $Rev: 1066 $
 * @author $Author: micheltokic $
 * @date $Date: 2016-11-08 18:29:34 +0100 (Tue, 08 Nov 2016) $
 * 
 */

package org.hswgt.teachingbox.core.rl.experiment;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;

/**
 * The EpisodicSaver writes an object every n-episodes and every n-steps
 * to a stream. If one of there values are negative, then writing is
 * disabled for the respective event.
 */
public class EpisodicSaver implements ExperimentObserver, Serializable
{
    private static final long serialVersionUID = 466929308653467485L;

    protected Serializable object;

    protected String filename;

    protected int nSteps = -1;

    protected int nEpisodes = 1;

    protected int episodeCnt = 0;

    protected int stepCnt = 0;
    
    protected boolean appendEpisodeId = false;

	/**
	 * @param appendEpisodeId the appendEpisodeId to set
	 */
	public void setAppendEpisodeId(boolean appendEpisodeId) {
		this.appendEpisodeId = appendEpisodeId;
	}

	// Logger
    private final static Logger log4j = Logger.getLogger("EpisodicSaver");

    /**
     * The EpisodicSaver writes an object every n-episodes and every n-steps
     * to a stream. If one of there values are negative, then writing is
     * disabled for the respective event.
     * 
     * @param filename The file to save
     * @param object The object to save
     * @param nEpisodes Save object every $nEpisodes
     * @param nSteps Save object every $nSteps
     * @param appendEpisodeId Appends episode id to the filename
     */
    public EpisodicSaver(String filename, Serializable object,
            int nEpisodes, int nSteps, boolean appendEpisodeId)
    {
    	this(filename, object, nEpisodes, nSteps);
        this.appendEpisodeId = appendEpisodeId;
    }
    
    /**
     * The EpisodicSaver writes an object every n-episodes and every n-steps
     * to a stream. If one of there values are negative, then writing is
     * disabled for the respective event.
     * 
     * @param filename The file to save
     * @param object The object to save
     * @param nEpisodes Save object every $nEpisodes
     * @param nSteps Save object every $nSteps
     */
    public EpisodicSaver(String filename, Serializable object,
            int nEpisodes, int nSteps)
    {
        this.object = object;
        this.filename = new String(filename);
        this.nEpisodes = nEpisodes;
        this.nSteps = nSteps;
    }
    
    /**
     * The EpisodicSaver writes an object every n-episodes and every n-steps
     * to a stream. If one of there values are negative, then writing is
     * disabled for the respective event.
     * 
     * @param filename The file to save
     * @param object The object to save
     */
    public EpisodicSaver(String filename, Serializable object)
    {
        this(filename, object, 1);
    }

    /**
     * The EpisodicSaver writes an object every n-episodes and every n-steps
     * to a stream. If one of there values are negative, then writing is
     * disabled for the respective event.
     * 
     * @param filename The file to save
     * @param object The object to save
     * @param nEpisodes Save object every $nEpisodes
     */
    public EpisodicSaver(String filename, Serializable object, int nEpisodes)
    {
        this(filename, object, nEpisodes, -1);
    }
    
    /*
     * (non-Javadoc)
     * @see
     * org.hswgt.teachingbox.experiment.ExperimentObserver#update(org.hswgt.
     * teachingbox.env.State, org.hswgt.teachingbox.env.Action,
     * org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action,
     * double, boolean)
     */
    public void update(State s, Action a, State sn, Action an, double r,
            boolean terminalState)
    {
        stepCnt++;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.hswgt.teachingbox.experiment.ExperimentObserver#updateNewEpisode(
     * org.hswgt.teachingbox.env.State)
     */
    public void updateNewEpisode(State initialState)
    {
        stepCnt = 0;
        this.save();
        
        episodeCnt++;        
    }

    protected void finalize() throws Throwable
    {
      ObjectSerializer.save(filename, object);
      super.finalize();
    } 
    
    protected void save()
    {   
    	String fname = filename;
    	if (this.appendEpisodeId) {
    		fname += "_episode_" + this.episodeCnt;
    	}
    	
        if (nEpisodes > 0 && episodeCnt % nEpisodes == 0) {
        	log4j.info("saving object to " + fname + " (due to episodes)");
            ObjectSerializer.save(fname, object);
        } else if (nSteps > 0 && stepCnt % nSteps == 0) {
        	log4j.info("saving object  " + fname + " (due to steps)");
        	ObjectSerializer.save(fname, object);
        }
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateExperimentStart()
     */
    public void updateExperimentStart()
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateExperimentStop()
     */
    public void updateExperimentStop()
    {
     	this.save();        
    }
}
