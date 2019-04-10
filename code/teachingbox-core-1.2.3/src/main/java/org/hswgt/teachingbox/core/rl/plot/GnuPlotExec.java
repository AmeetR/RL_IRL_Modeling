/**
 *
 * $Id: GnuPlotExec.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.plot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.tools.PropertyLoader;

/**
 * Execute gnuplot commands.
 * 
 * gnuplotpath must be set in configuration.properties or
 * given as argument
 * 
 * Usage example: 
<pre>
    double[][] data = new double[][]{
        {56,45,75}.
        {47,12,58},
        {12,45,15},
        ...
    };

    GnuPlotExec gplot = new GnuPlotExec();
    gplot.addCommand("set title 'PoleSwingup Evaluation'");
    gplot.addCommand("set xlabel 'Angle'");
    gplot.addCommand("set ylabel 'Angular Velocity'");
    gplot.addCommand("set label 1 'Costs' center rotate by 90 at graph 0, graph 0, graph 0.5 offset -8");
    gplot.addCommand("set xrange [-pi:pi]");
    gplot.addCommand("set yrange [-2*pi:2*pi]");
    gplot.addCommand("set dgrid3d 30,30");
    gplot.addCommand("set pm3d");
    gplot.addCommand("set contour base");
    gplot.addCommand("splot '-' title 'Costs' with lines");
    gplot.addCommand(data);
    gplot.plot();
</pre>

 */
//@Deprecated
public class GnuPlotExec
{
	protected String path;
	protected String[] param;
	protected StringBuffer commands = new StringBuffer();
	private final static Logger log4j = Logger.getLogger("GnuPlotExec");
	protected Process proc = null;
	OutputStreamWriter cli = null;
	    
    /**
     * Constructor
     * gnuplotpath will be read from configuration.properties
     */
	public GnuPlotExec()
	{
	    this(PropertyLoader.loadProperties("configuration").getProperty("gnuplotpath"));
	}

	/**
	 * Constructor
	 * @param path Path to gnuplot executable
	 */
	public GnuPlotExec(String path){
		this(path, new String[] {});
	}
	
    /**
     * Constructor
     * gnuplotpath will be read from configuration.properties
     * @param param additional parameter for gnuplot executable
     */
	public GnuPlotExec(String[] param){
        this(PropertyLoader.loadProperties("configuration").getProperty("gnuplotpath"), param);
	}
	
	/**
	 * Constructor
	 * @param path Path to gnuplot executable
	 * @param param additional parameter for gnuplot executable
	 */
	public GnuPlotExec(String path, String[] param){
		this.path = path;
		this.param = param.clone();
	}
	
	/**
	 * Add a gnuplot command
	 * @param cmd The command to add
	 */
    public void addCommand(String cmd){
        commands.append(cmd+"\n");
    }
    
    /**
     * Append an array to the list of commands.
     * Use this to plot discrete data:
     * <pre>
     *  gplot.addCommand("splot '-' title 'Costs' with lines");
     *  gplot.addCommand(datarray);
     * </pre>
     * 
     * @param data The data to plot
     */
    public void addCommand(double[][] data)
    {
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < data.length; i++) {
            for(int j=0; j<data[i].length; j++ ){
                sb.append( data[i][j] );
                sb.append( " " );
            }
            sb.append("\n");
        }
        sb.append("e\n");
        
        addCommand(sb.toString());
    }
    
    /**
     * Append multiple plot data to the list of commands.
     * Use this to plot discrete data:
     * 
     * @param data The data to plot
     */
    public void addMData(double[][] data)
    {

    	StringBuffer sb = new StringBuffer();
        
    	int stepSize = data.length;
    	   	
        for (int i = 0; i < data.length; i=i+stepSize) {
        	for(int j=0; j<data[i].length; j++ ){
        		for(int k=0; k<stepSize; k++){
        			sb.append(data[i+k][j]);
        			if(k != stepSize - 1)
        				sb.append(" ");
        			else
        				sb.append("\n");
        		}
            }
            sb.append("e\n");
        }
        
        addCommand(sb.toString());
    }
    
    /**
     * Returns the full gnuplot command as string
     */
    public String toString() 
    {
        return commands.toString();
    }
    
    /**
     * Sends the complete command string to gnuplot
     * @throws Exception The Exception
     */
    public void plot() throws Exception 
    {
        plot(null);
    }
    
    /**
     * Sends the complete commandstring to gnuplot and save the
     * plot in the file specified by filename
     * @param filename The file to save the plot
     * @throws IOException The IOException
     */
    public void plot(String filename) throws IOException 
    {
        if( proc == null ){
            this.openInstance();
        }

        // printt debug string
        log4j.debug(commands.toString());
        
        // wirte command to gnuplot process
        // and flush stream
        cli.write(commands.toString());
        cli.flush();
        
        // if no filename specified try to load from properties
        if( filename == null )
            filename = PropertyLoader.loadProperties("configuration").getProperty("gnuplot_lastplot");
        
        // write to file
        if( filename != null )
        {
            try {
                Writer fw = new BufferedWriter(new FileWriter(filename));
                fw.write(commands.toString());
                fw.flush();
                fw.close();
            } catch (Exception e) {
                log4j.error("Could not save gnuplot to file: "+filename);
            }
        }
        
        commands = new StringBuffer();
    }
    
	protected void openInstance() throws IOException
	{
		if( proc != null ){
			return;
		}
		
		StringBuffer execstrs = new StringBuffer(path);
		for(int i=0; i<param.length; i++)
			execstrs.append( param[i] );
		
		log4j.debug(execstrs.toString());
		
		// get process
		proc = Runtime.getRuntime().exec(execstrs.toString());
		
        /* We utilize the current thread for gnuplot execution */
        cli = new OutputStreamWriter(proc.getOutputStream());
	}
	
	protected void closeInstance() throws IOException, InterruptedException 
	{
		if( proc == null ){
			return;
		}
		
		cli.write("quit");
        cli.flush();
        cli.close();
        
        proc.waitFor(); // wait for process to finish
        proc = null;
	}
	

}


