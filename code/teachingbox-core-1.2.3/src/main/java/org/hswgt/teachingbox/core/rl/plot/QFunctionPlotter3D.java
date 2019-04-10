/**
 *
 * $Id: QFunctionPlotter3D.java 1059 2016-10-15 13:57:42Z micheltokic $
 *
 * @version   $Rev: 1059 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2016-10-15 15:57:42 +0200 (Sat, 15 Oct 2016) $
 *
 */
package org.hswgt.teachingbox.core.rl.plot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import net.ericaro.surfaceplotter.Mapper;

import org.hswgt.teachingbox.core.rl.env.MountainCarEnv;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;

/**
 * @author Michel Tokic
 * This class plots a 3D Q-Function.
 */
public class QFunctionPlotter3D extends FunctionPlotter3D implements java.io.Serializable {
	
   	private static final long serialVersionUID = -2895306181059227463L;
	
	/* Q-function specific variables */
    protected QFunction Q;
    protected QFunction cachedQ;
    protected double costFactor = 1.0;

    /**
     * The constructor with the mandatory fields
     * @param Q The Q-Function Object
     */
    public QFunctionPlotter3D(final QFunction Q) {

    	super ("QFunction", Style.SURFACE);
    	
    	this.Q = Q;    	
    	this.cachedQ = Q;
    	
    	Mapper mapper = new Mapper() {
        	
			public synchronized float f1( float x, float y) {
				return (float)(cachedQ.getMaxValue(new State(new double[]{x,y}))*costFactor);
            }
			public synchronized float f2( float x, float y) { 
				return f1(x,y); 
			}	
		};

		this.setMapper(mapper);
    }
    
    /**
     * Plot the current Q funcion. 
     * 
     * A serialized object of the Q funcion is 
     * used because plotting is asynchronous to the learning process. 
     * Therefore weights can change during plotting, which could result in a 
     * plotted mixture of Q functions.
     */
    public void plot() {
    	
		ByteArrayOutputStream qCacheOut = new ByteArrayOutputStream();
    	ObjectSerializer.save(qCacheOut, Q);
    	ByteArrayInputStream qCacheIn = new ByteArrayInputStream(qCacheOut.toByteArray());
    	this.cachedQ = ObjectSerializer.load(qCacheIn);
    	
    	//this.model.setAutoScaleZ(true);    	
    	super.plot();
    	//this.model.setAutoScaleZ(false);    	
    }
    
    /**
     * sets whether the Q-Function is a cost function (inverts the sign)
     * @param costs A flag indicating if Q-value should be inverted or not
     */
    public void setCosts(boolean costs) {
    	if (costs) {
    		this.costFactor = -1.0;
    	} else {
    		this.costFactor = 1.0;
    	}
    }    

    /**
     * @param args The command-line arguments
     * @throws Exception An Exception
     */
    public static void main(String[] args) throws Exception
    {
    	System.out.println("Plotting mountaincar Q-function...");

    	// load the Q-function object
    	QFunction Q = ObjectSerializer.load("MC_Q_NRBF.ser"); 	
       
    	MountainCarEnv mEnv = new MountainCarEnv();

    	// initialize the QFunctionPlotter3D object
    	QFunctionPlotter3D qf = new QFunctionPlotter3D( Q );
    	qf.setBounds(	new double[]{mEnv.MIN_POS, mEnv.MAX_POS}, 
    					new double[]{mEnv.MIN_VEL, mEnv.MAX_VEL});
    	
    	qf.setFilename("mc-Q.gnuplot");
    	qf.setTitle("Mountain-Car Q-Function");
    	qf.setLabels("Position", "Velocity", "Costs");
    	qf.setCosts(true);
    	qf.plot();    	
    	qf.plot();    	
    }
}
