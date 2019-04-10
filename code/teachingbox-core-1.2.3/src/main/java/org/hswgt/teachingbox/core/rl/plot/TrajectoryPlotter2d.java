package org.hswgt.teachingbox.core.rl.plot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import org.hswgt.teachingbox.core.rl.agent.AgentObserver;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver;
import org.hswgt.teachingbox.core.rl.plot.FunctionPlotter2D.PLOT_TYPE;

import cern.jet.random.Uniform;


/**
 * This class plots 2d trajectories. It can be attached to an @Experiment as an @Observer, for
 * visualizing learning process (see main() function example). 
 * 
 * @author Michel Tokic
 *
 */
public class TrajectoryPlotter2d implements Plotter, AgentObserver, ExperimentObserver, Serializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -8567084938637727482L;

    // The 2D function plotter
    private FunctionPlotter2D fp = new FunctionPlotter2D("TrajectoryPlotter");
	
	/**
	 * The list of States to be plotted from the current trajectory
	 */
	private LinkedList<State> currentTrajectory;
	private ArrayList<FunctionPlotter2dData> trajectories;
	
	private double minX = Double.POSITIVE_INFINITY;
	private double maxX = Double.NEGATIVE_INFINITY;
	private double minY = Double.POSITIVE_INFINITY;
	private double maxY = Double.NEGATIVE_INFINITY;
	
	private int xIndex = 0;
	private int yIndex = 1;
	
	
	/**
	 * The constructor
	 */
	public TrajectoryPlotter2d () {	
		this.currentTrajectory = new LinkedList<State>();
		this.trajectories = new ArrayList<FunctionPlotter2dData>();
		fp.setLabel("x-axis", "y-axis");
		fp.setPlotType(PLOT_TYPE.SCATTER);
	}
	
	/**
	 * This function sets the indices for the x- and y-dimeneions within a @State.
	 * @param xIndex The index of the x-dimension. Default: 0
	 * @param yIndex The index of the y-dimension. Default: 1
	 */
	public void setStateDimensionIndices (int xIndex, int yIndex) {
		this.xIndex = xIndex;
		this.yIndex = yIndex;
	}
	
	
	/**
	 * plot all trajectories
	 */
	@Override
	public synchronized void plot() {
		
		if (trajectories.size() > 0) {
	        fp.setRange(new double[]{minX, maxX}, new double[]{minY, maxY});
	        double xDiff = Math.abs((maxX-minX)/10.0);
	        double yDiff = Math.abs((maxY-minY)/10.0);
	        
	        //System.out.println ("minX=" + minX + ", maxX=" + maxX + ", minY=" + minY + ", maxY="+ maxY);
	        //System.out.println ("currentTrajectory.size=" + currentTrajectory.size());
	        //System.out.println ("trajectories.size=" + trajectories.size());
	        
			fp.setTics(	new double[]{minX, xDiff, maxX}, 
						new double[]{minY, yDiff, maxY});
	        fp.plotPolygons(trajectories);
		}
	}
	
	@Override
	public void update(State state, Action action, State arg2, Action arg3,
			double arg4, boolean arg5) {
		
		this.currentTrajectory.add(state);
		double x = state.get(xIndex);
		double y = state.get(yIndex);

		// update boundaries
    	if (x > maxX) { maxX = x; }
    	if (x < minX) { minX = x; }
    	if (y > maxY) { maxY = y; }
    	if (y < minY) { minY = y; }
	}
	
	/**
	 * This function puts the currentTrajectory to the set of trajectories to plot.
	 */
	private void memorizeTrajectory() {

		// memorize current trajectory if num states > 1 (first state is initial state)
		if (this.currentTrajectory.size() > 1) {
			double[] x = new double[currentTrajectory.size()];
	        double[] y = new double[currentTrajectory.size()];

	        State tmpState;
	        for (int i=0; i< currentTrajectory.size(); i++) {
	        	tmpState = currentTrajectory.get(i);
	        	x[i] = tmpState.get(xIndex);
	        	y[i] = tmpState.get(yIndex);
	        }
	        
	        // add trajectory
			this.trajectories.add (new FunctionPlotter2dData(new double[][]{x, y}, "" + trajectories.size()));
			// empty buffer
			this.currentTrajectory.clear();
			//System.out.println ("\nadding trajectory no. " + this.trajectories.size());
		}
	}

	@Override
	public void updateNewEpisode(State initState) {
		
		//System.out.println ("updateNewEpisode, currentTrajectory.size=" + this.currentTrajectory.size());
		// memorize current trajectory and create new one
		this.memorizeTrajectory();
		this.currentTrajectory.clear();
		this.currentTrajectory.add(initState);
	}

	@Override
	public void updateExperimentStart() {
	}

	@Override
	public void updateExperimentStop() {
		this.memorizeTrajectory();
		//this.plot();
	}
	
	/**
	 * this function clears all trajectories
	 */
	public void clearTrajectories() {

		// clear data
		this.currentTrajectory = new LinkedList<State>();
		this.trajectories = new ArrayList<FunctionPlotter2dData>();		
		
		minX = Double.POSITIVE_INFINITY;
		maxX = Double.NEGATIVE_INFINITY;
		minY = Double.POSITIVE_INFINITY;
		maxY = Double.NEGATIVE_INFINITY;
	}
	
	/**
	 * exports the current plot as PNG file 
	 * @param filename The filename. 
	 */
	public void exportPNG (String filename) {
		this.fp.exportPNG(filename);
	}

	/**
	 * sets the title for the plot
	 * @param title The title
	 */
	public void setTitle (String title) {
		this.fp.setTitle(title);
	}
	
	/**
	 * sets the X- and Y-axis labels
	 * @param xlabel The x-axis label
	 * @param ylabel The y-axis label
	 */
	public void setLabel (String xlabel, String ylabel) {
		this.fp.setLabel(xlabel, ylabel);
	}
	
	/**
	 * This function demonstrates the usage of TrajectoryPlotter. 
	 * @param args The command-line arguments
	 */
    public static void main(String args[]) {

    	// create TrajectoryPlotter object
    	TrajectoryPlotter2d tp = new TrajectoryPlotter2d();
    	tp.setTitle("Random walks starting from (0,0)");

    	tp.updateExperimentStart();
    	
    	// generate random walk trajectories
    	for (int episode=0; episode<3; episode++) {

    		double x=0; 
    		double y=0;
    		State initState = new State(new double[]{x, y});
        	
			tp.updateNewEpisode(initState);
			tp.plot();
			
			// add random states
			for (int j=0; j<50; j++) {
	    		
				x = x + Uniform.staticNextDoubleFromTo(-3, 3);
				y = y + Uniform.staticNextDoubleFromTo(-3, 3);
				State s = new State (new double[]{x, y});
	    		tp.update(s, null, null, null, 0, false);	
	    	}

			// plot current episode
    		System.out.println ("plotting episode " + episode);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	
    	tp.updateExperimentStop();
    	tp.plot();
    	tp.exportPNG("data/trajectories.png");
    }
}
