/**
 *
 * $Id: ReplayMultiExperiments.java 731 2010-07-17 13:44:31Z twanschik $
 *
 * @version   $Rev: 731 $
 * @author    $Author: twanschik $
 * @date      $Date: 2010-07-17 15:44:31 +0200 (Sa, 17 Jul 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.experiment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * The ReplayMultiExperiments loads multiple data from experiments and "re-runs"
 * them. Doing so,we can use experiment observer to analyise data of already
 * run experiments i.e. to caculate the average reward per episode over multiple
 * experimental runs.
 */
public class ReplayMultiExperiments
{
    private final static Logger log4j = Logger.getLogger("ReplayMultiExperiments");
    protected List<ExperimentObserver> observers = new LinkedList<ExperimentObserver>();
    protected String[] filenames;
    protected ReplayExperiment currentExperiment = null;

    public ReplayMultiExperiments(String pathToFolder, final String fileType) {
        this(ReplayMultiExperiments.getFileNames(pathToFolder, fileType));
    }

    public ReplayMultiExperiments(String pathToFolder) {
        this(pathToFolder, "ser");
    }

    public ReplayMultiExperiments(String[] filenames) {
        this.filenames = filenames;
        if (this.filenames.length != 0)
            this.currentExperiment = new ReplayExperiment(filenames[0]);
    }

    public void run() {
        int numExp = 0;
        for (String filename: this.filenames) {
            numExp++;
            this.currentExperiment = new ReplayExperiment(filename);
            for (ExperimentObserver observer : this.observers)
                this.currentExperiment.addObserver(observer);
            log4j.info("New Experiment started: " + numExp + "/" + this.filenames.length);
            this.currentExperiment.run();
        }
    }

    /**
     * Attaches an observer to all experiment
     * @param obs The observer to attach
     */
    public void addObserver(ExperimentObserver obs) {
        log4j.info("New Observer added used for all experiments: "+obs.getClass());
        this.observers.add(obs);
    }

    // maxEpisodes and maxSteps shouldn't change over multiple experiments!
    // Otherwise you can't reuse observers which need  maxEpisodes or maxSteps.
    public int getMaxEpisodes() {
    	return this.currentExperiment.getMaxEpisodes();
    }

    public int getMaxSteps() {
    	return this.currentExperiment.getMaxSteps();
    }

    // helper functions
    private static String[] getFileNames(String pathToFolder, final String fileType) {
        File dir = new File(pathToFolder);
        String[] fileNames = null;
        if (dir.isDirectory()) {
             fileNames = dir.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith("." + fileType);
                }
            });

            for (int i=0; i<fileNames.length; i++) {
                String pathToConcat = pathToFolder;
                fileNames[i] = pathToConcat.concat(dir.separator).concat(
                        fileNames[i]);
            }
        }
        return fileNames;
    }
}

