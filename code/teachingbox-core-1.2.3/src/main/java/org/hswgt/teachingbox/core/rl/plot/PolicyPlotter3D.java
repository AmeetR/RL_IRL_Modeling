/**
 *
 * $Id: PolicyPlotter3D.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.plot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import net.ericaro.surfaceplotter.Mapper;

import org.hswgt.teachingbox.core.rl.env.MountainCarEnv;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.policy.GreedyPolicy;
import org.hswgt.teachingbox.core.rl.policy.Policy;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;

/**
 * @author Michel Tokic
 * This class plots a 3D policy
 */
public class PolicyPlotter3D extends FunctionPlotter3D implements java.io.Serializable {
	
	private static final long serialVersionUID = -6374060499857117459L;
	
	/* Policy specific variables */
	protected Policy pi = null;
	protected Policy cachedPi = null;
	protected double[] boundaries = null;
	protected String filename = null;
	
    /**
     * The constructor with the mandatory fields
     * @param policy The policy
     */
    public PolicyPlotter3D(final Policy policy) {
    	
    	super("PolicyPlot ", Style.DENSITY);
    	
    	this.pi = policy;
    	this.cachedPi = policy;
    	
    	this.setMapper(new Mapper() {
			public synchronized float f1( float x, float y) {
				return (float)cachedPi.getBestAction(new State(new double[]{x,y})).get(0);
    	    }

			// required by surfaceplotter lib, but unused in our case
    	    public synchronized float f2( float x, float y) {
    	    	return f1(x,y);
    	    }	
    	});
    }
    
    /**
     * Plot the current policy. 
     * 
     * A serialized object of the policy is 
     * used because plotting is asynchronous to the learning process. 
     * Therefore weights can change during plotting, which could result in a 
     * plotted mixture of policy functions. 
     */
    public void plot() {
    	
		ByteArrayOutputStream piCacheOut = new ByteArrayOutputStream();
    	ObjectSerializer.save(piCacheOut, pi);
    	ByteArrayInputStream piCacheIn = new ByteArrayInputStream(piCacheOut.toByteArray());
    	cachedPi = ObjectSerializer.load(piCacheIn);
    	
    	super.plot();
    }
	
    /**
     * @param args The command-line arguments
     * @throws Exception An Exception
     */
    public static void main(String[] args) throws Exception
    {
    	System.out.println("Plotting mountaincar policy...");
    	QFunction Q = ObjectSerializer.load("MC_Q_NRBF.ser"); 	
       
        // setup policy
        GreedyPolicy pi = new GreedyPolicy(Q, MountainCarEnv.ACTION_SET);
        
        MountainCarEnv mEnv = new MountainCarEnv();
        
    	PolicyPlotter3D piPlot = new PolicyPlotter3D(pi);
    	piPlot.setBounds(	new double[]{mEnv.MIN_POS, mEnv.MAX_POS}, 
    						new double[]{mEnv.MIN_VEL, mEnv.MAX_VEL});
    	piPlot.setLabels("Position", "Velocity", "Costs");
    	piPlot.setTitle("Mountain-Car Policy");
    	piPlot.plot();
    }
}
