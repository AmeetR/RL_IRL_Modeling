/**
 *
 * $Id: DifferentialEquation.java 475 2009-12-15 09:10:57Z Markus Schneider $
 *
 * @version   $Rev: 475 $
 * @author    $Author: Markus Schneider $
 * @date      $Date: 2009-12-15 10:10:57 +0100 (Tue, 15 Dec 2009) $
 *
 */

package org.hswgt.teachingbox.core.rl.datastructures;

import java.io.Serializable;

/**
 * Interface for ordinary differential equations
 */
public interface DifferentialEquation extends Serializable
{    
    /**
     * A differential equation of the form <pre>dy = f(t, y)</pre>
     * @param t The time
     * @param y The value to derive
     * @param dy The memory for the result
     */
    public void derive(double t, double[] y, double[] dy);
}
