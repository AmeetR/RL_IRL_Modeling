/**
 *
 * $Id: Plotter.java 1054 2016-10-05 20:29:44Z micheltokic $
 *
 * @version   $Rev: 1054 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2016-10-05 22:29:44 +0200 (Wed, 05 Oct 2016) $
 *
 */

package org.hswgt.teachingbox.core.rl.plot;

/**
 * Abstract base class for plotter
 */
public interface Plotter {
    
	/**
     * The method to create the plot
     */
    public abstract void plot();
}
