/**
 * \file VFunction.java
 *
 * $Id: ValueFunction.java 988 2015-06-17 19:48:01Z micheltokic $
 * $URL: https://svn.code.sf.net/p/teachingbox/code/tags/teachingbox-core-1.2.3/src/main/java/org/hswgt/teachingbox/core/rl/valuefunctions/ValueFunction.java $
 
 * \version   $Rev: 988 $
 * \author    Markus Schneider
 * \date      Sep 3, 2008
 *
 */

package org.hswgt.teachingbox.core.rl.valuefunctions;

import org.hswgt.teachingbox.core.rl.env.State;


/**
 * Interface for a general ValueFunction
 */
public interface ValueFunction extends java.io.Serializable {
    /**
     * Return the value of a given state
     * @param state The state
     * @return The value of the state
     */
    public double getValue(final State state);
}
