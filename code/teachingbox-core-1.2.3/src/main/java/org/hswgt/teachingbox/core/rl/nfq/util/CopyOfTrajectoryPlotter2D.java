package org.hswgt.teachingbox.core.rl.nfq.util;

import java.io.Serializable;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.AgentObserver;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver;
import org.hswgt.teachingbox.core.rl.nfq.features.InputFeatures;
import org.hswgt.teachingbox.core.rl.nfq.features.type.NormalizedFeatures;
import org.hswgt.teachingbox.core.rl.plot.GnuPlotExec;
import org.hswgt.teachingbox.core.rl.plot.Plotter;

import cern.jet.random.Uniform;


/**
 * This class plots 2d trajectories. 
 * @author Michel Tokic
 *
 */
public class CopyOfTrajectoryPlotter2D implements Plotter, AgentObserver, ExperimentObserver, Serializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 6176966360923443111L;

	// Logger
    private final static Logger log4j = Logger.getLogger("TrajectoryPlotter2D");
	
    /**
     * The gnuplot object
     */
    GnuPlotExec gplot = new GnuPlotExec();
	
    /**
     * The feature object describing state bounds
     */
    InputFeatures features;
	
	/**
	 * The list of States to be plotted (Integer refers to a cluster ID, which is zero by default)
	 */
	LinkedList<State> states[];
	
	/**
	 * The list of lines to be plotted
	 */
	LinkedList<State> lines = new LinkedList<State>();

	/**
	 * The list of states to highlight using another plotting style,  e.g. for SOM cluster centers
	 */
	LinkedList<State> highlightStates = new LinkedList<State>();
	
	int episode = 0;
	int centers = 0;
	
	String title = null;
	State lastState = null;
	
	
	/**
	 * The constructor
	 * @param features The features
	 * @param centers The amount of centers
	 */
	public CopyOfTrajectoryPlotter2D (InputFeatures features, int centers) {	
		
		this.features = features;
		
		// initialize state array
		this.centers = centers;
		this.states = new LinkedList[centers];
		
		for (int i=0; i<centers; i++) {
			states[i] = new LinkedList<State>();
		}
	}
	
	/**
	 * The constructor
	 * @param features The features
	 */
	public CopyOfTrajectoryPlotter2D (InputFeatures features) {	
		this (features, 1);
	}
	
	/**
	 * adds a new State with default clusterID=0
	 * @param state The state to add
	 */
	public void addState (State state) {
		this.states[0].add(state);
	}
	
	/**
	 * adds a new State with cluster id
	 * @param state The position of State in cluster clusterID
	 * @param clusterID The id of the cluster
	 */
	public void addState (State state, int clusterID) {
		this.states[clusterID].add(state);
	}
	
	/**
	 * Sets the position of the cluster centroid
	 * @param state The position
	 */
	public void addHighlightState (State state) {
		this.highlightStates.add(state);
	}
	
	/**
	 * Adds a point to the list of lines to be plotted
	 * @param state The state
	 */
	public void addLinePoint (State state) {
		this.lines.add(state);
	}
	
	public void setPlotTitle (String title) {
		this.title = title;
	}
		
	
	/**
	 * clears the training data list
	 */
	public void clear() {
		for (int i=0; i<centers; i++) {
			this.states[i].clear();			
		}
		this.highlightStates.clear();
		this.lines.clear();
		

		lastState = null;
	}
	
	public void plot() {
		
		log4j.debug("plotting graph in episode " + episode);
        
		/********************
		 * ASSEMBLE PLOT DATA
		 ********************/                               
        String data = "";

        // ADD STATES
        for (int i=0; i<centers; i++) {
	        for (State s: states[i]) {
	        	data += s.get(0)  + " " + s.get(1) + " states" + i + "\n";
	        }
	        if (states[i].size() > 0) {
	        	data += "e\n\n";
	        }
        }
    	        
        
        // ADD LINES       
        if (lines.size() > 0) {
	        for (State s: lines) {
	        	data += s.get(0) + " " + s.get(1) + " " + "lines\n";
	        }
	        data += "e\n\n";
        }
    	
        // ADD highlight states (square box)
        for (State s : highlightStates) {
        	data += s.get(0)  + " " + s.get(1) + " center" + "\n";        			
        }
        data += "e\n\n";
   
		        
        /***************************
         * SEND COMMANDS TO GNUPLOT
         ***************************/
        String gCmd = "";
        gCmd += "set size ratio 1\n";
        gCmd += "unset label\n";		

        // labels for highlight states (number is the order the state was added to the list)
        for (int i=0; i<highlightStates.size(); i++) {
        	
        	gCmd += "set label '" + i + "' at " + highlightStates.get(i).get(0) + "," + highlightStates.get(i).get(1) + " front\n";
        }
       
		//gCmd += "set xrange [" + features.getStateDimensions(0)[0] + ":" + features.getStateDimensions(0)[1] + "]\n";
		//gCmd += "set yrange [" + features.getStateDimensions(1)[0] + ":" + features.getStateDimensions(1)[1] + "]\n";
        gCmd += "set pointsize 3\n";
        if (title != null) {
        	gCmd += "set title '" + title + "'\n";
        } else {
        	gCmd += "set title 'data in episode " + episode + "'\n";
        }
        gCmd += "set style line 1 lc rgb '#fff988' lt 1 lw 3 pt 7 ps 2\n";
        
        // add neuron names
        gCmd += "plot \\\n";
        // plot data points
        boolean flag = false;
        for (int i=0; i<centers; i++) {                        	
        	if (states[i].size() > 0) {
        		if (flag == true) {
        			gCmd += ",";
        		}
        		gCmd += "     '-' u 1:2 t '' w p pt 2 ps 1\\\n";
        		flag = true;
        	}
        }             
        gCmd += ",     '-' u 1:2 t '' w p pt 5 ps 2\\\n";
        
        // plot neuron topology
        if (lines.size() > 0) {
        	gCmd += ",     '-' t '' with linespoints ls 1\n";
        }
        gCmd += "\n"  + data + "\n\n";
       
        // PLOT DATA
        try {
        	
        	gplot.addCommand(gCmd);
        	gplot.plot();
		} catch (Exception e) {
			e.printStackTrace();
		}

		log4j.debug("plotting finished");
	}
	
	
	@Override
	public void update(State state, Action action, State arg2, Action arg3,
			double arg4, boolean arg5) {
		this.addState(state, 0);
		this.lastState = state;
	}

	@Override
	public void updateNewEpisode(State arg0) {
		// visualize last state
		if (this.lastState != null) {
			this.addHighlightState(lastState);
			lastState = null;
		}
	}

	@Override
	public void updateExperimentStart() {
	}

	@Override
	public void updateExperimentStop() {
		this.plot();
	}
	
	
	
	/**
	 * This function demonstrates the usage of TrajectoryPlotter. Two clusters with random data points are created and plotted. 
	 * @param args The command-line arguments
	 */
    public static void main(String args[]) {
    	
    	// create feature object with x \in (0,1), y \in (0,1)
    	InputFeatures features = new InputFeatures();
    	features.addStateFeatures(new NormalizedFeatures(0, 1));
    	features.addStateFeatures(new NormalizedFeatures(0, 1));
    	features.addActionFeatures(new NormalizedFeatures(-1, 1));
    	
    	// create SOM object
    	CopyOfTrajectoryPlotter2D tp = 
    			new CopyOfTrajectoryPlotter2D(features, 2);
    	
		// add 10 random states to cluster 0
		for (int j=0; j<10; j++) {
    		State s = new State (new double[]{	
    				Uniform.staticNextDoubleFromTo(0, 0.5), 
    				Uniform.staticNextDoubleFromTo(0, 0.5)});
    		tp.addState(s, 0);
    	}
		
		// add 10 random states to cluster 1
		for (int j=0; j<10; j++) {
    		State s = new State (new double[]{	
    				Uniform.staticNextDoubleFromTo(0.5, 1), 
    				Uniform.staticNextDoubleFromTo(0.5, 1)});
    		tp.addState(s, 1);
    	}
		
		// add cluster centers
		State sc1 = new State(new double[]{0.25, 0.25});
		State sc2 = new State(new double[]{0.75, 0.75});
		tp.addHighlightState(sc1);
		tp.addHighlightState(sc2);
		
		// connect cluster centers
		tp.addLinePoint(sc1);
		tp.addLinePoint(sc2);
		
    	tp.plot();
    	//tp.clear();
    }


}
