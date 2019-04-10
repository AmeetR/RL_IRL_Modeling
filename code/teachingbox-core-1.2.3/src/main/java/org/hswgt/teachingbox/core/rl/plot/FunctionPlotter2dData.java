package org.hswgt.teachingbox.core.rl.plot;


/**
 * The FunctionPlotter2d Data Object.
 * @author tokicm
 *
 */
public class FunctionPlotter2dData {
	
    
    private double[][] dData;
    private String title;
    
    /**
     * Constructor for double-array data
     * @param dData The double-array data
     * @param title The title
     */
    public FunctionPlotter2dData(double[][] dData, String title) {
    	this.dData = dData;
    	this.title = title;    	
    }
    
    /**
     * returns the data
     * @return The data array
     */
    public double[][] getData() {
    	return this.dData;    	
    }
    
    /**
     * returns the title
     * @return The title
     */
    public String getTitle() {
    	return this.title;
    }
}
