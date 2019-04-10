/**
 *
 * $Id: ETraceType.java 475 2009-12-15 09:10:57Z Markus Schneider $
 *
 * @version   $Rev: 475 $
 * @author    $Author: Markus Schneider $
 * @date      $Date: 2009-12-15 10:10:57 +0100 (Tue, 15 Dec 2009) $
 *
 */

package org.hswgt.teachingbox.core.rl.etrace;

/**
 * The type of an eligibility trace
 * 
 * With accumulating etraces, revisiting the same state can
 * cause further increment also greater than 1. Replacing etraces
 * have their maximum at 1.
 * 
 * @see <a href="http://www.cs.ualberta.ca/%7Esutton/book/ebook/node72.html">http://www.cs.ualberta.ca/%7Esutton/book/ebook/node72.html</a>
 */
public enum ETraceType
{
    /** accumulating etrace */
    accumulating,
    /** replacing etrace */
    replacing,
    /** disable etracing */
    none
}
