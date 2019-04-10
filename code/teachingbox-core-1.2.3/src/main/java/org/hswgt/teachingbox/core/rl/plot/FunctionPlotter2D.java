/**
 *
 * $Id: FunctionPlotter2D.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPolygonAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

/**
 * @author tokicm
 * Interface for 2D function plotting
 *
 */
public class FunctionPlotter2D extends ApplicationFrame implements java.io.Serializable {
	
	private static final long serialVersionUID = 3961239899432144928L;

	protected String title = null;
	protected String filename = null;
	protected String xlabel=null, ylabel=null;
	protected double[] xrange=null, yrange=null;
	protected double[] xtics=null, ytics=null;
	
	XYSeriesCollection currentDataSet;

	protected int sizeX=1024;
	protected int sizeY=768;

	protected boolean visibility = true;
	
	JFreeChart chart;
	XYPlot plot;
	
	public enum PLOT_TYPE {LINES, SCATTER};
	private PLOT_TYPE plotType = PLOT_TYPE.SCATTER;
	
	/**
	 * constructor without title
	 */
	public FunctionPlotter2D() {
		super("notitle");
		this.title = "notitle";
		this.plotType = PLOT_TYPE.LINES;
		this.setSize(800,  600);
	}
    
	/**
	 * constructor with plot title 
	 * @param title The plot title
	 */
	public FunctionPlotter2D(String title) {
		super (title);
		this.title = title;
		this.plotType = PLOT_TYPE.LINES;
		this.setSize(800,  600);
	}
	
	public void setPlotType (PLOT_TYPE plotType) {
		this.plotType = plotType;
	}
	
	
	/**
	 * sets the visibility of the plot window (default: "true"). Set to false if only PNG-images or CSV-data should be exported without any GUI. 
	 * @param visibility The visibility of the window true/false
	 */
	public void setVisibility (boolean visibility) {
		this.visibility = visibility;
	}
	
	
	/**
	 * setup the axis-labels
	 * @param xlabel The x-axis label
	 * @param ylabel The y-axis label
	 */
	public void setLabel(String xlabel, String ylabel) {		
		this.xlabel = xlabel;
		this.ylabel = ylabel;
	} 
	
	/**
	 * setup the data ranges
	 * @param xrange The x-axis range [low, high]
	 * @param yrange The y-axis range [low, high]
	 */
	public void setRange(double[] xrange, double[] yrange) {
		if (xrange != null)
			this.xrange = xrange;
		if (yrange != null)
			this.yrange = yrange;
	}
	
	/**
	 * customization of the axis tics. Example xtics = {minimum=0, step=10, maximum=100} produces a tic every 10 steps {0, 10, 20, ... , 100}
	 * @param xtics The x-axis tics
	 * @param ytics The y-axis tics
	 */
	public void setTics(double[] xtics, double[] ytics) {		
		if (xtics != null)
			this.xtics = xtics;
		
		if (ytics != null)
			this.ytics = ytics;
	}
	
	/**
	 * sets the title for the plot
	 * @param title The plot title
	 */
	public void setTitle (String title) {
		this.title = title;
	}

    
	/**
	 * This function initializes the plot window given a dataset
	 * @param dataset The dataset to plot
	 */
    private void createChart(final XYDataset dataset) {
    	
    	// update size
    	this.setPreferredSize(this.getSize());
    	
        // create the chart...
    	if (this.plotType == PLOT_TYPE.LINES) {    		
	    	chart = ChartFactory.createXYLineChart(
	            title,      // chart title
	            this.xlabel,                      // x axis label
	            this.ylabel,                      // y axis label
	            dataset,                  // data
	            PlotOrientation.VERTICAL,
	            true,                     // include legend
	            true,                     // tooltips
	            false                     // urls
	        );
    	} else if (this.plotType == PLOT_TYPE.SCATTER) {
	    	chart = ChartFactory.createScatterPlot(
		            title,      // chart title
		            this.xlabel,                      // x axis label
		            this.ylabel,                      // y axis label
		            dataset,                  // data
		            PlotOrientation.VERTICAL,
		            true,                     // include legend
		            true,                     // tooltips
		            false                     // urls
		        );    		
    	}
        // NOW DO SOME CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.lightGray);

        //final StandardLegend legend = (StandardLegend) chart.getLegend();
        //legend.setDisplaySeriesShapes(true);
        
        // get a reference to the plot for further customization...
        plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.white);
        //plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);
        
        // set axis limits
    	ValueAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();
        
        if (this.xrange != null) {
            xAxis.setRange(xrange[0], xrange[1]);
        }
        if (this.yrange != null) {
            yAxis.setRange(yrange[0], yrange[1]);        	
        }

        // set tics
        if (this.xtics != null) {
        	TickUnits ticks = new TickUnits();
        	for (double i=xtics[0]; i<=xtics[2]; i+=xtics[1]) {
        		ticks.add(new NumberTickUnit(i));
        		//System.out.println ("adding xtic=" + i);
        	}
        	xAxis.setStandardTickUnits(ticks);
        } else {
        	xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        }
        if (this.ytics != null) {
        	TickUnits ticks = new TickUnits();
        	for (double i=ytics[0]; i<=ytics[2]; i+=ytics[1]) {
        		ticks.add(new NumberTickUnit(i));	
        		//System.out.println ("adding ytic=" + i);
        	}
        	yAxis.setStandardTickUnits(ticks);
        } else {
        	yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        }  
        
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        if (this.plotType == PLOT_TYPE.LINES) {
        	for (int i=0; i<dataset.getSeriesCount(); i++) {
                renderer.setSeriesLinesVisible(i, true);
                renderer.setSeriesShapesVisible(i, true);         	        		
        	}
        } else if (this.plotType == PLOT_TYPE.SCATTER) {
        	for (int i=0; i<dataset.getSeriesCount(); i++) {
                renderer.setSeriesLinesVisible(i, false);
                renderer.setSeriesShapesVisible(i, true);         	        		
        	}        	
        }
        
        plot.setRenderer(renderer);

		final ChartPanel chartPanel = new ChartPanel(chart);
		this.setContentPane(chartPanel);
        this.pack();
        //RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(visibility);
    }

	/**
	 * plots an double[x][y] array
	 * @param data The data array
	 */
	public synchronized void plotData (double[][] data) {
        	
		//final XYDataset dataset = createDataset();
		currentDataSet = new XYSeriesCollection();
        
		XYSeries series = new XYSeries(title);
		for (int i=0; i< data[0].length; i++) {
			series.add(data[0][i], data[1][i]);
		}

		currentDataSet.addSeries(series);
		createChart(currentDataSet);
	}
    
	/**
	 * Plotting function for multiple data.
	 * @param dataFusion The ArrayList with data
	 */
	public synchronized void plotMData (ArrayList<FunctionPlotter2dData> dataFusion) {
        
		currentDataSet = new XYSeriesCollection();
		
		// plot all curves
		for (FunctionPlotter2dData fp : dataFusion) {

			if (!fp.getTitle().startsWith("SD") && !fp.getTitle().startsWith("Variance of")) {
				XYSeries series = new XYSeries(fp.getTitle());
				double data[][] = fp.getData();
				
				for (int i=0; i< data[0].length; i++) {
					series.add(data[0][i], data[1][i]);
				}			
				currentDataSet.addSeries(series);				
			}
		}
		createChart(currentDataSet);
		
		// plot all SD and variances into created chart
		for (FunctionPlotter2dData fp : dataFusion) {
			if (fp.getTitle().startsWith("SD") || fp.getTitle().startsWith("Variance of")) {
				double data[][] = fp.getData(); 
				int polygons = data[0].length;
				
				for (int i=0; i<polygons; i++) {
					double[] edgesArr = {data[0][i], data[2][i], data[0][i], data[3][i]};
					plot.addAnnotation(new XYPolygonAnnotation(
								edgesArr, new BasicStroke(2.0f), Color.black, new Color(0,0,0,0))); 					
				}
			} 
		}
	}

	/**
	 * This function plots an array of polygons
	 * @param dataArray The array of FunctionPlotter2dData objects (trajectories to plot)
	 */
	public synchronized void plotPolygons (ArrayList<FunctionPlotter2dData> dataArray) {
        
		this.plotType = PLOT_TYPE.SCATTER;
		ArrayList<FunctionPlotter2dData> dataArrayCopy = (ArrayList<FunctionPlotter2dData>) dataArray.clone();
		plotMData(dataArrayCopy);

        for (int i=0; i<dataArrayCopy.size(); i++) {

        	FunctionPlotter2dData polygon = dataArrayCopy.get(i);
        	
			int edges = polygon.getData()[0].length;
			double pArray[][] = polygon.getData();
			double[] edgesArr = new double[edges*4];
            
			/**
			 * Because XYPolygonAnnotation connects the first and last point, 
			 * we need to draw the path forth and back.
			 */
            for(int edge=0; edge<edges; edge++) {

            	int a = (edge)*2; 		// for x value
            	int b = (edge)*2 + 1; 	// for y value
            	int c = ((edges)*4) - b -1;  // for x value
            	int d = ((edges)*4) - a -1;  // for y value
            	
            	edgesArr[a] = pArray[0][edge];
            	edgesArr[b] = pArray[1][edge];
            	edgesArr[c] = pArray[0][edge];
            	edgesArr[d] = pArray[1][edge];
            }
            plot.addAnnotation(new XYPolygonAnnotation(
            		edgesArr, new BasicStroke(0.5f), Color.black, new Color(0,0,0,0))); 
		}
	}
	
	// TODO: implement PDF export
	
	/**
	 * exports the current plot as PNG file 
	 * @param filename The filename. 
	 */
	public void exportPNG (String filename) {

	    File file = new File( filename ); 
	    try {
			ChartUtilities.saveChartAsPNG(file, chart, sizeX, sizeY);
		} catch (IOException e) {
			e.printStackTrace();
		}	    
	}
	
	/** 
	 * This function exports the current Timeseries-Object to a CSV file (for possible further processing).
	 * It is assumed that all time series share the same X-axis elements. 
	 * @param filename The filename
	 */
	public void exportCSV (String filename) {

	    PrintWriter out;
		try {
			out = new PrintWriter(filename);
		    out.print ("x");
		    
			// put header
			for (int i=0; i<this.currentDataSet.getSeriesCount(); i++) {
				String description = this.currentDataSet.getSeries(i).getDescription();
				if (description == null) {
					out.print(", COL_" + i);	
				} else {
					out.print(", " + description.replace(" ", "_"));
				}				
			}
			out.println();
			
			// put data
			for (int i=0; i< this.currentDataSet.getSeries(0).getItemCount(); i++) {
				out.print(this.currentDataSet.getSeries(0).getX(i).doubleValue());
					for (int j=0; j<this.currentDataSet.getSeriesCount(); j++) {
						out.print(", " + this.currentDataSet.getSeries(j).getY(i).doubleValue());	
				}
				out.println ();
			}
			
			out.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * A simple plotting example
     * @param args The command line arguments
     * @throws Exception The exception
     */
	public static void main(String[] args) throws Exception
    {
    	double[] dataX = new double[] {-5, 2, 3};
        double[] dataA = new double[] {1, 2, 3};
        double[] dataB = new double[] {2, 4, 6};
        double[] dataC = new double[] {3, 6, 50};
        
    	FunctionPlotter2D fp = new FunctionPlotter2D("Testplot");
    	
    	// define tics
        fp.setTics(new double[] {-7, 5, 4} , new double[] {-5, 5, 55});
        
        // range for x- and y-axis
        fp.setRange(new double[]{-7, 4}, new double[]{-5, 55});
        fp.setLabel("x-axis", "y-axis");
        
        // create data list
        ArrayList<FunctionPlotter2dData> dataFusion = new ArrayList<FunctionPlotter2dData>();
        dataFusion.add(new FunctionPlotter2dData(new double[][]{dataX, dataA}, new String("Function A")));
        dataFusion.add(new FunctionPlotter2dData(new double[][]{dataX, dataB}, new String("Function B")));
        dataFusion.add(new FunctionPlotter2dData(new double[][]{dataX, dataC}, new String("Function C")));

        // plot functions
        //fp.plotData(dataFusion.get(0).getData()); // first data set       
        fp.plotMData(dataFusion);        // entire data set

        fp.exportCSV("FunctionPlotter2D-test.csv");
    }
}
