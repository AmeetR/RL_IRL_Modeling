package org.hswgt.teachingbox.core.rl.plot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.ScalarAverager;

/**
 * Helper class to plot an array of ScalarAverager data
 * @author tokicm
 *
 */
public class DataAveragePlotter extends FunctionPlotter2D implements Plotter {

    /**
     *
     */
    private static final long serialVersionUID = 4820972267981720531L;

    private ArrayList<ScalarAverager> scalarAveragers = new ArrayList<ScalarAverager>();
    private String plotTitle;

	private boolean plot_standardError;
	private int plot_standardError_xAxisInterval;
	private String textfile = null; 

    /**
     * The constructor
     * @param da The data averager
     * @param filename The output filename
     * @param plotTitle The plot title
     */
    public DataAveragePlotter(ScalarAverager da, String filename, String plotTitle) {
        this (filename, plotTitle);
        scalarAveragers.add(da);
    }

    /**
     * The constructor
     * @param filename The output filename
     * @param plotTitle The plot title
     */
    public DataAveragePlotter(String filename, String plotTitle) {
        super(plotTitle);

        this.filename = filename;
        this.plotTitle = plotTitle;
        this.plot_standardError = false;
    }

    /**
     * Add a ScalarAverager Object
     * @param da The ScalarAverager Object
     */
    public void addScalarAverager(ScalarAverager da) {
        scalarAveragers.add(da);
    }
        
    /**
     * Enable variance plotting with given x-axis interval
     * @param xAxisInterval The interval on the x-axis
     */
    public void setErrorPlotting(int xAxisInterval)
    {
    	this.plot_standardError = true;
    	this.plot_standardError_xAxisInterval = xAxisInterval;
    }
    
    
    /**
     * sets the plot title
     * @param plotTitle The plot title
     */
    public void setTitle(String plotTitle) {
    	this.title = this.plotTitle = plotTitle;
    }
    
    /**
     * Do not only plot to a window, but also write results to a textfile.
     * @param filename The name of the text file
     */
    public void setTextfileOutput (String filename) {
    	this.textfile = filename;
    }
    
    /**
     * Plot the graph
     */
    public void plotGraph() {

        ScalarAverager ra;
        double[] t;
        double[] meanArray;
        double[] stdErrorArray;
        
        //FunctionPlotter2D fp = new FunctionPlotter2D(this.plotTitle);
        //fp.setEpsFilename("../../" + this.filename + ".eps");
        ArrayList<FunctionPlotter2dData> dataFusion = new ArrayList<FunctionPlotter2dData>();

        for (int i=0; i<scalarAveragers.size(); i++) {
            ra = scalarAveragers.get(i);
            meanArray = new double[ra.getMaxSteps()];
           	stdErrorArray = new double[ra.getMaxSteps()];

            // time axis
            t = new double[ra.getMaxSteps()];

            for (int j=0; j<ra.getMaxSteps(); j++) {

                t[j] = j;
               
                meanArray[j] = ra.getDataArray().get(j);
                if(plot_standardError) {
                    stdErrorArray[j] = Math.sqrt(ra.getVarianceDataArray().get(j) / ra.getNumberOfSamples());
                }
            }
            
            // add the dataArray for plotting
            dataFusion.add(new FunctionPlotter2dData(new double[][]{t, meanArray}, ra.getConfigString()));
        	
            //dataFusion.add(new FunctionPlotter2dData(new double[][]{t, ra.getDataArray()}, ra.getConfigString()));
            if(plot_standardError) {
               	int num_bars = (int) Math.floor(ra.getMaxSteps()/this.plot_standardError_xAxisInterval);

               	double dataVarianceArray[] = new double[num_bars];
               	double varianceMinArray[] = new double[num_bars];
               	double varianceMaxArray[] = new double[num_bars];
               	double tVariance[] = new double[num_bars];
               	
               	// for each bar (every xAxisInterval)
               	for(int k = 0; k < num_bars; k++) {           

               		tVariance[k] = (double) (k*this.plot_standardError_xAxisInterval);
               		dataVarianceArray[k] = meanArray[k*this.plot_standardError_xAxisInterval];
               		
               		varianceMinArray[k] = dataVarianceArray[k] - stdErrorArray[k * this.plot_standardError_xAxisInterval];
                 	varianceMaxArray[k] = dataVarianceArray[k] + stdErrorArray[k * this.plot_standardError_xAxisInterval];               			
               	}
               	               	               		
            	String vType = new String ("Variance of ");

            	dataFusion.add(new FunctionPlotter2dData(new double[][]{tVariance, dataVarianceArray, varianceMinArray, varianceMaxArray}, vType + ra.getConfigString()));
            }

            //System.out.println (sum/1000.0);
        }
        
        if (this.textfile != null) {
        	try {
				File f = new File (this.textfile);
				FileWriter fw = new FileWriter (f);

				// write header
				fw.write("row");
				for (int d=0; d< this.scalarAveragers.size(); d++) {
		            ScalarAverager sa = scalarAveragers.get(d);
					fw.write(";mean of " + sa.getConfigString() + " avgs=" + sa.getNumberOfSamples());
					fw.write(";variance of " + sa.getConfigString());
					fw.write(";stdError of " + sa.getConfigString());
				}
				fw.write("\n");
				
				for (int row=0; row<this.scalarAveragers.get(0).getMaxSteps(); row++) {
					fw.write(row + " ");
					for (int d=0; d< this.scalarAveragers.size(); d++) {
			            ScalarAverager sa = scalarAveragers.get(d);
						fw.write(";" + sa.getDataArray().get(row));
						fw.write(";" + sa.getVarianceDataArray().get(row));
						fw.write(";" + Math.sqrt(sa.getVarianceDataArray().get(row)) / sa.getNumberOfSamples());
					}
					fw.write("\n");
				}
				
				fw.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }

        this.title = this.plotTitle;
        this.plotMData(dataFusion);
    }
   

    /**
     * test main function
     * @param args The command line arguments 
     * @throws Exception The exception
     */
    public static void main(String[] args) throws Exception
    {  
    	
    	final int maxSteps = 10;
    	
    	// create test ScalarAverager object with maxSteps steps per episode
    	ScalarAverager da = new ScalarAverager(maxSteps, "Testfunction");
    	
    	// fill data array with dummy values (reward values)
    	for (int i=0; i<maxSteps; i++) {
    		da.update(new State(0), new Action(0), new State(0), new Action(0), Math.random(), false);    		
    	}

    	DataAveragePlotter dataPlotter = new DataAveragePlotter(da, "data.gnuplot", "Plot Title");
    	dataPlotter.plotGraph();
    	
    	System.out.println("Please press any key ...");
    	System.in.read();
    }

	@Override
	public void plot() {
		plotGraph();		
	}
}
